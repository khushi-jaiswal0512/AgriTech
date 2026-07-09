package com.agritech.gateway.filter;

import com.agritech.common.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

/**
 * Global filter to validate JWTs at the API Gateway.
 * Ensures the gateway remains stateless and does not make blocking calls to Auth Service.
 */
@Slf4j
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    // Endpoints that do not require authentication
    private static final List<String> OPEN_ENDPOINTS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh",
            "/eureka",
            "/actuator"
    );

    public AuthenticationFilter(@Value("${jwt.secret}") String jwtSecret) {
        // Instantiate JwtUtil using the secret injected from Config Server
        this.jwtUtil = new JwtUtil(jwtSecret);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isSecured.test(request)) {
            if (!hasAuthorizationHeader(request)) {
                log.warn("Missing Authorization header for request: {}", request.getURI());
                return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = extractToken(request);

            if (token == null || !jwtUtil.validateToken(token)) {
                log.warn("Invalid JWT token for request: {}", request.getURI());
                return onError(exchange, "Invalid or expired Authorization token", HttpStatus.UNAUTHORIZED);
            }

            try {
                // Extract claims and mutate request to include them as headers
                String email = jwtUtil.extractEmail(token);
                Long userId = jwtUtil.extractUserId(token);
                String role = jwtUtil.extractRole(token);

                exchange = exchange.mutate()
                        .request(r -> r.header("X-User-Email", email)
                                .header("X-User-Id", String.valueOf(userId))
                                .header("X-User-Role", role))
                        .build();

            } catch (Exception e) {
                log.error("Failed to extract claims from JWT", e);
                return onError(exchange, "Failed to authorize request", HttpStatus.UNAUTHORIZED);
            }
        }

        return chain.filter(exchange);
    }

    private final Predicate<ServerHttpRequest> isSecured =
            request -> OPEN_ENDPOINTS.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

    private boolean hasAuthorizationHeader(ServerHttpRequest request) {
        return request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        // We return an empty response body here. In a real scenario, we could map to ApiResponse.
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // Execute before route filters
    }
}
