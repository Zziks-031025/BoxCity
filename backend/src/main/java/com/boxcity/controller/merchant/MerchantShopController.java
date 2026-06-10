package com.boxcity.controller.merchant;

import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.MerchantInfoVO;
import com.boxcity.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/merchant/shop")
@RequiredArgsConstructor
public class MerchantShopController {

    private final MerchantService merchantService;

    @GetMapping("/info")
    public Result<MerchantInfoVO> getInfo(HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(merchantService.getInfo(merchantId));
    }

    @PutMapping("/info")
    public Result<Void> updateInfo(HttpServletRequest request, @RequestBody MerchantInfoVO vo) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        merchantService.updateInfo(merchantId, vo);
        return Result.success();
    }
}
