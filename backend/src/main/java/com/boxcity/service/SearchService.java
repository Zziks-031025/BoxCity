package com.boxcity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.dto.GoodsListVO;

import java.util.List;

public interface SearchService {

    /**
     * 搜索商品，同时记录搜索历史
     */
    IPage<GoodsListVO> search(Long userId, String keyword, Integer page, Integer size);

    /**
     * 获取用户最近20条搜索历史
     */
    List<String> getSearchHistory(Long userId);

    /**
     * 清空用户所有搜索历史
     */
    void clearSearchHistory(Long userId);

    /**
     * 获取热门搜索关键词（前10条）
     */
    List<String> getHotKeywords();
}
