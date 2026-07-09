package com.agritech.user.dto.response;

import com.agritech.user.enums.AccountStatus;
import com.agritech.user.enums.VerificationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO representing a user's profile data.
 */
@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private Long authUserId;
    private String firstName;
    private String lastName;
    private String phone;
    private String profileImageUrl;
    private String role;
    private AccountStatus accountStatus;
    private VerificationStatus verificationStatus;
    private String verificationNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
