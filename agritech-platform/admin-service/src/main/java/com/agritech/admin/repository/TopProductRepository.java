package com.agritech.admin.repository;

import com.agritech.admin.entity.TopProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TopProductRepository extends JpaRepository<TopProduct, Long> {
    List<TopProduct> findByPeriodAndSnapshotDateOrderByTotalRevenueDesc(String period, LocalDate date);
}
