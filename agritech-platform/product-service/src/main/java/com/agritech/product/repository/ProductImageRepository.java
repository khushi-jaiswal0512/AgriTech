package com.agritech.product.repository;

import com.agritech.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    int countByProductId(Long productId);

    List<ProductImage> findByProductId(Long productId);

    Optional<ProductImage> findByIdAndProductId(Long id, Long productId);
}
