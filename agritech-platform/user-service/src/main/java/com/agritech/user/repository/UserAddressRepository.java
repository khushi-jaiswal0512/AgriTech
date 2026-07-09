package com.agritech.user.repository;

import com.agritech.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for {@link UserAddress} entities.
 */
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    /**
     * Find all addresses belonging to a given user profile.
     *
     * @param userProfileId the profile ID
     * @return list of addresses
     */
    List<UserAddress> findByUserProfileId(Long userProfileId);

    /**
     * Find a specific address owned by a specific user profile (prevents cross-user access).
     *
     * @param id            the address ID
     * @param userProfileId the owning profile ID
     * @return an Optional containing the address if found and owned by the profile
     */
    Optional<UserAddress> findByIdAndUserProfileId(Long id, Long userProfileId);

    /**
     * Clear the default flag on all addresses for a profile except the specified address.
     * Used when setting a new default to ensure only one default exists.
     *
     * @param profileId the profile ID
     * @param addressId the address ID to exclude from the clear
     * @return number of rows updated
     */
    @Modifying
    @Query("UPDATE UserAddress a SET a.isDefault = false WHERE a.userProfile.id = :profileId AND a.id <> :addressId")
    int clearDefaultExcept(Long profileId, Long addressId);
}
