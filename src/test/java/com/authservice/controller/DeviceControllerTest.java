package com.authservice.controller;

import com.authservice.dto.DeviceListResponse;
import com.authservice.dto.DeviceRegistrationRequest;
import com.authservice.model.Device;
import com.authservice.model.User;
import com.authservice.service.AuthenticationService;
import com.authservice.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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

@WebMvcTest(DeviceController.class)
public class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    @MockBean
    private AuthenticationService authenticationService;

    private User testUser;
    private Device testDevice;
    private UUID userId;
    private UUID deviceId;
    private String macAddress;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        deviceId = UUID.randomUUID();
        macAddress = "00:1A:2B:3C:4D:5E";
        
        testUser = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();
        
        testDevice = Device.builder()
                .id(deviceId)
                .user(testUser)
                .macAddress(macAddress)
                .deviceType("Mobile")
                .deviceName("Test Phone")
                .blocked(false)
                .lastLoginAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserDevices_shouldReturnUserDevices() throws Exception {
        // Arrange
        List<Device> devices = Arrays.asList(testDevice);
        when(authenticationService.findUserById(userId)).thenReturn(testUser);
        when(deviceService.getUserDevices(testUser)).thenReturn(devices);

        // Act & Assert
        mockMvc.perform(get("/api/devices/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.devices[0].macAddress").value(macAddress))
                .andExpect(jsonPath("$.devices[0].deviceType").value("Mobile"))
                .andExpect(jsonPath("$.devices[0].deviceName").value("Test Phone"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void registerDevice_shouldRegisterDevice() throws Exception {
        // Arrange
        when(authenticationService.findUserById(userId)).thenReturn(testUser);
        when(deviceService.registerOrUpdateDevice(eq(testUser), eq(macAddress), eq("Mobile"), eq("Test Phone")))
                .thenReturn(testDevice);

        // Act & Assert
        mockMvc.perform(post("/api/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"" + userId + "\",\"macAddress\":\"" + macAddress + "\",\"deviceType\":\"Mobile\",\"deviceName\":\"Test Phone\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.macAddress").value(macAddress))
                .andExpect(jsonPath("$.deviceType").value("Mobile"))
                .andExpect(jsonPath("$.deviceName").value("Test Phone"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void blockDevice_shouldBlockDevice() throws Exception {
        // Arrange
        testDevice.setBlocked(true);
        when(deviceService.blockDevice(deviceId)).thenReturn(testDevice);

        // Act & Assert
        mockMvc.perform(post("/api/devices/" + deviceId + "/block"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocked").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void unblockDevice_shouldUnblockDevice() throws Exception {
        // Arrange
        testDevice.setBlocked(false);
        when(deviceService.unblockDevice(deviceId)).thenReturn(testDevice);

        // Act & Assert
        mockMvc.perform(post("/api/devices/" + deviceId + "/unblock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocked").value(false));
    }
}
