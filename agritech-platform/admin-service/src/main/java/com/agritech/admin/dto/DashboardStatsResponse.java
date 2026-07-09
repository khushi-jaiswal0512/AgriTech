package com.agritech.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class DashboardStatsResponse {
    private LocalDate date;
    private Integer totalUsers;
    private Integer totalFarmers;
    private Integer totalConsumers;
    private Integer totalRetailers;
    private Integer totalProducts;
    private Integer totalOrders;
    private BigDecimal totalRevenue;
    private Integer ordersToday;
    private BigDecimal revenueToday;
}
