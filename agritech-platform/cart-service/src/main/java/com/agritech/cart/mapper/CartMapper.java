package com.agritech.cart.mapper;

import com.agritech.cart.dto.response.CartItemResponse;
import com.agritech.cart.dto.response.CartResponse;
import com.agritech.cart.entity.Cart;
import com.agritech.cart.entity.CartItem;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "totalAmount", expression = "java(calculateTotal(cart.getItems()))")
    CartResponse toResponse(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(item.getUnitPrice().multiply(new java.math.BigDecimal(item.getQuantity())))")
    CartItemResponse toItemResponse(CartItem item);

    default BigDecimal calculateTotal(List<CartItem> items) {
        if (items == null || items.isEmpty()) return BigDecimal.ZERO;
        return items.stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
