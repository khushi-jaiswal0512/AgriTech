package com.agritech.admin.service.impl;

import com.agritech.admin.dto.DashboardStatsResponse;
import com.agritech.admin.entity.AnalyticsDaily;
import com.agritech.admin.entity.TopProduct;
import com.agritech.admin.repository.AnalyticsDailyRepository;
import com.agritech.admin.repository.TopProductRepository;
import com.agritech.admin.service.AdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {

    private final AnalyticsDailyRepository analyticsDailyRepository;
    private final TopProductRepository topProductRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        LocalDate today = LocalDate.now();
        AnalyticsDaily stats = analyticsDailyRepository.findBySnapshotDate(today)
                .orElse(AnalyticsDaily.builder().snapshotDate(today).build());

        return DashboardStatsResponse.builder()
                .date(stats.getSnapshotDate())
                .totalUsers(stats.getTotalUsers())
                .totalFarmers(stats.getTotalFarmers())
                .totalConsumers(stats.getTotalConsumers())
                .totalRetailers(stats.getTotalRetailers())
                .totalProducts(stats.getTotalProducts())
                .totalOrders(stats.getTotalOrders())
                .totalRevenue(stats.getTotalRevenue())
                .ordersToday(stats.getOrdersToday())
                .revenueToday(stats.getRevenueToday())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopProduct> getTopProducts(String period) {
        return topProductRepository.findByPeriodAndSnapshotDateOrderByTotalRevenueDesc(period, LocalDate.now());
    }

    @Override
    @Transactional
    public void incrementOrderAndRevenue(BigDecimal amount) {
        LocalDate today = LocalDate.now();
        AnalyticsDaily stats = analyticsDailyRepository.findBySnapshotDate(today)
                .orElseGet(() -> {
                    AnalyticsDaily newStats = AnalyticsDaily.builder().snapshotDate(today).build();
                    // In a real scenario, fetch totals from DB or previous day's snapshot
                    return newStats;
                });

        stats.setTotalOrders(stats.getTotalOrders() + 1);
        stats.setTotalRevenue(stats.getTotalRevenue().add(amount));
        stats.setOrdersToday(stats.getOrdersToday() + 1);
        stats.setRevenueToday(stats.getRevenueToday().add(amount));

        analyticsDailyRepository.save(stats);
    }
}
