package com.agritech.product.specification;

import com.agritech.product.entity.Product;
import com.agritech.product.enums.ApprovalStatus;
import com.agritech.product.enums.ProductStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * JPA Specification predicates for dynamic Product filtering.
 * Used by ProductService.searchProducts() to build type-safe queries.
 */
public final class ProductSpecification {

    private ProductSpecification() {}

    public static Specification<Product> isApproved() {
        return (root, query, cb) ->
                cb.equal(root.get("approvalStatus"), ApprovalStatus.APPROVED);
    }

    public static Specification<Product> isAvailable() {
        return (root, query, cb) ->
                cb.equal(root.get("productStatus"), ProductStatus.AVAILABLE);
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        if (categoryId == null) return null;
        return (root, query, cb) ->
                cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> hasFarmer(Long farmerId) {
        if (farmerId == null) return null;
        return (root, query, cb) ->
                cb.equal(root.get("farmerId"), farmerId);
    }

    public static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
        if (min == null && max == null) return null;
        return (root, query, cb) -> {
            if (min != null && max != null) return cb.between(root.get("price"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.lessThanOrEqualTo(root.get("price"), max);
        };
    }

    public static Specification<Product> inCity(String city) {
        if (!StringUtils.hasText(city)) return null;
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%");
    }

    public static Specification<Product> inState(String state) {
        if (!StringUtils.hasText(state)) return null;
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("state")), "%" + state.toLowerCase() + "%");
    }

    public static Specification<Product> isOrganic(Boolean organic) {
        if (organic == null) return null;
        return (root, query, cb) ->
                cb.equal(root.get("organic"), organic);
    }

    public static Specification<Product> minRating(Double minRating) {
        if (minRating == null) return null;
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("avgRating").as(Double.class), minRating);
    }

    /**
     * Keyword search using JPA LIKE on name and description.
     * FULLTEXT matching happens via MySQL index — LIKE provides portability.
     */
    public static Specification<Product> searchByKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) return null;
        return (root, query, cb) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }
}
