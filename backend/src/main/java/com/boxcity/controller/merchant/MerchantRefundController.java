package com.boxcity.controller.merchant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.MerchantRefundHandleRequest;
import com.boxcity.dto.RefundVO;
import com.boxcity.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/merchant/refund")
@RequiredArgsConstructor
public class MerchantRefundController {

    private final RefundService refundService;

    @GetMapping("/list")
    public Result<IPage<RefundVO>> list(HttpServletRequest request,
                                        @RequestParam(required = false) Integer status,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(refundService.getMerchantRefundList(merchantId, status, page, size));
    }

    @GetMapping("/{refundId}")
    public Result<RefundVO> detail(HttpServletRequest request,
                                   @PathVariable Long refundId) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(refundService.getMerchantRefundDetail(merchantId, refundId));
    }

    @PutMapping("/{refundId}/handle")
    public Result<Void> handle(HttpServletRequest request,
                               @PathVariable Long refundId,
                               @Validated @RequestBody MerchantRefundHandleRequest body) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        refundService.handleRefund(merchantId, refundId, Boolean.TRUE.equals(body.getAgree()), body.getRejectReason());
        return Result.success();
    }

    @PutMapping("/{refundId}/confirm")
    public Result<Void> confirm(HttpServletRequest request,
                                @PathVariable Long refundId) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        refundService.confirmReturnReceived(merchantId, refundId);
        return Result.success();
    }
}
