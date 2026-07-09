package com.agritech.admin.service.impl;

import com.agritech.admin.client.UserClient;
import com.agritech.admin.event.producer.AdminEventProducer;
import com.agritech.admin.service.AdminUserService;
import com.agritech.common.pagination.PagedResponse;
import com.agritech.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserClient userClient;
    private final AdminEventProducer eventProducer;

    @Override
    public ApiResponse<PagedResponse<Map<String, Object>>> getAllUsers(int page, int size) {
        return userClient.getAllUsers(page, size);
    }

    @Override
    public void verifyFarmer(Long farmerId) {
        // Fire kafka event to async verify farmer in user and auth services
        eventProducer.publishFarmerVerified(farmerId);
    }
}
