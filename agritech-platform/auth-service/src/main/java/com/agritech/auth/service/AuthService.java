package com.agritech.auth.service;

import com.agritech.auth.dto.request.LoginRequest;
import com.agritech.auth.dto.request.RefreshTokenRequest;
import com.agritech.auth.dto.request.RegisterRequest;
import com.agritech.auth.dto.response.AuthResponse;
import com.agritech.auth.dto.response.UserInfoResponse;

/**
 * Auth service interface defining core authentication operations.
 *
 * <p>Implementations handle user registration, credential verification,
 * JWT issuance, refresh token rotation, and logout.
 */
public interface AuthService {

    /**
     * Registers a new user and returns authentication tokens.
     *
     * @param request the registration details
     * @return auth response with access and refresh tokens
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates a user by email and password.
     *
     * @param request the login credentials
     * @return auth response with access and refresh tokens
     */
    AuthResponse login(LoginRequest request);

    /**
     * Rotates the refresh token and returns a new access token.
     *
     * @param request the refresh token request
     * @return auth response with new tokens
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * Revokes the provided refresh token (logout).
     *
     * @param refreshToken the refresh token to revoke
     */
    void logout(String refreshToken);

    /**
     * Retrieves info about the currently authenticated user.
     *
     * @param email the email extracted from the JWT by the API Gateway
     * @return the user info response
     */
    UserInfoResponse getCurrentUser(String email);
}
