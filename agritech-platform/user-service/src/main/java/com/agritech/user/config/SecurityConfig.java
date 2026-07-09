package com.agritech.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the User Service.
 *
 * <p>All authentication and authorisation is delegated to the API Gateway, which
 * validates JWT tokens and injects the {@code X-User-Id} header before forwarding
 * requests to this service. Therefore, this service runs stateless with all
 * requests permitted at the Spring Security layer — gateway-level security is the
 * only enforcement needed.
 *
 * <p>CSRF protection is disabled because this is a stateless REST API with no
 * browser-based session state.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain to be fully stateless and permissive,
     * relying on the API Gateway for authentication enforcement.
     *
     * @param http the {@link HttpSecurity} builder
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/info",
                    "/v3/api-docs/**",
                    "/swagger-ui/**"
                ).permitAll()
                .anyRequest().permitAll()  // Gateway has already authenticated the caller
            );
        return http.build();
    }
}
