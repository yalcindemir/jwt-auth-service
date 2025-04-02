package com.authservice.service;

import com.authservice.model.User;
import com.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.Response;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KeycloakService keycloakService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;
    private Response mockResponse;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();

        mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(201);
    }

    @Test
    void registerUser_shouldCreateUserSuccessfully() {
        // Arrange
        when(keycloakService.createKeycloakUser(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockResponse);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = authenticationService.registerUser(
                testUser.getUsername(),
                testUser.getEmail(),
                testUser.getFirstName(),
                testUser.getLastName(),
                "password123"
        );

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(keycloakService).createKeycloakUser(
                testUser.getUsername(),
                testUser.getEmail(),
                testUser.getFirstName(),
                testUser.getLastName(),
                "password123"
        );
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findUserById_shouldReturnUserWhenExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        User result = authenticationService.findUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void findUserById_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.findUserById(userId);
        });

        assertTrue(exception.getMessage().contains("Kullanıcı bulunamadı"));
        verify(userRepository).findById(userId);
    }
}
