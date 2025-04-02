package com.authservice.service;

import com.authservice.model.OpenFgaAuthorization;
import com.authservice.model.User;
import com.authservice.repository.OpenFgaAuthorizationRepository;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.CheckRequest;
import dev.openfga.sdk.api.client.model.TupleKey;
import dev.openfga.sdk.api.client.model.WriteRequest;
import dev.openfga.sdk.api.client.model.WriteRequestTupleKey;
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

    /**
     * Yetkilendirme kontrolü yapar
     */
    public boolean checkAuthorization(String objectType, String objectId, String relation, String userId) {
        try {
            CheckRequest checkRequest = new CheckRequest()
                    .user(userId)
                    .relation(relation)
                    .object(objectType + ":" + objectId);

            var response = openFgaClient.check(checkRequest).get();
            return response.getAllowed();
        } catch (InterruptedException | ExecutionException e) {
            log.error("OpenFGA yetkilendirme kontrolü hatası", e);
            return false;
        }
    }

    /**
     * Yetkilendirme kaydı yapar
     */
    @Transactional
    public void addAuthorization(String objectType, String objectId, String relation, User user) {
        try {
            // OpenFGA'ya yetkilendirme kaydı ekle
            WriteRequestTupleKey tupleKey = new WriteRequestTupleKey()
                    .user(user.getId().toString())
                    .relation(relation)
                    .object(objectType + ":" + objectId);

            WriteRequest writeRequest = new WriteRequest()
                    .writes(Collections.singletonList(tupleKey));

            openFgaClient.write(writeRequest).get();

            // Veritabanına yetkilendirme kaydı ekle
            OpenFgaAuthorization authorization = OpenFgaAuthorization.builder()
                    .objectType(objectType)
                    .objectId(objectId)
                    .relation(relation)
                    .user(user)
                    .build();

            openFgaAuthorizationRepository.save(authorization);
        } catch (InterruptedException | ExecutionException e) {
            log.error("OpenFGA yetkilendirme ekleme hatası", e);
            throw new RuntimeException("Yetkilendirme eklenirken hata oluştu", e);
        }
    }

    /**
     * Yetkilendirme kaydını kaldırır
     */
    @Transactional
    public void removeAuthorization(String objectType, String objectId, String relation, User user) {
        try {
            // OpenFGA'dan yetkilendirme kaydını kaldır
            TupleKey tupleKey = new TupleKey()
                    .user(user.getId().toString())
                    .relation(relation)
                    .object(objectType + ":" + objectId);

            WriteRequest writeRequest = new WriteRequest()
                    .deletes(Collections.singletonList(tupleKey));

            openFgaClient.write(writeRequest).get();

            // Veritabanından yetkilendirme kaydını kaldır
            openFgaAuthorizationRepository.findByObjectTypeAndObjectIdAndRelationAndUser(
                    objectType, objectId, relation, user
            ).ifPresent(openFgaAuthorizationRepository::delete);
        } catch (InterruptedException | ExecutionException e) {
            log.error("OpenFGA yetkilendirme kaldırma hatası", e);
            throw new RuntimeException("Yetkilendirme kaldırılırken hata oluştu", e);
        }
    }

    /**
     * Kullanıcının tüm yetkilendirmelerini getirir
     */
    public List<OpenFgaAuthorization> getUserAuthorizations(UUID userId) {
        User user = new User();
        user.setId(userId);
        return openFgaAuthorizationRepository.findByUser(user);
    }

    /**
     * Nesne için tüm yetkilendirmeleri getirir
     */
    public List<OpenFgaAuthorization> getObjectAuthorizations(String objectType, String objectId) {
        return openFgaAuthorizationRepository.findByObjectTypeAndObjectId(objectType, objectId);
    }
}
