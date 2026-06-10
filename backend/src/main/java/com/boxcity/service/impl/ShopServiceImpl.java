package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxcity.common.BusinessException;
import com.boxcity.dto.GoodsListVO;
import com.boxcity.dto.ShopVO;
import com.boxcity.entity.Goods;
import com.boxcity.entity.GoodsImage;
import com.boxcity.entity.Merchant;
import com.boxcity.mapper.GoodsImageMapper;
import com.boxcity.mapper.GoodsMapper;
import com.boxcity.mapper.MerchantMapper;
import com.boxcity.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final MerchantMapper merchantMapper;
    private final GoodsMapper goodsMapper;
    private final GoodsImageMapper goodsImageMapper;

    @Override
    public ShopVO getShopDetail(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null || merchant.getAuditStatus() == null || merchant.getAuditStatus() != 1) {
            throw new BusinessException(404, "店铺不存在");
        }

        ShopVO vo = new ShopVO();
        vo.setId(merchant.getId());
        vo.setShopName(merchant.getShopName());
        vo.setShopAvatar(merchant.getShopAvatar());
        vo.setShopIntro(merchant.getShopIntro());
        vo.setShopNotice(merchant.getShopNotice());
        vo.setContactPhone(merchant.getContactPhone());
        vo.setContactEmail(merchant.getContactEmail());
        vo.setCreditScore(merchant.getCreditScore());
        vo.setCreatedAt(merchant.getCreatedAt());
        vo.setGoodsCount(countGoods(merchantId, false));
        vo.setOnSaleCount(countGoods(merchantId, true));
        return vo;
    }

    @Override
    public IPage<GoodsListVO> getShopGoods(Long merchantId, Integer page, Integer size) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null || merchant.getAuditStatus() == null || merchant.getAuditStatus() != 1) {
            throw new BusinessException(404, "店铺不存在");
        }

        IPage<Goods> goodsPage = goodsMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Goods>()
                        .eq(Goods::getMerchantId, merchantId)
                        .eq(Goods::getAuditStatus, 1)
                        .eq(Goods::getStatus, 1)
                        .orderByDesc(Goods::getCreatedAt));
        return goodsPage.convert(goods -> toGoodsListVO(goods, merchant.getShopName()));
    }

    private int countGoods(Long merchantId, boolean onSaleOnly) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
                .eq(Goods::getMerchantId, merchantId)
                .eq(Goods::getAuditStatus, 1);
        if (onSaleOnly) {
            wrapper.eq(Goods::getStatus, 1);
        }
        return Math.toIntExact(goodsMapper.selectCount(wrapper));
    }

    private GoodsListVO toGoodsListVO(Goods goods, String shopName) {
        GoodsListVO vo = new GoodsListVO();
        vo.setId(goods.getId());
        vo.setMerchantId(goods.getMerchantId());
        vo.setShopName(shopName);
        vo.setTitle(goods.getTitle());
        vo.setPrice(goods.getPrice());
        vo.setStock(goods.getStock());
        vo.setSales(goods.getSales());
        vo.setAuditStatus(goods.getAuditStatus());
        vo.setStatus(goods.getStatus());
        vo.setCreatedAt(goods.getCreatedAt());

        GoodsImage mainImage = goodsImageMapper.selectOne(
                new LambdaQueryWrapper<GoodsImage>()
                        .eq(GoodsImage::getGoodsId, goods.getId())
                        .eq(GoodsImage::getType, 1)
                        .orderByAsc(GoodsImage::getSort)
                        .last("LIMIT 1"));
        if (mainImage != null) {
            vo.setMainImage(mainImage.getImageUrl());
        }
        return vo;
    }
}
