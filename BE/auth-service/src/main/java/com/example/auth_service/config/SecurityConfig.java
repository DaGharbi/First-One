package com.example.auth_service.config;

import com.example.auth_service.repository.AdminUserRepository;
import com.example.common.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository, AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
        return username -> userRepository.findByEmail(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                "ROLE_" + resolveRoleFromCodeProfile(adminUserRepository, user.getEmail())
                        ))
                        .accountExpired(false)
                        .accountLocked(false)
                        .credentialsExpired(false)
                        .disabled(false)
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private String resolveRoleFromCodeProfile(AdminUserRepository adminUserRepository, String username) {
        return adminUserRepository.findByNormalizedUsrMat(username)
                .map(user -> mapCodeProfileToRole(user.getCodeProfil()))
                .orElse("USER");
    }

    private String mapCodeProfileToRole(String codeProfile) {
        if (codeProfile == null) {
            return "USER";
        }

        return switch (codeProfile.trim().toUpperCase()) {
            case "ADM" -> "ADMIN";
            case "SEC" -> "SECURITY";
            case "RAG" -> "AGENT";
            case "RCC" -> "CC";
            default -> "USER";
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/devises/**", "/cde-monnaies/**", "/cde-vers-delta/**", "/actuator/**", "/error").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
