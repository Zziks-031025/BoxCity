package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxcity.common.BusinessException;
import com.boxcity.dto.GoodsImageDTO;
import com.boxcity.dto.GoodsListVO;
import com.boxcity.dto.GoodsPublishDTO;
import com.boxcity.dto.GoodsVO;
import com.boxcity.entity.Category;
import com.boxcity.entity.City;
import com.boxcity.entity.Goods;
import com.boxcity.entity.GoodsAttribute;
import com.boxcity.entity.GoodsImage;
import com.boxcity.entity.Merchant;
import com.boxcity.mapper.CategoryMapper;
import com.boxcity.mapper.CityMapper;
import com.boxcity.mapper.GoodsAttributeMapper;
import com.boxcity.mapper.GoodsImageMapper;
import com.boxcity.mapper.GoodsMapper;
import com.boxcity.mapper.MerchantMapper;
import com.boxcity.service.GoodsService;
import com.boxcity.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    private final GoodsMapper goodsMapper;
    private final GoodsImageMapper goodsImageMapper;
    private final GoodsAttributeMapper goodsAttributeMapper;
    private final MerchantMapper merchantMapper;
    private final CityMapper cityMapper;
    private final CategoryMapper categoryMapper;
    private final ReviewService reviewService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishGoods(Long merchantId, GoodsPublishDTO dto) {
        Goods goods = buildGoods(dto);
        goods.setMerchantId(merchantId);
        goods.setAuditStatus(0);
        goods.setStatus(0);
        goods.setSales(0);
        goods.setViews(0);
        goodsMapper.insert(goods);
        saveImages(goods.getId(), dto.getImages());
        saveAttributes(goods.getId(), dto.getAttributes());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGoods(Long merchantId, Long goodsId, GoodsPublishDTO dto) {
        Goods goods = getAndVerifyOwner(merchantId, goodsId);
        goods.setCategoryId(dto.getCategoryId());
        goods.setTitle(dto.getTitle());
        goods.setDescription(dto.getDescription());
        goods.setPrice(dto.getPrice());
        goods.setOriginalPrice(dto.getOriginalPrice());
        goods.setStock(dto.getStock());
        goods.setCityId(dto.getCityId());
        goods.setAuditStatus(0);
        goods.setStatus(0);
        goodsMapper.updateById(goods);

        goodsImageMapper.delete(new LambdaQueryWrapper<GoodsImage>().eq(GoodsImage::getGoodsId, goodsId));
        goodsAttributeMapper.delete(new LambdaQueryWrapper<GoodsAttribute>().eq(GoodsAttribute::getGoodsId, goodsId));
        saveImages(goodsId, dto.getImages());
        saveAttributes(goodsId, dto.getAttributes());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGoods(Long merchantId, Long goodsId) {
        Goods goods = getAndVerifyOwner(merchantId, goodsId);
        if (goods.getStatus() != null && goods.getStatus() == 1) {
            throw new BusinessException("请先下架商品再删除");
        }
        goodsMapper.deleteById(goodsId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long merchantId, Long goodsId, Integer status) {
        Goods goods = getAndVerifyOwner(merchantId, goodsId);
        if (status != null && status == 1 && (goods.getAuditStatus() == null || goods.getAuditStatus() != 1)) {
            throw new BusinessException("商品未通过审核，无法上架");
        }
        goods.setStatus(status);
        goodsMapper.updateById(goods);
    }

    @Override
    public GoodsVO getMerchantGoodsDetail(Long merchantId, Long goodsId) {
        Goods goods = getAndVerifyOwner(merchantId, goodsId);
        return getGoodsDetail(goods.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStock(Long merchantId, Long goodsId, Integer stock) {
        Goods goods = getAndVerifyOwner(merchantId, goodsId);
        goods.setStock(stock);
        goodsMapper.updateById(goods);
    }

    @Override
    public IPage<GoodsListVO> merchantGoodsList(Long merchantId, Integer auditStatus, Integer status, Integer page, Integer size) {
        IPage<Goods> goodsPage = goodsMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Goods>()
                        .eq(Goods::getMerchantId, merchantId)
                        .eq(auditStatus != null, Goods::getAuditStatus, auditStatus)
                        .eq(status != null, Goods::getStatus, status)
                        .orderByDesc(Goods::getCreatedAt));
        return goodsPage.convert(goods -> toListVO(goods, true));
    }

    @Override
    public IPage<GoodsListVO> appGoodsList(Long categoryId, Long cityId, BigDecimal minPrice, BigDecimal maxPrice, String sort, Integer page, Integer size) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
                .eq(Goods::getStatus, 1)
                .eq(Goods::getAuditStatus, 1)
                .eq(categoryId != null, Goods::getCategoryId, categoryId)
                .eq(cityId != null, Goods::getCityId, cityId)
                .ge(minPrice != null, Goods::getPrice, minPrice)
                .le(maxPrice != null, Goods::getPrice, maxPrice);
        if ("sales".equals(sort)) {
            wrapper.orderByDesc(Goods::getSales);
        } else if ("price_asc".equals(sort)) {
            wrapper.orderByAsc(Goods::getPrice);
        } else if ("price_desc".equals(sort)) {
            wrapper.orderByDesc(Goods::getPrice);
        } else {
            wrapper.orderByDesc(Goods::getCreatedAt);
        }

        IPage<Goods> goodsPage = goodsMapper.selectPage(new Page<>(page, size), wrapper);
        return goodsPage.convert(goods -> toListVO(goods, false));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GoodsVO getGoodsDetail(Long goodsId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException(404, "商品不存在");
        }

        GoodsVO vo = new GoodsVO();
        vo.setId(goods.getId());
        vo.setMerchantId(goods.getMerchantId());
        vo.setCategoryId(goods.getCategoryId());
        vo.setTitle(goods.getTitle());
        vo.setDescription(goods.getDescription());
        vo.setPrice(goods.getPrice());
        vo.setOriginalPrice(goods.getOriginalPrice());
        vo.setStock(goods.getStock());
        vo.setSales(goods.getSales());
        vo.setCityId(goods.getCityId());
        vo.setAuditStatus(goods.getAuditStatus());
        vo.setAuditRemark(goods.getAuditRemark());
        vo.setStatus(goods.getStatus());
        vo.setViews(goods.getViews());
        vo.setCreatedAt(goods.getCreatedAt());

        vo.setImages(goodsImageMapper.selectList(
                new LambdaQueryWrapper<GoodsImage>()
                        .eq(GoodsImage::getGoodsId, goodsId)
                        .orderByAsc(GoodsImage::getSort)));
        vo.setAttributes(goodsAttributeMapper.selectList(
                new LambdaQueryWrapper<GoodsAttribute>()
                        .eq(GoodsAttribute::getGoodsId, goodsId)));

        Merchant merchant = merchantMapper.selectById(goods.getMerchantId());
        if (merchant != null) {
            vo.setShopName(merchant.getShopName());
        }
        if (goods.getCityId() != null) {
            City city = cityMapper.selectById(goods.getCityId());
            if (city != null) {
                vo.setCityName(city.getName());
            }
        }
        if (goods.getCategoryId() != null) {
            Category category = categoryMapper.selectById(goods.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }

        vo.setReviewCount(reviewService.countByGoodsId(goodsId));
        vo.setAvgRating(reviewService.avgRatingByGoodsId(goodsId));
        vo.setReviews(reviewService.getLatestGoodsReviews(goodsId, 3));

        Goods update = new Goods();
        update.setId(goodsId);
        update.setViews(goods.getViews() == null ? 1 : goods.getViews() + 1);
        goodsMapper.updateById(update);

        return vo;
    }

    private Goods getAndVerifyOwner(Long merchantId, Long goodsId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException(404, "商品不存在");
        }
        if (!merchantId.equals(goods.getMerchantId())) {
            throw new BusinessException(403, "无权操作该商品");
        }
        return goods;
    }

    private Goods buildGoods(GoodsPublishDTO dto) {
        Goods goods = new Goods();
        goods.setCategoryId(dto.getCategoryId());
        goods.setTitle(dto.getTitle());
        goods.setDescription(dto.getDescription());
        goods.setPrice(dto.getPrice());
        goods.setOriginalPrice(dto.getOriginalPrice());
        goods.setStock(dto.getStock());
        goods.setCityId(dto.getCityId());
        return goods;
    }

    private void saveImages(Long goodsId, List<GoodsImageDTO> imageDTOs) {
        if (CollectionUtils.isEmpty(imageDTOs)) {
            return;
        }
        for (GoodsImageDTO dto : imageDTOs) {
            GoodsImage image = new GoodsImage();
            image.setGoodsId(goodsId);
            image.setImageUrl(dto.getImageUrl());
            image.setType(dto.getType() == null ? 1 : dto.getType());
            image.setSort(dto.getSort() == null ? 0 : dto.getSort());
            goodsImageMapper.insert(image);
        }
    }

    private void saveAttributes(Long goodsId, List<String> attributeNames) {
        if (CollectionUtils.isEmpty(attributeNames)) {
            return;
        }
        for (String name : attributeNames) {
            if (StringUtils.hasText(name)) {
                GoodsAttribute attr = new GoodsAttribute();
                attr.setGoodsId(goodsId);
                attr.setAttributeName(name);
                goodsAttributeMapper.insert(attr);
            }
        }
    }

    private GoodsListVO toListVO(Goods goods, boolean includeShopName) {
        GoodsListVO vo = new GoodsListVO();
        vo.setId(goods.getId());
        vo.setMerchantId(goods.getMerchantId());
        vo.setTitle(goods.getTitle());
        vo.setPrice(goods.getPrice());
        vo.setStock(goods.getStock());
        vo.setSales(goods.getSales());
        vo.setAuditStatus(goods.getAuditStatus());
        vo.setStatus(goods.getStatus());
        vo.setCreatedAt(goods.getCreatedAt());

        if (includeShopName) {
            Merchant merchant = merchantMapper.selectById(goods.getMerchantId());
            if (merchant != null) {
                vo.setShopName(merchant.getShopName());
            }
        }

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
