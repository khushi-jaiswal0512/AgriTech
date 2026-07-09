package com.agritech.product.mapper;

import com.agritech.product.dto.response.CategoryResponse;
import com.agritech.product.dto.response.ProductImageResponse;
import com.agritech.product.dto.response.ProductResponse;
import com.agritech.product.entity.Category;
import com.agritech.product.entity.Product;
import com.agritech.product.entity.ProductImage;
import org.mapstruct.*;

/**
 * MapStruct mapper for Product, Category, and ProductImage entities.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductResponse toResponse(Product product);

    @Mapping(target = "parentId", source = "parent.id")
    CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "primary", source = "primary")
    ProductImageResponse toImageResponse(ProductImage image);
}
