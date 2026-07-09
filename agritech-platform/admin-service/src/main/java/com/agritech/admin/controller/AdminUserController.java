package com.agritech.admin.controller;

import com.agritech.admin.service.AdminUserService;
import com.agritech.common.pagination.PagedResponse;
import com.agritech.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin Users", description = "Admin User Management APIs")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "List all users")
    public ResponseEntity<ApiResponse<PagedResponse<Map<String, Object>>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminUserService.getAllUsers(page, size));
    }

    @PutMapping("/farmers/{id}/verify")
    @Operation(summary = "Verify a farmer")
    public ResponseEntity<ApiResponse<Void>> verifyFarmer(@PathVariable Long id) {
        adminUserService.verifyFarmer(id);
        return ResponseEntity.ok(ApiResponse.success("Farmer verification initiated successfully"));
    }
}
