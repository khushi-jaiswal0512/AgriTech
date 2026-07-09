package com.agritech.auth.config;

import com.agritech.common.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWT configuration class.
 *
 * <p>Instantiates {@link JwtUtil} from {@code agritech-common} as a Spring-managed
 * bean, wiring in the JWT secret sourced from Config Server. This allows the secret
 * to be injected at runtime without hard-coding it in source files.
 */
@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Creates a {@link JwtUtil} bean configured with the application JWT secret.
     *
     * @return a fully initialized JwtUtil instance
     */
    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtSecret);
    }
}
