package org.bme.picture_viewer.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.bme.picture_viewer.service.AppUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AppUserDetailsService userDetailsService;

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static void writeJson(HttpServletResponse res, int status, String body) throws IOException {
        res.setStatus(status);
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(body);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .userDetailsService(userDetailsService)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/js/**", "/css/**", "/images/**").permitAll()
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers("/api/pictures", "/api/pictures/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler((req, res, auth) ->
                                writeJson(res, HttpStatus.OK.value(),
                                        "{\"username\":\"" + auth.getName() + "\"}"))
                        .failureHandler((req, res, ex) ->
                                writeJson(res, HttpStatus.UNAUTHORIZED.value(),
                                        "{\"error\":\"Hibás felhasználónév vagy jelszó.\"}"))
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((req, res, auth) ->
                                writeJson(res, HttpStatus.OK.value(),
                                        "{\"message\":\"Sikeres kijelentkezés.\"}"))
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, authEx) ->
                                writeJson(res, HttpStatus.UNAUTHORIZED.value(),
                                        "{\"error\":\"Bejelentkezés szükséges.\"}"))
                );

        return http.build();
    }
}
