package com.agritech.admin.service;

import com.agritech.common.pagination.PagedResponse;
import com.agritech.common.response.ApiResponse;

import java.util.Map;

public interface AdminProductService {
    ApiResponse<PagedResponse<Map<String, Object>>> getPendingProducts(int page, int size);
    void approveProduct(Long productId);
    void rejectProduct(Long productId);
}
