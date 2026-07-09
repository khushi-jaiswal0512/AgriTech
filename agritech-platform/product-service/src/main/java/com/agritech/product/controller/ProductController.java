package com.agritech.product.controller;

import com.agritech.common.pagination.PagedResponse;
import com.agritech.common.response.ApiResponse;
import com.agritech.product.dto.request.CreateProductRequest;
import com.agritech.product.dto.request.ReserveStockRequest;
import com.agritech.product.dto.request.UpdateInventoryRequest;
import com.agritech.product.dto.request.UpdateProductRequest;
import com.agritech.product.dto.response.CategoryResponse;
import com.agritech.product.dto.response.ProductResponse;
import com.agritech.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for product catalog operations.
 * All write operations read farmer identity from the Gateway-injected X-User-Id header.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog, inventory, images, and search")
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a product with 1-5 images (Farmer only)")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestHeader("X-User-Id") Long farmerId,
            @Valid @RequestPart("product") CreateProductRequest request,
            @RequestPart("images") List<MultipartFile> images) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created, pending approval",
                        productService.createProduct(farmerId, request, images)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProduct(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product (Owner only)")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long farmerId,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Product updated",
                productService.updateProduct(id, farmerId, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product (Owner or Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long farmerId,
            @RequestHeader("X-User-Role") String role) {
        productService.deleteProduct(id, farmerId, role);
        return ResponseEntity.ok(ApiResponse.success("Product deleted"));
    }

    @GetMapping
    @Operation(summary = "Search and filter products (paginated)")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> searchProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long farmerId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean organic,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.success(
                productService.searchProducts(search, categoryId, farmerId,
                        minPrice, maxPrice, city, state, organic, minRating,
                        page, size, sortBy, sortDir)));
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload additional images (max 5 total)")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> uploadImages(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long farmerId,
            @RequestPart("images") List<MultipartFile> images) {
        return ResponseEntity.ok(ApiResponse.success("Images uploaded",
                productService.uploadImages(id, farmerId, images)));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @Operation(summary = "Delete image (min 1 must remain)")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @PathVariable Long id,
            @PathVariable Long imageId,
            @RequestHeader("X-User-Id") Long farmerId) {
        productService.deleteImage(id, farmerId, imageId);
        return ResponseEntity.ok(ApiResponse.success("Image deleted"));
    }

    @PutMapping("/{id}/inventory")
    @Operation(summary = "Update product inventory (STOCK_IN / STOCK_OUT / ADJUSTMENT)")
    public ResponseEntity<ApiResponse<ProductResponse>> updateInventory(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long farmerId,
            @Valid @RequestBody UpdateInventoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Inventory updated",
                productService.updateInventory(id, farmerId, request)));
    }

    @PostMapping("/reserve-stock")
    @Operation(summary = "Reserve stock synchronously during checkout")
    public ResponseEntity<ApiResponse<Void>> reserveStock(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody List<ReserveStockRequest> requests) {
        productService.reserveStock(userId, requests);
        return ResponseEntity.ok(ApiResponse.success("Stock reserved successfully"));
    }

    @GetMapping("/categories")
    @Operation(summary = "List all active categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(productService.getCategories()));
    }
}
