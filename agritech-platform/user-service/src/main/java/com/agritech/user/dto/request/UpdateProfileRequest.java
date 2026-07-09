package com.agritech.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Request DTO for updating a user's own profile.
 */
@Data
public class UpdateProfileRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100)
    private String lastName;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;

    @Size(max = 500)
    private String profileImageUrl;
}
