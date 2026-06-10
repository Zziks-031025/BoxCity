package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.boxcity.common.BusinessException;
import com.boxcity.dto.CartVO;
import com.boxcity.entity.Cart;
import com.boxcity.entity.Goods;
import com.boxcity.entity.GoodsImage;
import com.boxcity.entity.Merchant;
import com.boxcity.mapper.CartMapper;
import com.boxcity.mapper.GoodsImageMapper;
import com.boxcity.mapper.GoodsMapper;
import com.boxcity.mapper.MerchantMapper;
import com.boxcity.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final GoodsMapper goodsMapper;
    private final GoodsImageMapper goodsImageMapper;
    private final MerchantMapper merchantMapper;

    @Override
    public List<CartVO> getCartList(Long userId) {
        List<Cart> carts = cartMapper.selectList(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
                        .orderByDesc(Cart::getCreatedAt));

        List<CartVO> result = new ArrayList<>();
        for (Cart cart : carts) {
            Goods goods = goodsMapper.selectById(cart.getGoodsId());
            if (goods == null) {
                continue;
            }

            CartVO vo = new CartVO();
            vo.setId(cart.getId());
            vo.setGoodsId(cart.getGoodsId());
            vo.setMerchantId(cart.getMerchantId());
            vo.setGoodsTitle(goods.getTitle());
            vo.setPrice(goods.getPrice());
            vo.setStock(goods.getStock());
            vo.setGoodsStatus(goods.getStatus());
            vo.setQuantity(cart.getQuantity());

            // 主图
            GoodsImage mainImage = goodsImageMapper.selectOne(
                    new LambdaQueryWrapper<GoodsImage>()
                            .eq(GoodsImage::getGoodsId, goods.getId())
                            .eq(GoodsImage::getType, 1)
                            .orderByAsc(GoodsImage::getSort)
                            .last("LIMIT 1"));
            if (mainImage != null) {
                vo.setMainImage(mainImage.getImageUrl());
            }

            // 商家名称
            Merchant merchant = merchantMapper.selectById(cart.getMerchantId());
            if (merchant != null) {
                vo.setShopName(merchant.getShopName());
            }

            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToCart(Long userId, Long goodsId, Integer quantity) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null || goods.getDeleted() != null && goods.getDeleted() == 1) {
            throw new BusinessException("商品不存在");
        }
        if (goods.getStatus() == null || goods.getStatus() != 1) {
            throw new BusinessException("商品已下架");
        }
        if (goods.getStock() == null || goods.getStock() < quantity) {
            throw new BusinessException("商品库存不足");
        }

        // 检查购物车是否已有该商品
        Cart existing = cartMapper.selectOne(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
                        .eq(Cart::getGoodsId, goodsId));

        if (existing != null) {
            int newQty = existing.getQuantity() + quantity;
            if (newQty > goods.getStock()) {
                throw new BusinessException("购物车数量超出库存");
            }
            existing.setQuantity(newQty);
            cartMapper.updateById(existing);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setGoodsId(goodsId);
            cart.setMerchantId(goods.getMerchantId());
            cart.setQuantity(quantity);
            cartMapper.insert(cart);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(Long userId, Long cartId, Integer quantity) {
        Cart cart = getAndVerifyOwner(userId, cartId);

        Goods goods = goodsMapper.selectById(cart.getGoodsId());
        if (goods != null && goods.getStock() != null && quantity > goods.getStock()) {
            throw new BusinessException("数量不能超过库存");
        }

        cart.setQuantity(quantity);
        cartMapper.updateById(cart);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFromCart(Long userId, Long cartId) {
        getAndVerifyOwner(userId, cartId);
        cartMapper.deleteById(cartId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearCart(Long userId, List<Long> goodsIds) {
        if (CollectionUtils.isEmpty(goodsIds)) {
            return;
        }
        cartMapper.delete(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .in(Cart::getGoodsId, goodsIds));
    }

    private Cart getAndVerifyOwner(Long userId, Long cartId) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null) {
            throw new BusinessException(404, "购物车记录不存在");
        }
        if (!userId.equals(cart.getUserId())) {
            throw new BusinessException(403, "无权操作该购物车记录");
        }
        return cart;
    }
}
