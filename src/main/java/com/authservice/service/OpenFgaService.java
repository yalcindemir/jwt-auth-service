package com.authservice.service;

import com.authservice.exception.OpenFgaException;
import com.authservice.exception.ResourceNotFoundException;
import com.authservice.model.OpenFgaAuthorization;
import com.authservice.model.User;
import com.authservice.repository.OpenFgaAuthorizationRepository;
import com.authservice.repository.UserRepository;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientCheckRequest;
import dev.openfga.sdk.api.client.model.ClientCheckResponse;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import dev.openfga.sdk.api.client.model.ClientWriteRequest;
import dev.openfga.sdk.api.client.model.ClientWriteTupleKey;
import dev.openfga.sdk.api.client.model.TupleKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenFgaService {

    private final OpenFgaClient openFgaClient;
    private final OpenFgaAuthorizationRepository openFgaAuthorizationRepository;
    private final UserRepository userRepository;

    /**
     * Yetkilendirme kontrolü yapar
     */
    public boolean checkAuthorization(String objectType, String objectId, String relation, String userId) {
        try {
            ClientCheckRequest checkRequest = new ClientCheckRequest();
            checkRequest.setUser(userId);
            checkRequest.setRelation(relation);
            checkRequest.setObject(objectType + ":" + objectId);

            ClientCheckResponse response = openFgaClient.check(checkRequest).get();
            return response.getAllowed();
        } catch (InterruptedException e) {
            log.error("OpenFGA yetkilendirme kontrolü kesintiye uğradı", e);
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            log.error("OpenFGA yetkilendirme kontrolü başarısız oldu", e);
            return false;
        }
    }

    /**
     * Yetkilendirme kaydı yapar
     */
    @Transactional
    public void addAuthorization(String objectType, String objectId, String relation, User user) {
        try {
            ClientWriteTupleKey writeTupleKey = new ClientWriteTupleKey();
            writeTupleKey.setUser(user.getId().toString());
            writeTupleKey.setRelation(relation);
            writeTupleKey.setObject(objectType + ":" + objectId);
            
            TupleKeys writes = new TupleKeys();
            writes.setTupleKeys(Collections.singletonList(writeTupleKey));

            ClientWriteRequest writeRequest = new ClientWriteRequest();
            writeRequest.setWrites(writes);

            openFgaClient.write(writeRequest).get();

            OpenFgaAuthorization authorization = OpenFgaAuthorization.builder()
                    .objectType(objectType)
                    .objectId(objectId)
                    .relation(relation)
                    .user(user)
                    .build();

            openFgaAuthorizationRepository.save(authorization);
        } catch (InterruptedException | ExecutionException e) {
            log.error("OpenFGA yetkilendirme ekleme hatası", e);
            throw new OpenFgaException("Yetkilendirme eklenirken hata oluştu", e);
        }
    }

    /**
     * Yetkilendirme kaydını kaldırır
     */
    @Transactional
    public void removeAuthorization(String objectType, String objectId, String relation, User user) {
        try {
            ClientTupleKey deleteTupleKey = new ClientTupleKey();
            deleteTupleKey.setUser(user.getId().toString());
            deleteTupleKey.setRelation(relation);
            deleteTupleKey.setObject(objectType + ":" + objectId);

            TupleKeys deletes = new TupleKeys();
            deletes.setTupleKeys(Collections.singletonList(deleteTupleKey));

            ClientWriteRequest writeRequest = new ClientWriteRequest();
            writeRequest.setDeletes(deletes);

            openFgaClient.write(writeRequest).get();

            openFgaAuthorizationRepository.findByObjectTypeAndObjectIdAndRelationAndUser(
                    objectType, objectId, relation, user
            ).ifPresent(openFgaAuthorizationRepository::delete);
        } catch (InterruptedException | ExecutionException e) {
            log.error("OpenFGA yetkilendirme kaldırma hatası", e);
            throw new OpenFgaException("Yetkilendirme kaldırılırken hata oluştu", e);
        }
    }

    /**
     * Kullanıcının tüm yetkilendirmelerini getirir
     */
    public List<OpenFgaAuthorization> getUserAuthorizations(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + userId));
        return openFgaAuthorizationRepository.findByUser(user);
    }

    /**
     * Nesne için tüm yetkilendirmeleri getirir
     */
    public List<OpenFgaAuthorization> getObjectAuthorizations(String objectType, String objectId) {
        return openFgaAuthorizationRepository.findByObjectTypeAndObjectId(objectType, objectId);
    }
}
