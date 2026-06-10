package com.boxcity.controller.app;

import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.RefundApplyRequest;
import com.boxcity.dto.RefundVO;
import com.boxcity.dto.ReturnLogisticsRequest;
import com.boxcity.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/app/refund")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping("/apply")
    public Result<Long> apply(HttpServletRequest request,
                              @Validated @RequestBody RefundApplyRequest body) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(refundService.applyRefund(userId, body));
    }

    @GetMapping("/{refundId}")
    public Result<RefundVO> detail(HttpServletRequest request,
                                   @PathVariable Long refundId) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(refundService.getRefundDetail(userId, refundId));
    }

    @PutMapping("/{refundId}/cancel")
    public Result<Void> cancel(HttpServletRequest request,
                               @PathVariable Long refundId) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        refundService.cancelRefund(userId, refundId);
        return Result.success();
    }

    @PutMapping("/{refundId}/logistics")
    public Result<Void> fillLogistics(HttpServletRequest request,
                                      @PathVariable Long refundId,
                                      @Validated @RequestBody ReturnLogisticsRequest body) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        refundService.fillReturnLogistics(userId, refundId, body.getLogisticsCompany(), body.getLogisticsNo());
        return Result.success();
    }

    @PutMapping("/{refundId}/intervene")
    public Result<Void> intervene(HttpServletRequest request,
                                  @PathVariable Long refundId,
                                  @RequestBody(required = false) Map<String, String> body) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        refundService.requestPlatformIntervene(userId, refundId, body == null ? null : body.get("reason"));
        return Result.success();
    }
}
