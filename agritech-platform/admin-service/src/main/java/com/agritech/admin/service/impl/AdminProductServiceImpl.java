package com.agritech.admin.service.impl;

import com.agritech.admin.client.ProductClient;
import com.agritech.admin.event.producer.AdminEventProducer;
import com.agritech.admin.service.AdminProductService;
import com.agritech.common.pagination.PagedResponse;
import com.agritech.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductClient productClient;
    private final AdminEventProducer eventProducer;

    @Override
    public ApiResponse<PagedResponse<Map<String, Object>>> getPendingProducts(int page, int size) {
        return productClient.getProducts(null, "PENDING", page, size);
    }

    @Override
    public void approveProduct(Long productId) {
        eventProducer.publishProductApproved(productId);
    }

    @Override
    public void rejectProduct(Long productId) {
        eventProducer.publishProductRejected(productId);
    }
}
