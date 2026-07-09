package com.agritech.rating.repository;

import com.agritech.rating.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Page<Rating> findByProductId(Long productId, Pageable pageable);
    
    Optional<Rating> findByProductIdAndUserId(Long productId, Long userId);
    
    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.productId = :productId")
    Double getAverageRatingForProduct(Long productId);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.productId = :productId")
    Long countRatingsForProduct(Long productId);
}
