package com.agritech.user.service.impl;

import com.agritech.common.exception.ResourceNotFoundException;
import com.agritech.user.dto.request.AddressRequest;
import com.agritech.user.dto.request.FarmerDetailsRequest;
import com.agritech.user.dto.request.RetailerDetailsRequest;
import com.agritech.user.dto.request.UpdateProfileRequest;
import com.agritech.user.dto.response.AddressResponse;
import com.agritech.user.dto.response.UserProfileResponse;
import com.agritech.user.entity.FarmerDetails;
import com.agritech.user.entity.RetailerDetails;
import com.agritech.user.entity.UserAddress;
import com.agritech.user.entity.UserProfile;
import com.agritech.user.mapper.UserProfileMapper;
import com.agritech.user.repository.UserAddressRepository;
import com.agritech.user.repository.UserProfileRepository;
import com.agritech.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of {@link UserProfileService}.
 * Handles profile, address, farmer, and retailer detail management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository profileRepository;
    private final UserAddressRepository addressRepository;
    private final UserProfileMapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long authUserId) {
        log.info("Fetching profile for authUserId={}", authUserId);
        UserProfile profile = findByAuthUserId(authUserId);
        return mapper.toResponse(profile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfileById(Long profileId) {
        log.info("Fetching profile by profileId={}", profileId);
        UserProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile", "id", profileId));
        return mapper.toResponse(profile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long authUserId, UpdateProfileRequest request) {
        log.info("Updating profile for authUserId={}", authUserId);
        UserProfile profile = findByAuthUserId(authUserId);
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhone(request.getPhone());
        if (request.getProfileImageUrl() != null) {
            profile.setProfileImageUrl(request.getProfileImageUrl());
        }
        UserProfile saved = profileRepository.save(profile);
        log.info("Profile updated for authUserId={}", authUserId);
        return mapper.toResponse(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AddressResponse addAddress(Long authUserId, AddressRequest request) {
        log.info("Adding address for authUserId={}", authUserId);
        UserProfile profile = findByAuthUserId(authUserId);

        if (request.isDefault()) {
            // Clear default flag on all other addresses before setting new default
            addressRepository.clearDefaultExcept(profile.getId(), -1L);
        }

        UserAddress address = UserAddress.builder()
                .userProfile(profile)
                .label(request.getLabel())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .isDefault(request.isDefault())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

        UserAddress saved = addressRepository.save(address);
        log.info("Address id={} created for authUserId={}", saved.getId(), authUserId);
        return mapper.toAddressResponse(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AddressResponse updateAddress(Long authUserId, Long addressId, AddressRequest request) {
        log.info("Updating address id={} for authUserId={}", addressId, authUserId);
        UserProfile profile = findByAuthUserId(authUserId);
        UserAddress address = addressRepository.findByIdAndUserProfileId(addressId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        if (request.isDefault()) {
            addressRepository.clearDefaultExcept(profile.getId(), addressId);
        }

        address.setLabel(request.getLabel());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setDefault(request.isDefault());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());

        return mapper.toAddressResponse(addressRepository.save(address));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteAddress(Long authUserId, Long addressId) {
        log.info("Deleting address id={} for authUserId={}", addressId, authUserId);
        UserProfile profile = findByAuthUserId(authUserId);
        UserAddress address = addressRepository.findByIdAndUserProfileId(addressId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        addressRepository.delete(address);
        log.info("Deleted address id={} for authUserId={}", addressId, authUserId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(Long authUserId) {
        log.info("Listing addresses for authUserId={}", authUserId);
        UserProfile profile = findByAuthUserId(authUserId);
        return addressRepository.findByUserProfileId(profile.getId())
                .stream()
                .map(mapper::toAddressResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserProfileResponse updateFarmerDetails(Long authUserId, FarmerDetailsRequest request) {
        log.info("Updating farmer details for authUserId={}", authUserId);
        UserProfile profile = findByAuthUserId(authUserId);

        FarmerDetails details = profile.getFarmerDetails();
        if (details == null) {
            log.info("No existing farmer details found for authUserId={}, creating new", authUserId);
            details = FarmerDetails.builder().userProfile(profile).build();
        }

        details.setFarmName(request.getFarmName());
        details.setFarmSizeAcres(request.getFarmSizeAcres());
        if (request.getFarmingType() != null) {
            details.setFarmingType(request.getFarmingType());
        }
        details.setExperienceYears(request.getExperienceYears());
        details.setGovernmentId(request.getGovernmentId());
        details.setGovernmentIdType(request.getGovernmentIdType());

        profile.setFarmerDetails(details);
        return mapper.toResponse(profileRepository.save(profile));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserProfileResponse updateRetailerDetails(Long authUserId, RetailerDetailsRequest request) {
        log.info("Updating retailer details for authUserId={}", authUserId);
        UserProfile profile = findByAuthUserId(authUserId);

        RetailerDetails details = profile.getRetailerDetails();
        if (details == null) {
            log.info("No existing retailer details found for authUserId={}, creating new", authUserId);
            details = RetailerDetails.builder().userProfile(profile).build();
        }

        details.setBusinessName(request.getBusinessName());
        details.setGstNumber(request.getGstNumber());
        details.setBusinessType(request.getBusinessType());
        details.setLicenseNumber(request.getLicenseNumber());

        profile.setRetailerDetails(details);
        return mapper.toResponse(profileRepository.save(profile));
    }

    // ---------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------

    /**
     * Fetch a UserProfile by authUserId or throw a ResourceNotFoundException.
     *
     * @param authUserId the auth service user ID
     * @return the user profile entity
     */
    private UserProfile findByAuthUserId(Long authUserId) {
        return profileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile", "authUserId", authUserId));
    }
}
