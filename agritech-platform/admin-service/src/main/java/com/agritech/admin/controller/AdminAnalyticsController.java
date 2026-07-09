package com.agritech.admin.controller;

import com.agritech.admin.dto.DashboardStatsResponse;
import com.agritech.admin.entity.TopProduct;
import com.agritech.admin.service.AdminAnalyticsService;
import com.agritech.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
@Tag(name = "Admin Analytics", description = "Admin Analytics APIs")
public class AdminAnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard KPIs")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(
                adminAnalyticsService.getDashboardStats()
        ));
    }

    @GetMapping("/top-products")
    @Operation(summary = "Get top selling products")
    public ResponseEntity<ApiResponse<List<TopProduct>>> getTopProducts(
            @RequestParam(defaultValue = "DAILY") String period) {
        return ResponseEntity.ok(ApiResponse.success(
                adminAnalyticsService.getTopProducts(period)
        ));
    }
}
