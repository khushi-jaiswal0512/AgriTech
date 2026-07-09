package com.agritech.auth.dto.response;

import com.agritech.auth.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for current authenticated user info.
 * Returned by the /me endpoint when the API Gateway forwards the request.
 */
@Data
@Builder
public class UserInfoResponse {

    /** The user's internal ID. */
    private Long id;

    /** The user's email address. */
    private String email;

    /** The user's assigned role. */
    private UserRole role;

    /** Whether the account is active. */
    private boolean active;

    /** Whether the email has been verified. */
    private boolean verified;

    /** Timestamp when the account was created. */
    private LocalDateTime createdAt;
}
