package com.agritech.cart.service;

import com.agritech.cart.dto.request.AddToCartRequest;
import com.agritech.cart.dto.request.UpdateCartItemRequest;
import com.agritech.cart.dto.response.CartResponse;

public interface CartService {
    CartResponse getCart(Long userId);
    CartResponse addToCart(Long userId, AddToCartRequest request);
    CartResponse updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request);
    CartResponse removeCartItem(Long userId, Long itemId);
    void clearCart(Long userId);
}
