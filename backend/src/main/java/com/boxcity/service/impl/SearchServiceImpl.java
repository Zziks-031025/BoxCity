package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxcity.dto.GoodsListVO;
import com.boxcity.entity.*;
import com.boxcity.mapper.*;
import com.boxcity.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final GoodsMapper goodsMapper;
    private final GoodsImageMapper goodsImageMapper;
    private final SearchHistoryMapper searchHistoryMapper;
    private final SearchHotMapper searchHotMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPage<GoodsListVO> search(Long userId, String keyword, Integer page, Integer size) {
        // 保存搜索历史
        if (userId != null && StringUtils.hasText(keyword)) {
            // 先删除同 userId+keyword 的旧记录
            searchHistoryMapper.delete(
                    new LambdaQueryWrapper<SearchHistory>()
                            .eq(SearchHistory::getUserId, userId)
                            .eq(SearchHistory::getKeyword, keyword));
            // 插入新记录
            SearchHistory history = new SearchHistory();
            history.setUserId(userId);
            history.setKeyword(keyword);
            history.setCreatedAt(LocalDateTime.now());
            searchHistoryMapper.insert(history);
        }

        // 查询商品：status=1 AND auditStatus=1，title LIKE %keyword% OR description LIKE %keyword%
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
                .eq(Goods::getStatus, 1)
                .eq(Goods::getAuditStatus, 1);

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Goods::getTitle, keyword)
                    .or()
                    .like(Goods::getDescription, keyword));
        }

        wrapper.orderByDesc(Goods::getCreatedAt);

        Page<Goods> pageParam = new Page<>(page, size);
        IPage<Goods> goodsPage = goodsMapper.selectPage(pageParam, wrapper);

        return goodsPage.convert(this::toListVO);
    }

    @Override
    public List<String> getSearchHistory(Long userId) {
        List<SearchHistory> histories = searchHistoryMapper.selectList(
                new LambdaQueryWrapper<SearchHistory>()
                        .eq(SearchHistory::getUserId, userId)
                        .orderByDesc(SearchHistory::getCreatedAt)
                        .last("LIMIT 20"));
        return histories.stream()
                .map(SearchHistory::getKeyword)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearSearchHistory(Long userId) {
        searchHistoryMapper.delete(
                new LambdaQueryWrapper<SearchHistory>()
                        .eq(SearchHistory::getUserId, userId));
    }

    @Override
    public List<String> getHotKeywords() {
        List<SearchHot> hotList = searchHotMapper.selectList(
                new LambdaQueryWrapper<SearchHot>()
                        .eq(SearchHot::getStatus, 1)
                        .orderByAsc(SearchHot::getSort)
                        .last("LIMIT 10"));
        return hotList.stream()
                .map(SearchHot::getKeyword)
                .collect(Collectors.toList());
    }

    private GoodsListVO toListVO(Goods goods) {
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

        // 主图：type=1，sort 最小的第一张
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
