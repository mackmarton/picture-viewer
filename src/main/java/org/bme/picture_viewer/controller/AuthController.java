package org.bme.picture_viewer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bme.picture_viewer.dto.RegistrationDto;
import org.bme.picture_viewer.service.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody RegistrationDto dto
    ) {
        userService.register(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Sikeres regisztráció!"));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(Map.of("username", authentication.getName()));
    }
}
