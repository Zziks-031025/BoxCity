package com.boxcity.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.common.Result;
import com.boxcity.dto.GoodsListVO;
import com.boxcity.dto.ShopVO;
import com.boxcity.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/app/shop")
@RequiredArgsConstructor
public class AppShopController {

    private final ShopService shopService;

    @GetMapping("/{merchantId}")
    public Result<ShopVO> detail(@PathVariable Long merchantId) {
        return Result.success(shopService.getShopDetail(merchantId));
    }

    @GetMapping("/{merchantId}/goods")
    public Result<IPage<GoodsListVO>> goods(@PathVariable Long merchantId,
                                            @RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(shopService.getShopGoods(merchantId, page, size));
    }
}
