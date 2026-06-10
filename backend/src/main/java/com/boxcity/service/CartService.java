package com.boxcity.service;

import com.boxcity.dto.CartVO;

import java.util.List;

public interface CartService {

    List<CartVO> getCartList(Long userId);

    void addToCart(Long userId, Long goodsId, Integer quantity);

    void updateQuantity(Long userId, Long cartId, Integer quantity);

    void removeFromCart(Long userId, Long cartId);

    /** 下单后清除购物车中指定商品 */
    void clearCart(Long userId, List<Long> goodsIds);
}
