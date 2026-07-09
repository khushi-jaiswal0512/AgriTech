package com.agritech.order.client;

import com.agritech.common.response.ApiResponse;
import com.agritech.order.client.dto.ReserveStockRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "product-service", path = "/api/products")
public interface ProductServiceClient {

    @PostMapping("/reserve-stock")
    ApiResponse<Void> reserveStock(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<ReserveStockRequest> requests);
}
