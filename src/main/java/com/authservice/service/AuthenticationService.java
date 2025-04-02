package com.authservice.service;

import com.authservice.exception.ResourceNotFoundException;
import com.authservice.model.Device;
import com.authservice.model.User;
import com.authservice.model.UserToken;
import com.authservice.repository.DeviceRepository;
import com.authservice.repository.UserRepository;
import com.authservice.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final KeycloakService keycloakService;
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final DeviceRepository deviceRepository;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    /**
     * Kullanıcı kaydı yapar
     */
    @Transactional
    public User registerUser(String username, String email, String firstName, String lastName, String password) {
        // Keycloak'ta kullanıcı oluştur
        var response = keycloakService.createKeycloakUser(username, email, firstName, lastName, password);
        
        if (response.getStatus() != 201) {
            throw new RuntimeException("Keycloak kullanıcı oluşturma hatası: " + response.getStatusInfo().getReasonPhrase());
        }
        
        // Veritabanında kullanıcı oluştur
        User user = User.builder()
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();
        
        return userRepository.save(user);
    }
    
    /**
     * Kullanıcıyı ID ile bulur
     */
    public User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
    }
    
    /**
     * Cihaz kaydı yapar
     */
    @Transactional
    public Device registerDevice(User user, String macAddress, String deviceType, String deviceName) {
        Optional<Device> existingDevice = deviceRepository.findByUserAndMacAddress(user, macAddress);
        
        if (existingDevice.isPresent()) {
            Device device = existingDevice.get();
            device.setDeviceType(deviceType);
            device.setDeviceName(deviceName);
            device.setLastLoginAt(LocalDateTime.now());
            return deviceRepository.save(device);
        }
        
        Device device = Device.builder()
                .user(user)
                .macAddress(macAddress)
                .deviceType(deviceType)
                .deviceName(deviceName)
                .blocked(false)
                .lastLoginAt(LocalDateTime.now())
                .build();
        
        return deviceRepository.save(device);
    }
    
    /**
     * Token kaydı yapar
     */
    @Transactional
    public UserToken saveUserToken(User user, String token, String refreshToken) {
        LocalDateTime expiryDate = LocalDateTime.now().plus(Duration.ofMillis(jwtExpiration));
        
        UserToken userToken = UserToken.builder()
                .user(user)
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresAt(expiryDate)
                .revoked(false)
                .build();
        
        return userTokenRepository.save(userToken);
    }
    
    /**
     * Cihazı engeller
     */
    @Transactional
    public Device blockDevice(UUID deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Cihaz bulunamadı"));
        
        device.setBlocked(true);
        return deviceRepository.save(device);
    }
    
    /**
     * Cihaz engelini kaldırır
     */
    @Transactional
    public Device unblockDevice(UUID deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Cihaz bulunamadı"));
        
        device.setBlocked(false);
        return deviceRepository.save(device);
    }
    
    /**
     * Token'ı geçersiz kılar
     */
    @Transactional
    public void revokeToken(String token) {
        UserToken userToken = userTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token bulunamadı"));
        
        userToken.setRevoked(true);
        userTokenRepository.save(userToken);
    }
}
