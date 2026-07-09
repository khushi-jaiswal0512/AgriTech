package com.agritech.user.repository;

import com.agritech.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for {@link UserProfile} entities.
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /**
     * Find a user profile by its linked auth service user ID.
     *
     * @param authUserId the ID from the auth service
     * @return an Optional containing the profile if found
     */
    Optional<UserProfile> findByAuthUserId(Long authUserId);

    /**
     * Check whether a profile already exists for a given auth user ID.
     *
     * @param authUserId the ID from the auth service
     * @return true if a profile already exists
     */
    boolean existsByAuthUserId(Long authUserId);
}
