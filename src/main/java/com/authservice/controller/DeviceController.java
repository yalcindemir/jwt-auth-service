package com.authservice.controller;

import com.authservice.dto.DeviceListResponse;
import com.authservice.dto.DeviceRegistrationRequest;
import com.authservice.model.Device;
import com.authservice.model.User;
import com.authservice.service.AuthenticationService;
import com.authservice.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Device Management", description = "Device Management API")
public class DeviceController {

    private final DeviceService deviceService;
    private final AuthenticationService authenticationService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user devices")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<DeviceListResponse> getUserDevices(@PathVariable UUID userId) {
        User user = authenticationService.findUserById(userId);
        List<Device> devices = deviceService.getUserDevices(user);
        
        DeviceListResponse response = new DeviceListResponse(
                devices.stream().collect(Collectors.toList())
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Register a device")
    public ResponseEntity<Device> registerDevice(@Valid @RequestBody DeviceRegistrationRequest request) {
        User user = authenticationService.findUserById(request.getUserId());
        Device device = deviceService.registerOrUpdateDevice(
                user,
                request.getMacAddress(),
                request.getDeviceType(),
                request.getDeviceName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(device);
    }

    @PostMapping("/{deviceId}/block")
    @Operation(summary = "Block a device")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Device> blockDevice(@PathVariable UUID deviceId) {
        Device device = deviceService.blockDevice(deviceId);
        return ResponseEntity.ok(device);
    }

    @PostMapping("/{deviceId}/unblock")
    @Operation(summary = "Unblock a device")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Device> unblockDevice(@PathVariable UUID deviceId) {
        Device device = deviceService.unblockDevice(deviceId);
        return ResponseEntity.ok(device);
    }

    @GetMapping("/search")
    @Operation(summary = "Search devices by MAC address")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceListResponse> searchDevicesByMacAddress(@RequestParam String macAddress) {
        List<Device> devices = deviceService.findDevicesByMacAddress(macAddress);
        
        DeviceListResponse response = new DeviceListResponse(devices);
        
        return ResponseEntity.ok(response);
    }
}
