package com.agritech.product.service.impl;

import com.agritech.common.exception.BadRequestException;
import com.agritech.common.exception.ForbiddenException;
import com.agritech.common.exception.ResourceNotFoundException;
import com.agritech.common.exception.ValidationException;
import com.agritech.common.pagination.PagedResponse;
import com.agritech.product.constants.ProductCacheKeys;
import com.agritech.product.dto.request.CreateProductRequest;
import com.agritech.product.dto.request.UpdateInventoryRequest;
import com.agritech.product.dto.request.UpdateProductRequest;
import com.agritech.product.dto.response.CategoryResponse;
import com.agritech.product.dto.response.ProductResponse;
import com.agritech.product.entity.Category;
import com.agritech.product.entity.InventoryLog;
import com.agritech.product.entity.Product;
import com.agritech.product.entity.ProductImage;
import com.agritech.product.enums.ApprovalStatus;
import com.agritech.product.enums.InventoryChangeType;
import com.agritech.product.enums.ProductStatus;
import com.agritech.product.event.producer.ProductEventProducer;
import com.agritech.product.mapper.ProductMapper;
import com.agritech.product.repository.CategoryRepository;
import com.agritech.product.repository.InventoryLogRepository;
import com.agritech.product.repository.ProductImageRepository;
import com.agritech.product.repository.ProductRepository;
import com.agritech.product.service.ProductService;
import com.agritech.product.service.S3Service;
import com.agritech.product.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * Core business logic for product catalog management.
 * Handles CRUD, image lifecycle, inventory tracking, and search.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final List<String> ALLOWED_SORT_FIELDS = List.of("price", "avgRating", "createdAt", "name");
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/webp");
    private static final int MAX_IMAGES = 5;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final ProductMapper productMapper;
    private final S3Service s3Service;
    private final ProductEventProducer eventProducer;

    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public ProductResponse createProduct(Long farmerId, CreateProductRequest request, List<MultipartFile> images) {
        log.info("Creating product for farmerId={}", farmerId);

        // Validate images
        validateImages(images, null);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = Product.builder()
                .farmerId(farmerId)
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .availableQuantity(request.getAvailableQuantity())
                .unit(request.getUnit())
                .organic(request.isOrganic())
                .harvestDate(request.getHarvestDate())
                .city(request.getCity())
                .state(request.getState())
                .productStatus(ProductStatus.AVAILABLE)
                .approvalStatus(ApprovalStatus.PENDING)
                .build();

        product = productRepository.save(product);
        final Long productId = product.getId();

        // Upload images to S3 and save
        for (int i = 0; i < images.size(); i++) {
            MultipartFile file = images.get(i);
            String s3Key = s3Service.uploadImage(productId, file);
            String url = s3Service.buildUrl(s3Key);

            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(url)
                    .s3Key(s3Key)
                    .sortOrder(i)
                    .primary(i == 0)
                    .build();
            product.getImages().add(image);
        }

        product = productRepository.save(product);
        log.info("Product created with id={}, pending approval", product.getId());

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "product", key = "#id")
    public ProductResponse getProduct(Long id) {
        Product product = findProductById(id);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = "product", key = "#id")
    public ProductResponse updateProduct(Long id, Long farmerId, UpdateProductRequest request) {
        Product product = findProductById(id);
        assertOwner(product, farmerId);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setUnit(request.getUnit());
        product.setOrganic(request.isOrganic());
        product.setHarvestDate(request.getHarvestDate());
        product.setCity(request.getCity());
        product.setState(request.getState());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategory(category);
        }

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    @CacheEvict(value = "product", key = "#id")
    public void deleteProduct(Long id, Long farmerId, String role) {
        Product product = findProductById(id);
        // Admin can delete any product; farmer only their own
        if (!"ROLE_ADMIN".equals(role)) {
            assertOwner(product, farmerId);
        }
        // Delete S3 images first
        product.getImages().forEach(img -> s3Service.deleteImage(img.getS3Key()));
        productRepository.delete(product);
        log.info("Product deleted: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> searchProducts(
            String search, Long categoryId, Long farmerId,
            BigDecimal minPrice, BigDecimal maxPrice,
            String city, String state, Boolean organic, Double minRating,
            int page, int size, String sortBy, String sortDir) {

        // Whitelist sort fields to prevent injection
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "createdAt";
        }

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, Math.min(size, 50), sort);

        Specification<Product> spec = Specification
                .where(ProductSpecification.isApproved())
                .and(ProductSpecification.isAvailable())
                .and(ProductSpecification.hasCategory(categoryId))
                .and(ProductSpecification.hasFarmer(farmerId))
                .and(ProductSpecification.priceBetween(minPrice, maxPrice))
                .and(ProductSpecification.inCity(city))
                .and(ProductSpecification.inState(state))
                .and(ProductSpecification.isOrganic(organic))
                .and(ProductSpecification.minRating(minRating))
                .and(ProductSpecification.searchByKeyword(search));

        Page<Product> result = productRepository.findAll(spec, pageable);
        return PagedResponse.from(result.map(productMapper::toResponse));
    }

    @Override
    @Transactional
    @CacheEvict(value = "product", key = "#productId")
    public List<ProductResponse> uploadImages(Long productId, Long farmerId, List<MultipartFile> images) {
        Product product = findProductById(productId);
        assertOwner(product, farmerId);

        int existingCount = productImageRepository.countByProductId(productId);
        validateImages(images, existingCount);

        for (int i = 0; i < images.size(); i++) {
            String s3Key = s3Service.uploadImage(productId, images.get(i));
            String url = s3Service.buildUrl(s3Key);

            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(url)
                    .s3Key(s3Key)
                    .sortOrder(existingCount + i)
                    .primary(existingCount == 0 && i == 0)
                    .build();
            productImageRepository.save(image);
        }

        // Reload and return updated product
        Product updated = productRepository.findById(productId).orElseThrow();
        return List.of(productMapper.toResponse(updated));
    }

    @Override
    @Transactional
    @CacheEvict(value = "product", key = "#productId")
    public void deleteImage(Long productId, Long farmerId, Long imageId) {
        Product product = findProductById(productId);
        assertOwner(product, farmerId);

        int existingCount = productImageRepository.countByProductId(productId);
        if (existingCount <= 1) {
            throw new ValidationException("Cannot delete the last image. At least 1 image is required.");
        }

        ProductImage image = productImageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductImage", "id", imageId));

        s3Service.deleteImage(image.getS3Key());
        productImageRepository.delete(image);
        log.info("Deleted image id={} from product id={}", imageId, productId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "product", key = "#productId")
    public ProductResponse updateInventory(Long productId, Long farmerId, UpdateInventoryRequest request) {
        Product product = findProductById(productId);
        assertOwner(product, farmerId);

        int previousQty = product.getAvailableQuantity();
        int newQty;

        switch (request.getChangeType()) {
            case STOCK_IN -> newQty = previousQty + request.getQuantity();
            case STOCK_OUT -> {
                newQty = previousQty - request.getQuantity();
                if (newQty < 0) {
                    throw new BadRequestException("Insufficient stock. Available: " + previousQty);
                }
            }
            case ADJUSTMENT -> newQty = request.getQuantity();
            default -> throw new BadRequestException("Unknown change type: " + request.getChangeType());
        }

        product.setAvailableQuantity(newQty);
        // Auto-update status based on stock level
        product.setProductStatus(newQty == 0 ? ProductStatus.OUT_OF_STOCK : ProductStatus.AVAILABLE);

        // Record audit log
        InventoryLog log = InventoryLog.builder()
                .product(product)
                .changeType(request.getChangeType())
                .quantityChange(request.getChangeType() == InventoryChangeType.ADJUSTMENT
                        ? newQty - previousQty : request.getQuantity())
                .previousQty(previousQty)
                .newQty(newQty)
                .reason(request.getReason())
                .referenceId(request.getReferenceId())
                .build();

        inventoryLogRepository.save(log);
        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void reserveStock(Long userId, List<com.agritech.product.dto.request.ReserveStockRequest> requests) {
        for (com.agritech.product.dto.request.ReserveStockRequest request : requests) {
            Product product = findProductById(request.getProductId());
            int previousQty = product.getAvailableQuantity();
            int newQty = previousQty - request.getQuantity();

            if (newQty < 0) {
                throw new BadRequestException("Insufficient stock for product " + product.getName() +
                        ". Requested: " + request.getQuantity() + ", Available: " + previousQty);
            }

            product.setAvailableQuantity(newQty);
            product.setProductStatus(newQty == 0 ? ProductStatus.OUT_OF_STOCK : ProductStatus.AVAILABLE);

            InventoryLog log = InventoryLog.builder()
                    .product(product)
                    .changeType(InventoryChangeType.STOCK_OUT)
                    .quantityChange(request.getQuantity())
                    .previousQty(previousQty)
                    .newQty(newQty)
                    .reason("Order Reservation")
                    .referenceId("USER-" + userId)
                    .build();

            inventoryLogRepository.save(log);
            productRepository.save(product);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories")
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAsc()
                .stream().map(productMapper::toCategoryResponse).toList();
    }

    // ─── Helpers ────────────────────────────────────────────────────────────

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    private void assertOwner(Product product, Long farmerId) {
        if (!product.getFarmerId().equals(farmerId)) {
            throw new ForbiddenException("You do not own this product");
        }
    }

    private void validateImages(List<MultipartFile> images, Integer existingCount) {
        if (existingCount == null) {
            // New product — must have at least 1
            if (images == null || images.isEmpty()) {
                throw new ValidationException("At least 1 image is required");
            }
            if (images.size() > MAX_IMAGES) {
                throw new ValidationException("Maximum " + MAX_IMAGES + " images allowed per product");
            }
        } else {
            // Adding images to existing product
            if (existingCount + images.size() > MAX_IMAGES) {
                throw new ValidationException(
                        "Maximum " + MAX_IMAGES + " images allowed. Currently " + existingCount +
                        " image(s) exist. You can add " + (MAX_IMAGES - existingCount) + " more.");
            }
        }

        for (MultipartFile file : images) {
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new ValidationException("Image " + file.getOriginalFilename() + " exceeds 5MB limit");
            }
            if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
                throw new ValidationException("Only JPEG, PNG, and WebP images are allowed");
            }
        }
    }
}
