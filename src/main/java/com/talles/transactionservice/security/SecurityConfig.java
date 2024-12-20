package com.talles.transactionservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * Disable all securities to make easier for testing.
     * But in real scenario we would need some security like:
     * - Allow only HTTPS
     * - Use some method for authentication and authorization like OAuth2.
     * - For safety, disallow request to any URL not handled by this API.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF, allow all requests, and disable authentication
        http
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF protection
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // Allow all requests
                .formLogin(FormLoginConfigurer::disable)  // Disable form-based login
                .httpBasic(HttpBasicConfigurer::disable);  // Disable HTTP basic authentication

        return http.build();  // Return the configured HttpSecurity
    }

}
