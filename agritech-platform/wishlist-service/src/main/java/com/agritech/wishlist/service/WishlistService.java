package com.agritech.wishlist.service;

import com.agritech.wishlist.dto.request.AddWishlistRequest;
import com.agritech.wishlist.dto.response.WishlistResponse;

import java.util.List;

public interface WishlistService {
    List<WishlistResponse> getUserWishlist(Long userId);
    WishlistResponse addToWishlist(Long userId, AddWishlistRequest request);
    void removeFromWishlist(Long userId, Long productId);
    boolean isInWishlist(Long userId, Long productId);
}
