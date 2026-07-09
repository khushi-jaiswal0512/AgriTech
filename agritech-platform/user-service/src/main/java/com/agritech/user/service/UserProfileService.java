package com.agritech.user.service;

import com.agritech.user.dto.request.AddressRequest;
import com.agritech.user.dto.request.FarmerDetailsRequest;
import com.agritech.user.dto.request.RetailerDetailsRequest;
import com.agritech.user.dto.request.UpdateProfileRequest;
import com.agritech.user.dto.response.AddressResponse;
import com.agritech.user.dto.response.UserProfileResponse;

import java.util.List;

/**
 * Service interface for user profile management operations.
 * Provides CRUD for profiles, addresses, and role-specific detail blocks.
 */
public interface UserProfileService {

    /**
     * Retrieve the profile for the currently authenticated user.
     *
     * @param authUserId the auth service user ID injected by the gateway
     * @return the user's profile response
     */
    UserProfileResponse getProfile(Long authUserId);

    /**
     * Retrieve a user profile by its profile table primary key (admin/internal).
     *
     * @param profileId the profile primary key
     * @return the user's profile response
     */
    UserProfileResponse getProfileById(Long profileId);

    /**
     * Update basic profile fields for the authenticated user.
     *
     * @param authUserId the auth service user ID
     * @param request    the update payload
     * @return the updated profile response
     */
    UserProfileResponse updateProfile(Long authUserId, UpdateProfileRequest request);

    /**
     * Add a new address for the authenticated user.
     *
     * @param authUserId the auth service user ID
     * @param request    the new address payload
     * @return the created address response
     */
    AddressResponse addAddress(Long authUserId, AddressRequest request);

    /**
     * Update an existing address owned by the authenticated user.
     *
     * @param authUserId the auth service user ID
     * @param addressId  the address to update
     * @param request    the update payload
     * @return the updated address response
     */
    AddressResponse updateAddress(Long authUserId, Long addressId, AddressRequest request);

    /**
     * Delete an address owned by the authenticated user.
     *
     * @param authUserId the auth service user ID
     * @param addressId  the address to delete
     */
    void deleteAddress(Long authUserId, Long addressId);

    /**
     * List all addresses belonging to the authenticated user.
     *
     * @param authUserId the auth service user ID
     * @return list of address responses
     */
    List<AddressResponse> getAddresses(Long authUserId);

    /**
     * Create or update farmer-specific details for the authenticated user.
     *
     * @param authUserId the auth service user ID
     * @param request    the farmer detail payload
     * @return the updated profile response
     */
    UserProfileResponse updateFarmerDetails(Long authUserId, FarmerDetailsRequest request);

    /**
     * Create or update retailer-specific details for the authenticated user.
     *
     * @param authUserId the auth service user ID
     * @param request    the retailer detail payload
     * @return the updated profile response
     */
    UserProfileResponse updateRetailerDetails(Long authUserId, RetailerDetailsRequest request);
}
