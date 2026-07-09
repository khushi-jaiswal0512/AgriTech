package com.agritech.product.repository;

import com.agritech.product.entity.Product;
import com.agritech.product.enums.ApprovalStatus;
import com.agritech.product.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
                                           JpaSpecificationExecutor<Product> {

    Page<Product> findByFarmerIdAndApprovalStatus(
            Long farmerId, ApprovalStatus approvalStatus, Pageable pageable);

    Page<Product> findByFarmerId(Long farmerId, Pageable pageable);
}
