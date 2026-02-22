package org.bme.picture_viewer.service;

import lombok.RequiredArgsConstructor;
import org.bme.picture_viewer.dto.RegistrationDto;
import org.bme.picture_viewer.entity.AppUser;
import org.bme.picture_viewer.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AppUser register(RegistrationDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Ez a felhasználónév már foglalt: " + dto.getUsername());
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ez az e-mail cím már regisztrálva van.");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("A két jelszó nem egyezik.");
        }

        AppUser user = AppUser.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        return userRepository.save(user);
    }

    public AppUser findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Felhasználó nem található: " + username));
    }
}

