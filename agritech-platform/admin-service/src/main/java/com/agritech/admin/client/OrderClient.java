package com.agritech.admin.client;

import com.agritech.common.pagination.PagedResponse;
import com.agritech.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "ORDER-SERVICE", path = "/api/orders")
public interface OrderClient {

    @GetMapping("/admin")
    ApiResponse<PagedResponse<Map<String, Object>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    );
}
