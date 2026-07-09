package com.agritech.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka event published by Auth Service when a new user registers.
 * Consumed by User Service to create the user profile.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {

    /** The auth_user_id from the auth_db (auth-service primary key). */
    private Long authUserId;

    /** User's email address. */
    private String email;

    /** User's first name. */
    private String firstName;

    /** User's last name. */
    private String lastName;

    /** User's phone number (nullable). */
    private String phone;

    /** Role string e.g. "ROLE_FARMER", "ROLE_CONSUMER", "ROLE_RETAILER". */
    private String role;
}
