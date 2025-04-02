package com.authservice.controller;

import com.authservice.dto.AuthorizationRequest;
import com.authservice.model.OpenFgaAuthorization;
import com.authservice.model.User;
import com.authservice.service.AuthenticationService;
import com.authservice.service.OpenFgaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/authorization")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authorization", description = "Authorization API")
public class AuthorizationController {

    private final OpenFgaService openFgaService;
    private final AuthenticationService authenticationService;

    @PostMapping("/check")
    @Operation(summary = "Check authorization")
    public ResponseEntity<Boolean> checkAuthorization(@Valid @RequestBody AuthorizationRequest request) {
        boolean isAuthorized = openFgaService.checkAuthorization(
                request.getObjectType(),
                request.getObjectId(),
                request.getRelation(),
                request.getUserId().toString()
        );
        return ResponseEntity.ok(isAuthorized);
    }

    @PostMapping("/grant")
    @Operation(summary = "Grant authorization")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> grantAuthorization(@Valid @RequestBody AuthorizationRequest request) {
        User user = authenticationService.findUserById(request.getUserId());
        openFgaService.addAuthorization(
                request.getObjectType(),
                request.getObjectId(),
                request.getRelation(),
                user
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/revoke")
    @Operation(summary = "Revoke authorization")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> revokeAuthorization(@Valid @RequestBody AuthorizationRequest request) {
        User user = authenticationService.findUserById(request.getUserId());
        openFgaService.removeAuthorization(
                request.getObjectType(),
                request.getObjectId(),
                request.getRelation(),
                user
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user authorizations")
    public ResponseEntity<List<OpenFgaAuthorization>> getUserAuthorizations(@PathVariable UUID userId) {
        List<OpenFgaAuthorization> authorizations = openFgaService.getUserAuthorizations(userId);
        return ResponseEntity.ok(authorizations);
    }

    @GetMapping("/object")
    @Operation(summary = "Get object authorizations")
    public ResponseEntity<List<OpenFgaAuthorization>> getObjectAuthorizations(
            @RequestParam String objectType,
            @RequestParam String objectId) {
        List<OpenFgaAuthorization> authorizations = openFgaService.getObjectAuthorizations(objectType, objectId);
        return ResponseEntity.ok(authorizations);
    }
}
