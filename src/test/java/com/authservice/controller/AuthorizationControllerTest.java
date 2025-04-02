package com.authservice.controller;

import com.authservice.dto.AuthorizationRequest;
import com.authservice.model.OpenFgaAuthorization;
import com.authservice.model.User;
import com.authservice.service.AuthenticationService;
import com.authservice.service.OpenFgaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorizationController.class)
public class AuthorizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenFgaService openFgaService;

    @MockBean
    private AuthenticationService authenticationService;

    private User testUser;
    private UUID userId;
    private String objectType;
    private String objectId;
    private String relation;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();

        objectType = "document";
        objectId = "123";
        relation = "reader";
    }

    @Test
    @WithMockUser(roles = "USER")
    void checkAuthorization_shouldReturnAuthorizationStatus() throws Exception {
        // Arrange
        when(openFgaService.checkAuthorization(eq(objectType), eq(objectId), eq(relation), any(String.class)))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/authorization/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"" + userId + "\",\"objectType\":\"" + objectType + "\",\"objectId\":\"" + objectId + "\",\"relation\":\"" + relation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void grantAuthorization_shouldGrantAccess() throws Exception {
        // Arrange
        when(authenticationService.findUserById(userId)).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/authorization/grant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"" + userId + "\",\"objectType\":\"" + objectType + "\",\"objectId\":\"" + objectId + "\",\"relation\":\"" + relation + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserAuthorizations_shouldReturnUserAuthorizations() throws Exception {
        // Arrange
        OpenFgaAuthorization auth = new OpenFgaAuthorization();
        auth.setId(UUID.randomUUID());
        auth.setObjectType(objectType);
        auth.setObjectId(objectId);
        auth.setRelation(relation);
        auth.setUser(testUser);

        List<OpenFgaAuthorization> authorizations = Arrays.asList(auth);
        when(openFgaService.getUserAuthorizations(userId)).thenReturn(authorizations);

        // Act & Assert
        mockMvc.perform(get("/api/authorization/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].objectType").value(objectType))
                .andExpect(jsonPath("$[0].objectId").value(objectId))
                .andExpect(jsonPath("$[0].relation").value(relation));
    }
}
