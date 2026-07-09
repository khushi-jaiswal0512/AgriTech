package com.agritech.product.constants;

/**
 * Redis cache key name constants for Product Service.
 */
public final class ProductCacheKeys {

    private ProductCacheKeys() {}

    public static final String PRODUCT = "product";
    public static final String CATEGORIES = "categories";
    public static final String PRODUCTS_POPULAR = "products:popular";
}
