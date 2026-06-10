package com.boxcity.controller.merchant;

import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.MerchantDashboardVO;
import com.boxcity.service.MerchantDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/merchant/dashboard")
@RequiredArgsConstructor
public class MerchantDashboardController {

    private final MerchantDashboardService merchantDashboardService;

    @GetMapping
    public Result<MerchantDashboardVO> dashboard(HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(merchantDashboardService.getDashboard(merchantId));
    }
}
