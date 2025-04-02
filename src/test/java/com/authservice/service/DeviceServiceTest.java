package com.authservice.service;

import com.authservice.model.Device;
import com.authservice.model.User;
import com.authservice.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    private User testUser;
    private Device testDevice;
    private String macAddress;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();

        macAddress = "00:1A:2B:3C:4D:5E";
        
        testDevice = Device.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .macAddress(macAddress)
                .deviceType("Mobile")
                .deviceName("Test Phone")
                .blocked(false)
                .lastLoginAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getUserDevices_shouldReturnUserDevices() {
        // Arrange
        List<Device> devices = Arrays.asList(testDevice);
        when(deviceRepository.findByUser(testUser)).thenReturn(devices);

        // Act
        List<Device> result = deviceService.getUserDevices(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDevice, result.get(0));
        verify(deviceRepository).findByUser(testUser);
    }

    @Test
    void isDeviceRegistered_shouldReturnTrueWhenDeviceExists() {
        // Arrange
        when(deviceRepository.existsByUserAndMacAddress(testUser, macAddress)).thenReturn(true);

        // Act
        boolean result = deviceService.isDeviceRegistered(testUser, macAddress);

        // Assert
        assertTrue(result);
        verify(deviceRepository).existsByUserAndMacAddress(testUser, macAddress);
    }

    @Test
    void isDeviceBlocked_shouldReturnTrueWhenDeviceIsBlocked() {
        // Arrange
        Device blockedDevice = testDevice;
        blockedDevice.setBlocked(true);
        when(deviceRepository.findByUserAndMacAddress(testUser, macAddress)).thenReturn(Optional.of(blockedDevice));

        // Act
        boolean result = deviceService.isDeviceBlocked(testUser, macAddress);

        // Assert
        assertTrue(result);
        verify(deviceRepository).findByUserAndMacAddress(testUser, macAddress);
    }

    @Test
    void registerOrUpdateDevice_shouldCreateNewDeviceWhenNotExists() {
        // Arrange
        when(deviceRepository.findByUserAndMacAddress(testUser, macAddress)).thenReturn(Optional.empty());
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);

        // Act
        Device result = deviceService.registerOrUpdateDevice(testUser, macAddress, "Mobile", "Test Phone");

        // Assert
        assertNotNull(result);
        assertEquals(testDevice, result);
        verify(deviceRepository).findByUserAndMacAddress(testUser, macAddress);
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void registerOrUpdateDevice_shouldUpdateExistingDevice() {
        // Arrange
        when(deviceRepository.findByUserAndMacAddress(testUser, macAddress)).thenReturn(Optional.of(testDevice));
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);

        // Act
        Device result = deviceService.registerOrUpdateDevice(testUser, macAddress, "Updated Type", "Updated Name");

        // Assert
        assertNotNull(result);
        assertEquals(testDevice, result);
        verify(deviceRepository).findByUserAndMacAddress(testUser, macAddress);
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void blockDevice_shouldBlockDevice() {
        // Arrange
        UUID deviceId = UUID.randomUUID();
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(testDevice));
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);

        // Act
        Device result = deviceService.blockDevice(deviceId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isBlocked());
        verify(deviceRepository).findById(deviceId);
        verify(deviceRepository).save(any(Device.class));
    }
}
