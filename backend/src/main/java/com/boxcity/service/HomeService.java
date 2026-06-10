package com.boxcity.service;

import com.boxcity.dto.HomeDataVO;

public interface HomeService {

    /**
     * 获取首页数据
     *
     * @param userId 用户ID，用于获取用户城市以推荐同城商品
     * @return 首页聚合数据
     */
    HomeDataVO getHomeData(Long userId);
}
