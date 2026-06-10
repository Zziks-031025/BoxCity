package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxcity.common.BusinessException;
import com.boxcity.dto.ReviewCreateRequest;
import com.boxcity.dto.ReviewVO;
import com.boxcity.entity.Order;
import com.boxcity.entity.OrderItem;
import com.boxcity.entity.Review;
import com.boxcity.entity.User;
import com.boxcity.mapper.OrderItemMapper;
import com.boxcity.mapper.OrderMapper;
import com.boxcity.mapper.ReviewMapper;
import com.boxcity.mapper.UserMapper;
import com.boxcity.service.ReviewService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReview(Long userId, ReviewCreateRequest request) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, request.getOrderNo())
                        .last("LIMIT 1"));
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(403, "无权评价该订单");
        }
        if (order.getStatus() == null || order.getStatus() != 3) {
            throw new BusinessException("只有已完成订单可以评价");
        }

        OrderItem orderItem = orderItemMapper.selectById(request.getOrderItemId());
        if (orderItem == null || !order.getId().equals(orderItem.getOrderId())) {
            throw new BusinessException("订单商品不存在");
        }

        Long parentId = resolveParentReviewId(userId, orderItem.getId(), request);
        boolean isAppend = request.getIsAppend() != null && request.getIsAppend() == 1;
        if (!isAppend && hasReviewed(orderItem.getId())) {
            throw new BusinessException("该商品已评价，如需补充请使用追评");
        }
        if (isAppend && parentId == null) {
            throw new BusinessException("请先提交首评后再追评");
        }

        Review review = new Review();
        review.setOrderId(order.getId());
        review.setOrderItemId(orderItem.getId());
        review.setUserId(userId);
        review.setGoodsId(orderItem.getGoodsId());
        review.setMerchantId(order.getMerchantId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(toJson(request.getImages()));
        review.setIsAppend(isAppend ? 1 : 0);
        review.setParentId(parentId == null ? 0L : parentId);
        review.setDeleted(0);
        reviewMapper.insert(review);
        return review.getId();
    }

    @Override
    public IPage<ReviewVO> getGoodsReviews(Long goodsId, Integer page, Integer size) {
        Page<Review> pageParam = new Page<>(page, size);
        IPage<Review> reviewPage = reviewMapper.selectPage(
                pageParam,
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getGoodsId, goodsId)
                        .orderByDesc(Review::getCreatedAt)
                        .orderByDesc(Review::getId));
        return reviewPage.convert(this::toVO);
    }

    @Override
    public List<ReviewVO> getLatestGoodsReviews(Long goodsId, Integer limit) {
        int fetchLimit = limit == null || limit < 1 ? 3 : limit;
        List<Review> reviews = reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getGoodsId, goodsId)
                        .orderByDesc(Review::getCreatedAt)
                        .orderByDesc(Review::getId)
                        .last("LIMIT " + fetchLimit));
        List<ReviewVO> result = new ArrayList<>();
        for (Review review : reviews) {
            result.add(toVO(review));
        }
        return result;
    }

    @Override
    public Integer countByGoodsId(Long goodsId) {
        return Math.toIntExact(reviewMapper.selectCount(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getGoodsId, goodsId)));
    }

    @Override
    public Double avgRatingByGoodsId(Long goodsId) {
        List<Review> reviews = reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getGoodsId, goodsId));
        if (reviews.isEmpty()) {
            return 0D;
        }
        double total = 0D;
        for (Review review : reviews) {
            total += review.getRating() == null ? 0 : review.getRating();
        }
        return Math.round(total * 10D / reviews.size()) / 10D;
    }

    @Override
    public boolean hasReviewed(Long orderItemId) {
        return reviewMapper.selectCount(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getOrderItemId, orderItemId)
                        .eq(Review::getIsAppend, 0)) > 0;
    }

    private Long resolveParentReviewId(Long userId, Long orderItemId, ReviewCreateRequest request) {
        boolean isAppend = request.getIsAppend() != null && request.getIsAppend() == 1;
        if (!isAppend) {
            return 0L;
        }
        if (request.getParentId() != null && request.getParentId() > 0) {
            Review parent = reviewMapper.selectById(request.getParentId());
            if (parent != null && userId.equals(parent.getUserId()) && orderItemId.equals(parent.getOrderItemId())) {
                return parent.getId();
            }
        }
        Review parent = reviewMapper.selectOne(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getOrderItemId, orderItemId)
                        .eq(Review::getUserId, userId)
                        .eq(Review::getIsAppend, 0)
                        .last("LIMIT 1"));
        return parent == null ? null : parent.getId();
    }

    private ReviewVO toVO(Review review) {
        ReviewVO vo = new ReviewVO();
        vo.setId(review.getId());
        vo.setOrderId(review.getOrderId());
        vo.setOrderItemId(review.getOrderItemId());
        vo.setGoodsId(review.getGoodsId());
        vo.setMerchantId(review.getMerchantId());
        vo.setRating(review.getRating());
        vo.setContent(review.getContent());
        vo.setImages(parseImages(review.getImages()));
        vo.setIsAppend(review.getIsAppend());
        vo.setParentId(review.getParentId());
        vo.setCreatedAt(review.getCreatedAt());

        User user = userMapper.selectById(review.getUserId());
        if (user != null) {
            vo.setUserName(maskName(user.getNickname()));
            vo.setUserAvatar(user.getAvatar());
        } else {
            vo.setUserName("匿名用户");
        }
        return vo;
    }

    private String maskName(String nickname) {
        if (!StringUtils.hasText(nickname)) {
            return "匿名用户";
        }
        if (nickname.length() == 1) {
            return nickname + "*";
        }
        return nickname.charAt(0) + "***";
    }

    private List<String> parseImages(String images) {
        if (!StringUtils.hasText(images)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(images, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            List<String> fallback = new ArrayList<>();
            fallback.add(images);
            return fallback;
        }
    }

    private String toJson(List<String> images) {
        try {
            return objectMapper.writeValueAsString(images == null ? new ArrayList<>() : images);
        } catch (JsonProcessingException e) {
            log.error("评价图片序列化失败", e);
            return "[]";
        }
    }
}
