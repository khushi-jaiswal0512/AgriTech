package com.agritech.admin.client;

import com.agritech.common.pagination.PagedResponse;
import com.agritech.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "USER-SERVICE", path = "/api/users")
public interface UserClient {

    @GetMapping
    ApiResponse<PagedResponse<Map<String, Object>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    );

    @PutMapping("/{id}/status")
    ApiResponse<Void> updateUserStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status
    );
}
