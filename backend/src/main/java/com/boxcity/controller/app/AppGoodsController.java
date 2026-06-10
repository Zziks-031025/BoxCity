package com.boxcity.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.GoodsListVO;
import com.boxcity.dto.GoodsVO;
import com.boxcity.dto.HomeDataVO;
import com.boxcity.service.GoodsService;
import com.boxcity.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/app/goods")
@RequiredArgsConstructor
public class AppGoodsController {

    private final GoodsService goodsService;
    private final HomeService homeService;

    /**
     * 首页数据
     * GET /api/app/goods/home
     */
    @GetMapping("/home")
    public Result<HomeDataVO> getHomeData(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(homeService.getHomeData(userId));
    }

    /**
     * 商品列表（消费者端）
     * GET /api/app/goods/list?categoryId=&cityId=&minPrice=&maxPrice=&sort=newest&page=1&size=10
     */
    @GetMapping("/list")
    public Result<IPage<GoodsListVO>> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(goodsService.appGoodsList(categoryId, cityId, minPrice, maxPrice, sort, page, size));
    }

    /**
     * 商品详情
     * GET /api/app/goods/{id}
     */
    @GetMapping("/{id}")
    public Result<GoodsVO> detail(@PathVariable Long id) {
        return Result.success(goodsService.getGoodsDetail(id));
    }
}
