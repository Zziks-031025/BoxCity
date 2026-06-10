package com.boxcity.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.ReviewCreateRequest;
import com.boxcity.dto.ReviewVO;
import com.boxcity.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/app/review")
@RequiredArgsConstructor
public class AppReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Result<Long> create(HttpServletRequest request,
                               @Validated @RequestBody ReviewCreateRequest body) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(reviewService.createReview(userId, body));
    }

    @GetMapping("/goods/{goodsId}")
    public Result<IPage<ReviewVO>> listByGoods(@PathVariable Long goodsId,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reviewService.getGoodsReviews(goodsId, page, size));
    }
}
