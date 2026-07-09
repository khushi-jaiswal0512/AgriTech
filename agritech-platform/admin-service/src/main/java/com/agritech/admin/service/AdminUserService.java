package com.agritech.admin.service;

import com.agritech.common.pagination.PagedResponse;
import com.agritech.common.response.ApiResponse;

import java.util.Map;

public interface AdminUserService {
    ApiResponse<PagedResponse<Map<String, Object>>> getAllUsers(int page, int size);
    void verifyFarmer(Long farmerId);
}
