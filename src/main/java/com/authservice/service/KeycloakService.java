package com.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

    private final Keycloak keycloakClient;

    @Value("${keycloak.realm}")
    private String realm;

    /**
     * Keycloak'ta yeni kullanıcı oluşturur
     */
    public Response createKeycloakUser(String username, String email, String firstName, String lastName, String password) {
        CredentialRepresentation credential = createPasswordCredential(password);
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setCredentials(Collections.singletonList(credential));
        user.setEnabled(true);

        RealmResource realmResource = keycloakClient.realm(realm);
        UsersResource usersResource = realmResource.users();

        return usersResource.create(user);
    }

    /**
     * Keycloak'ta kullanıcı arar
     */
    public List<UserRepresentation> searchUserByUsername(String username) {
        return keycloakClient.realm(realm).users().search(username);
    }

    /**
     * Keycloak'ta kullanıcı siler
     */
    public void deleteKeycloakUser(String userId) {
        keycloakClient.realm(realm).users().delete(userId);
    }

    /**
     * Keycloak'ta kullanıcı şifresini günceller
     */
    public void updatePassword(String userId, String newPassword) {
        CredentialRepresentation credential = createPasswordCredential(newPassword);
        keycloakClient.realm(realm).users().get(userId).resetPassword(credential);
    }

    /**
     * Şifre kimlik bilgisi oluşturur
     */
    private CredentialRepresentation createPasswordCredential(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        return credential;
    }
}
