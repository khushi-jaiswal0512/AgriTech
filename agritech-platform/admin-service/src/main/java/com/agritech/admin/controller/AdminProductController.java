package com.agritech.admin.controller;

import com.agritech.admin.service.AdminProductService;
import com.agritech.common.pagination.PagedResponse;
import com.agritech.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Tag(name = "Admin Products", description = "Admin Product Management APIs")
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping("/pending")
    @Operation(summary = "List pending products")
    public ResponseEntity<ApiResponse<PagedResponse<Map<String, Object>>>> getPendingProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminProductService.getPendingProducts(page, size));
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve a product")
    public ResponseEntity<ApiResponse<Void>> approveProduct(@PathVariable Long id) {
        adminProductService.approveProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product approval initiated successfully"));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a product")
    public ResponseEntity<ApiResponse<Void>> rejectProduct(@PathVariable Long id) {
        adminProductService.rejectProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product rejection initiated successfully"));
    }
}
