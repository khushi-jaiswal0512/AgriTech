package com.agritech.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Base OpenAPI configuration for all microservices.
 * Defines the generic API metadata and the JWT security scheme.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Agritech Marketplace API",
                version = "1.0",
                description = "REST APIs for the Agritech Marketplace Platform"
        )
)
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {
}
