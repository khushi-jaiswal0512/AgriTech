package com.agritech.product.service;

import com.agritech.common.pagination.PagedResponse;
import com.agritech.product.dto.request.CreateProductRequest;
import com.agritech.product.dto.request.ReserveStockRequest;
import com.agritech.product.dto.request.UpdateInventoryRequest;
import com.agritech.product.dto.request.UpdateProductRequest;
import com.agritech.product.dto.response.CategoryResponse;
import com.agritech.product.dto.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductResponse createProduct(Long farmerId, CreateProductRequest request, List<MultipartFile> images);

    ProductResponse getProduct(Long id);

    ProductResponse updateProduct(Long id, Long farmerId, UpdateProductRequest request);

    void deleteProduct(Long id, Long farmerId, String role);

    PagedResponse<ProductResponse> searchProducts(
            String search, Long categoryId, Long farmerId,
            BigDecimal minPrice, BigDecimal maxPrice,
            String city, String state, Boolean organic, Double minRating,
            int page, int size, String sortBy, String sortDir);

    List<ProductResponse> uploadImages(Long productId, Long farmerId, List<MultipartFile> images);

    void deleteImage(Long productId, Long farmerId, Long imageId);

    ProductResponse updateInventory(Long productId, Long farmerId, UpdateInventoryRequest request);

    void reserveStock(Long userId, List<ReserveStockRequest> requests);

    List<CategoryResponse> getCategories();
}
