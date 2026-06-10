package com.boxcity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.dto.GoodsListVO;
import com.boxcity.dto.GoodsPublishDTO;
import com.boxcity.dto.GoodsVO;

import java.math.BigDecimal;

public interface GoodsService {

    // 商家端

    void publishGoods(Long merchantId, GoodsPublishDTO dto);

    void updateGoods(Long merchantId, Long goodsId, GoodsPublishDTO dto);

    void deleteGoods(Long merchantId, Long goodsId);

    void updateStatus(Long merchantId, Long goodsId, Integer status);

    GoodsVO getMerchantGoodsDetail(Long merchantId, Long goodsId);

    void updateStock(Long merchantId, Long goodsId, Integer stock);

    IPage<GoodsListVO> merchantGoodsList(Long merchantId, Integer auditStatus, Integer status,
                                         Integer page, Integer size);

    // 消费者端（D18）

    IPage<GoodsListVO> appGoodsList(Long categoryId, Long cityId,
                                    BigDecimal minPrice, BigDecimal maxPrice,
                                    String sort, Integer page, Integer size);

    GoodsVO getGoodsDetail(Long goodsId);
}
