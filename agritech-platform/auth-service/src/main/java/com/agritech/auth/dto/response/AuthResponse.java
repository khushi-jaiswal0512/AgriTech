package com.agritech.auth.dto.response;

import com.agritech.auth.enums.UserRole;
import lombok.Builder;
import lombok.Data;

/**
 * Response DTO returned after successful authentication (login or register).
 * Contains both access and refresh tokens along with user metadata.
 */
@Data
@Builder
public class AuthResponse {

    /** JWT access token for API requests. */
    private String accessToken;

    /** Opaque refresh token for obtaining new access tokens. */
    private String refreshToken;

    /** Always "Bearer". */
    private String tokenType;

    /** Access token TTL in seconds. */
    private long expiresIn;

    /** The authenticated user's internal ID. */
    private Long userId;

    /** The authenticated user's email address. */
    private String email;

    /** The authenticated user's role. */
    private UserRole role;

    /** Whether the user has verified their email address. */
    private boolean verified;
}
