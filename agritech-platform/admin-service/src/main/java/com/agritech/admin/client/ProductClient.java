package com.agritech.admin.client;

import com.agritech.common.pagination.PagedResponse;
import com.agritech.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "PRODUCT-SERVICE", path = "/api/products")
public interface ProductClient {

    @GetMapping
    ApiResponse<PagedResponse<Map<String, Object>>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    );
}
