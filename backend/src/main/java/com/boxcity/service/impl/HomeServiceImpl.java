package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxcity.dto.CategoryVO;
import com.boxcity.dto.GoodsListVO;
import com.boxcity.dto.HomeDataVO;
import com.boxcity.entity.*;
import com.boxcity.mapper.*;
import com.boxcity.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final BannerMapper bannerMapper;
    private final GoodsMapper goodsMapper;
    private final GoodsImageMapper goodsImageMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public HomeDataVO getHomeData(Long userId) {
        HomeDataVO vo = new HomeDataVO();

        // 1. 轮播图：status=1，按 sort 升序
        List<Banner> banners = bannerMapper.selectList(
                new LambdaQueryWrapper<Banner>()
                        .eq(Banner::getStatus, 1)
                        .orderByAsc(Banner::getSort));
        vo.setBanners(banners);

        // 2. 获取用户城市
        Long cityId = null;
        if (userId != null) {
            User user = userMapper.selectById(userId);
            if (user != null) {
                cityId = user.getCityId();
            }
        }

        // 3. 同城推荐：status=1 AND auditStatus=1 AND cityId=用户cityId，按 sales DESC 取前10
        List<GoodsListVO> recommended = new ArrayList<>();
        if (cityId != null) {
            List<Goods> cityGoods = goodsMapper.selectList(
                    new LambdaQueryWrapper<Goods>()
                            .eq(Goods::getStatus, 1)
                            .eq(Goods::getAuditStatus, 1)
                            .eq(Goods::getCityId, cityId)
                            .orderByDesc(Goods::getSales)
                            .last("LIMIT 10"));
            recommended = cityGoods.stream()
                    .map(this::toListVO)
                    .collect(Collectors.toList());
        }
        vo.setRecommended(recommended);

        // 4. 热门商品：status=1 AND auditStatus=1，按 sales DESC 取前20
        List<Goods> hotList = goodsMapper.selectList(
                new LambdaQueryWrapper<Goods>()
                        .eq(Goods::getStatus, 1)
                        .eq(Goods::getAuditStatus, 1)
                        .orderByDesc(Goods::getSales)
                        .last("LIMIT 20"));
        List<GoodsListVO> hotGoods = hotList.stream()
                .map(this::toListVO)
                .collect(Collectors.toList());
        vo.setHotGoods(hotGoods);

        // 5. 分类导航：一级分类（parentId=0, status=1），按 sort 排序，取前8个
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getParentId, 0L)
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSort)
                        .last("LIMIT 8"));
        List<CategoryVO> categoryVOs = categories.stream()
                .map(this::toCategoryVO)
                .collect(Collectors.toList());
        vo.setCategories(categoryVOs);

        return vo;
    }

    private GoodsListVO toListVO(Goods goods) {
        GoodsListVO listVO = new GoodsListVO();
        listVO.setId(goods.getId());
        listVO.setMerchantId(goods.getMerchantId());
        listVO.setTitle(goods.getTitle());
        listVO.setPrice(goods.getPrice());
        listVO.setStock(goods.getStock());
        listVO.setSales(goods.getSales());
        listVO.setAuditStatus(goods.getAuditStatus());
        listVO.setStatus(goods.getStatus());
        listVO.setCreatedAt(goods.getCreatedAt());

        // 主图：type=1，sort 最小的第一张
        GoodsImage mainImage = goodsImageMapper.selectOne(
                new LambdaQueryWrapper<GoodsImage>()
                        .eq(GoodsImage::getGoodsId, goods.getId())
                        .eq(GoodsImage::getType, 1)
                        .orderByAsc(GoodsImage::getSort)
                        .last("LIMIT 1"));
        if (mainImage != null) {
            listVO.setMainImage(mainImage.getImageUrl());
        }

        return listVO;
    }

    private CategoryVO toCategoryVO(Category category) {
        CategoryVO cvo = new CategoryVO();
        cvo.setId(category.getId());
        cvo.setParentId(category.getParentId());
        cvo.setName(category.getName());
        cvo.setSort(category.getSort());
        return cvo;
    }
}
