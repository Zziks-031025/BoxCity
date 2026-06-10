package com.boxcity.controller.app;

import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.LogisticsVO;
import com.boxcity.service.LogisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/app/logistics")
@RequiredArgsConstructor
public class LogisticsController {

    private final LogisticsService logisticsService;

    @GetMapping("/{orderNo}")
    public Result<LogisticsVO> detail(HttpServletRequest request,
                                      @PathVariable String orderNo) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(logisticsService.getLogistics(userId, orderNo));
    }
}
