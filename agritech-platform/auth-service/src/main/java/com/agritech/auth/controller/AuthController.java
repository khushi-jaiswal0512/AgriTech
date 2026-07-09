package com.agritech.auth.controller;

import com.agritech.auth.dto.request.LoginRequest;
import com.agritech.auth.dto.request.RefreshTokenRequest;
import com.agritech.auth.dto.request.RegisterRequest;
import com.agritech.auth.dto.response.AuthResponse;
import com.agritech.auth.dto.response.UserInfoResponse;
import com.agritech.auth.service.AuthService;
import com.agritech.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing authentication endpoints for the Agritech platform.
 *
 * <p>All endpoints under {@code /api/auth/**} are publicly accessible.
 * The API Gateway injects the {@code X-User-Email} header after validating
 * the JWT for the {@code /me} endpoint.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration, login, token refresh, and logout")
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user account and returns a JWT token pair.
     *
     * @param request the validated registration payload
     * @return 201 Created with access + refresh tokens
     */
    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a user account, publishes a Kafka event for profile creation, and returns JWT tokens."
    )
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("POST /api/auth/register — email={}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    /**
     * Authenticates a user and returns a JWT token pair.
     *
     * @param request the login credentials
     * @return 200 OK with access + refresh tokens
     */
    @PostMapping("/login")
    @Operation(
        summary = "Login",
        description = "Validates email and password, then returns access and refresh tokens."
    )
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("POST /api/auth/login — email={}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    /**
     * Issues a new access token by rotating the provided refresh token.
     *
     * @param request the refresh token payload
     * @return 200 OK with a new access token and rotated refresh token
     */
    @PostMapping("/refresh-token")
    @Operation(
        summary = "Refresh access token",
        description = "Validates the refresh token, revokes it, and returns a fresh token pair."
    )
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    /**
     * Revokes the provided refresh token, effectively logging the user out.
     *
     * @param request the refresh token to revoke
     * @return 200 OK with no data payload
     */
    @PostMapping("/logout")
    @Operation(
        summary = "Logout",
        description = "Revokes the refresh token. The access token expires naturally."
    )
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    /**
     * Returns info about the currently authenticated user.
     *
     * <p>The API Gateway validates the JWT and injects the email via
     * the {@code X-User-Email} header before forwarding the request.
     *
     * @param email the user's email injected by the API Gateway
     * @return 200 OK with user info
     */
    @GetMapping("/me")
    @Operation(
        summary = "Get current user info",
        description = "Returns id, email, role, active, verified, and createdAt for the authenticated user."
    )
    public ResponseEntity<ApiResponse<UserInfoResponse>> getCurrentUser(
            @Parameter(description = "User email injected by API Gateway from JWT claims")
            @RequestHeader("X-User-Email") String email) {

        UserInfoResponse response = authService.getCurrentUser(email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
