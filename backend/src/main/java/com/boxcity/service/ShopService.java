package com.boxcity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.dto.GoodsListVO;
import com.boxcity.dto.ShopVO;

public interface ShopService {

    ShopVO getShopDetail(Long merchantId);

    IPage<GoodsListVO> getShopGoods(Long merchantId, Integer page, Integer size);
}
