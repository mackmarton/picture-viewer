package org.bme.picture_viewer.service;

import lombok.RequiredArgsConstructor;
import org.bme.picture_viewer.entity.AppUser;
import org.bme.picture_viewer.repository.AppUserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Felhasználó nem található: " + username));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}

