package com.agritech.user.controller;

import com.agritech.common.response.ApiResponse;
import com.agritech.user.dto.request.AddressRequest;
import com.agritech.user.dto.request.FarmerDetailsRequest;
import com.agritech.user.dto.request.RetailerDetailsRequest;
import com.agritech.user.dto.request.UpdateProfileRequest;
import com.agritech.user.dto.response.AddressResponse;
import com.agritech.user.dto.response.UserProfileResponse;
import com.agritech.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user profile management.
 *
 * <p>All endpoints receive the authenticated user's identity via the
 * {@code X-User-Id} header, injected by the API Gateway after JWT validation.
 * This service itself does not perform JWT validation.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Profile, address, and role-specific detail management")
public class UserProfileController {

    private final UserProfileService profileService;

    /**
     * Retrieve the authenticated user's own profile.
     *
     * @param authUserId the auth service user ID injected by the API Gateway
     * @return the user's profile wrapped in {@link ApiResponse}
     */
    @GetMapping("/profile")
    @Operation(summary = "Get own profile", description = "Returns the full profile of the currently authenticated user.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @RequestHeader("X-User-Id") Long authUserId) {
        log.info("GET /api/users/profile for authUserId={}", authUserId);
        return ResponseEntity.ok(ApiResponse.success(profileService.getProfile(authUserId)));
    }

    /**
     * Update the authenticated user's basic profile fields.
     *
     * @param authUserId the auth service user ID
     * @param request    the update payload
     * @return the updated profile wrapped in {@link ApiResponse}
     */
    @PutMapping("/profile")
    @Operation(summary = "Update profile", description = "Update name, phone, and profile image URL.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @RequestHeader("X-User-Id") Long authUserId,
            @Valid @RequestBody UpdateProfileRequest request) {
        log.info("PUT /api/users/profile for authUserId={}", authUserId);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", profileService.updateProfile(authUserId, request)));
    }

    /**
     * Retrieve a user profile by its primary key (admin/internal use).
     *
     * @param id the profile table primary key
     * @return the profile wrapped in {@link ApiResponse}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user profile by ID", description = "Intended for admin or inter-service use. Returns profile by profile table ID.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getById(@PathVariable Long id) {
        log.info("GET /api/users/{}", id);
        return ResponseEntity.ok(ApiResponse.success(profileService.getProfileById(id)));
    }

    /**
     * List all addresses for the authenticated user.
     *
     * @param authUserId the auth service user ID
     * @return list of addresses wrapped in {@link ApiResponse}
     */
    @GetMapping("/addresses")
    @Operation(summary = "List all addresses", description = "Returns all saved addresses for the authenticated user.")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @RequestHeader("X-User-Id") Long authUserId) {
        log.info("GET /api/users/addresses for authUserId={}", authUserId);
        return ResponseEntity.ok(ApiResponse.success(profileService.getAddresses(authUserId)));
    }

    /**
     * Add a new address for the authenticated user.
     *
     * @param authUserId the auth service user ID
     * @param request    the address creation payload
     * @return the created address wrapped in {@link ApiResponse} with HTTP 201
     */
    @PostMapping("/addresses")
    @Operation(summary = "Add a new address", description = "Creates a new address entry for the authenticated user.")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @RequestHeader("X-User-Id") Long authUserId,
            @Valid @RequestBody AddressRequest request) {
        log.info("POST /api/users/addresses for authUserId={}", authUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address added", profileService.addAddress(authUserId, request)));
    }

    /**
     * Update an existing address owned by the authenticated user.
     *
     * @param authUserId the auth service user ID
     * @param addressId  the address to update
     * @param request    the update payload
     * @return the updated address wrapped in {@link ApiResponse}
     */
    @PutMapping("/addresses/{addressId}")
    @Operation(summary = "Update address", description = "Updates an existing address. Only the owning user can update their address.")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @RequestHeader("X-User-Id") Long authUserId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {
        log.info("PUT /api/users/addresses/{} for authUserId={}", addressId, authUserId);
        return ResponseEntity.ok(ApiResponse.success("Address updated",
                profileService.updateAddress(authUserId, addressId, request)));
    }

    /**
     * Delete an address owned by the authenticated user.
     *
     * @param authUserId the auth service user ID
     * @param addressId  the address to delete
     * @return a success message wrapped in {@link ApiResponse}
     */
    @DeleteMapping("/addresses/{addressId}")
    @Operation(summary = "Delete address", description = "Deletes an address. Only the owning user can delete their address.")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @RequestHeader("X-User-Id") Long authUserId,
            @PathVariable Long addressId) {
        log.info("DELETE /api/users/addresses/{} for authUserId={}", addressId, authUserId);
        profileService.deleteAddress(authUserId, addressId);
        return ResponseEntity.ok(ApiResponse.success("Address deleted"));
    }

    /**
     * Create or update farmer-specific profile details for the authenticated user.
     *
     * @param authUserId the auth service user ID
     * @param request    the farmer detail payload
     * @return the updated profile wrapped in {@link ApiResponse}
     */
    @PutMapping("/farmer-details")
    @Operation(summary = "Update farmer-specific details", description = "Upserts farm metadata, farming type, and government ID for a farmer user.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateFarmerDetails(
            @RequestHeader("X-User-Id") Long authUserId,
            @Valid @RequestBody FarmerDetailsRequest request) {
        log.info("PUT /api/users/farmer-details for authUserId={}", authUserId);
        return ResponseEntity.ok(ApiResponse.success("Farmer details updated",
                profileService.updateFarmerDetails(authUserId, request)));
    }

    /**
     * Create or update retailer-specific profile details for the authenticated user.
     *
     * @param authUserId the auth service user ID
     * @param request    the retailer detail payload
     * @return the updated profile wrapped in {@link ApiResponse}
     */
    @PutMapping("/retailer-details")
    @Operation(summary = "Update retailer-specific details", description = "Upserts business name, GST, type, and license for a retailer user.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateRetailerDetails(
            @RequestHeader("X-User-Id") Long authUserId,
            @Valid @RequestBody RetailerDetailsRequest request) {
        log.info("PUT /api/users/retailer-details for authUserId={}", authUserId);
        return ResponseEntity.ok(ApiResponse.success("Retailer details updated",
                profileService.updateRetailerDetails(authUserId, request)));
    }
}
