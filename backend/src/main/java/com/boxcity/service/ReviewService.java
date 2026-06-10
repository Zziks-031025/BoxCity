package com.boxcity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.dto.ReviewCreateRequest;
import com.boxcity.dto.ReviewVO;

import java.util.List;

public interface ReviewService {

    Long createReview(Long userId, ReviewCreateRequest request);

    IPage<ReviewVO> getGoodsReviews(Long goodsId, Integer page, Integer size);

    List<ReviewVO> getLatestGoodsReviews(Long goodsId, Integer limit);

    Integer countByGoodsId(Long goodsId);

    Double avgRatingByGoodsId(Long goodsId);

    boolean hasReviewed(Long orderItemId);
}
