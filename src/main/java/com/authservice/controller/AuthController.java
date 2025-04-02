package com.authservice.controller;

import com.authservice.dto.AuthenticationRequest;
import com.authservice.dto.AuthenticationResponse;
import com.authservice.dto.DeviceRegistrationRequest;
import com.authservice.dto.UserRegistrationRequest;
import com.authservice.model.Device;
import com.authservice.model.User;
import com.authservice.service.AuthenticationService;
import com.authservice.service.KeycloakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final KeycloakService keycloakService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        User user = authenticationService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPassword()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/device")
    @Operation(summary = "Register a device")
    public ResponseEntity<Device> registerDevice(@Valid @RequestBody DeviceRegistrationRequest request) {
        User user = authenticationService.findUserById(request.getUserId());
        Device device = authenticationService.registerDevice(
                user,
                request.getMacAddress(),
                request.getDeviceType(),
                request.getDeviceName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(device);
    }

    @PostMapping("/device/{deviceId}/block")
    @Operation(summary = "Block a device")
    public ResponseEntity<Device> blockDevice(@PathVariable UUID deviceId) {
        Device device = authenticationService.blockDevice(deviceId);
        return ResponseEntity.ok(device);
    }

    @PostMapping("/device/{deviceId}/unblock")
    @Operation(summary = "Unblock a device")
    public ResponseEntity<Device> unblockDevice(@PathVariable UUID deviceId) {
        Device device = authenticationService.unblockDevice(deviceId);
        return ResponseEntity.ok(device);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and revoke token")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        authenticationService.revokeToken(token);
        return ResponseEntity.ok().build();
    }
}
