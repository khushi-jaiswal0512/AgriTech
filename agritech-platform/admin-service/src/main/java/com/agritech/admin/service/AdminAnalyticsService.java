package com.agritech.admin.service;

import com.agritech.admin.dto.DashboardStatsResponse;
import com.agritech.admin.entity.TopProduct;

import java.math.BigDecimal;
import java.util.List;

public interface AdminAnalyticsService {
    DashboardStatsResponse getDashboardStats();
    List<TopProduct> getTopProducts(String period);
    void incrementOrderAndRevenue(BigDecimal amount);
}
