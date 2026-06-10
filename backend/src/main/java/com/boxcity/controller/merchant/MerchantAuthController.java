package com.boxcity.controller.merchant;

import com.boxcity.common.Result;
import com.boxcity.dto.MerchantApplyRequest;
import com.boxcity.dto.MerchantLoginRequest;
import com.boxcity.dto.MerchantLoginResponse;
import com.boxcity.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchant/auth")
@RequiredArgsConstructor
public class MerchantAuthController {

    private final MerchantService merchantService;

    @PostMapping("/apply")
    public Result<Void> apply(@Validated @RequestBody MerchantApplyRequest request) {
        merchantService.apply(request);
        return Result.success("申请已提交，请等待审核", null);
    }

    @PostMapping("/login")
    public Result<MerchantLoginResponse> login(@Validated @RequestBody MerchantLoginRequest request) {
        MerchantLoginResponse response = merchantService.login(request);
        return Result.success(response);
    }
}
