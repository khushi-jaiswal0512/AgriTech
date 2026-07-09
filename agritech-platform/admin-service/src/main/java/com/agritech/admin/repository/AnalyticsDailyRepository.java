package com.agritech.admin.repository;

import com.agritech.admin.entity.AnalyticsDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AnalyticsDailyRepository extends JpaRepository<AnalyticsDaily, Long> {
    Optional<AnalyticsDaily> findBySnapshotDate(LocalDate date);
}
