package com.authservice.repository;

import com.authservice.model.Device;
import com.authservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DeviceRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Device testDevice;
    private String macAddress = "00:1A:2B:3C:4D:5E";

    @BeforeEach
    void setUp() {
        // Kullanıcı oluştur
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testUser = userRepository.save(testUser);

        // Cihaz oluştur
        testDevice = Device.builder()
                .user(testUser)
                .macAddress(macAddress)
                .deviceType("Mobile")
                .deviceName("Test Phone")
                .blocked(false)
                .lastLoginAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testDevice = deviceRepository.save(testDevice);
    }

    @Test
    void findByUser_shouldReturnDevices_whenUserHasDevices() {
        // Act
        List<Device> devices = deviceRepository.findByUser(testUser);

        // Assert
        assertFalse(devices.isEmpty());
        assertEquals(1, devices.size());
        assertEquals(macAddress, devices.get(0).getMacAddress());
    }

    @Test
    void findByUserAndMacAddress_shouldReturnDevice_whenExists() {
        // Act
        Optional<Device> device = deviceRepository.findByUserAndMacAddress(testUser, macAddress);

        // Assert
        assertTrue(device.isPresent());
        assertEquals(macAddress, device.get().getMacAddress());
        assertEquals("Mobile", device.get().getDeviceType());
        assertEquals("Test Phone", device.get().getDeviceName());
    }

    @Test
    void existsByUserAndMacAddress_shouldReturnTrue_whenDeviceExists() {
        // Act
        boolean exists = deviceRepository.existsByUserAndMacAddress(testUser, macAddress);

        // Assert
        assertTrue(exists);
    }

    @Test
    void findByUserAndBlocked_shouldReturnDevices_whenMatchingCriteria() {
        // Arrange
        Device blockedDevice = Device.builder()
                .user(testUser)
                .macAddress("AA:BB:CC:DD:EE:FF")
                .deviceType("Tablet")
                .deviceName("Blocked Tablet")
                .blocked(true)
                .lastLoginAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        deviceRepository.save(blockedDevice);

        // Act
        List<Device> blockedDevices = deviceRepository.findByUserAndBlocked(testUser, true);
        List<Device> unblockedDevices = deviceRepository.findByUserAndBlocked(testUser, false);

        // Assert
        assertEquals(1, blockedDevices.size());
        assertEquals("Blocked Tablet", blockedDevices.get(0).getDeviceName());
        
        assertEquals(1, unblockedDevices.size());
        assertEquals("Test Phone", unblockedDevices.get(0).getDeviceName());
    }

    @Test
    void findByMacAddress_shouldReturnDevices_whenMacAddressMatches() {
        // Act
        List<Device> devices = deviceRepository.findByMacAddress(macAddress);

        // Assert
        assertFalse(devices.isEmpty());
        assertEquals(1, devices.size());
        assertEquals(macAddress, devices.get(0).getMacAddress());
    }
}
