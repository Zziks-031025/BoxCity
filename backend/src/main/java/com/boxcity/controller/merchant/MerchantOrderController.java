package com.boxcity.controller.merchant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.OrderListVO;
import com.boxcity.dto.OrderVO;
import com.boxcity.dto.ShipOrderRequest;
import com.boxcity.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/merchant/order")
@RequiredArgsConstructor
public class MerchantOrderController {

    private final OrderService orderService;

    /**
     * 商家订单列表
     * GET /api/merchant/order/list?status=&page=1&size=10
     */
    @GetMapping("/list")
    public Result<IPage<OrderListVO>> list(HttpServletRequest request,
                                           @RequestParam(required = false) Integer status,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(orderService.getMerchantOrderList(merchantId, status, page, size));
    }

    /**
     * 商家订单详情
     * GET /api/merchant/order/{orderNo}
     */
    @GetMapping("/{orderNo}")
    public Result<OrderVO> detail(HttpServletRequest request,
                                  @PathVariable String orderNo) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(orderService.getMerchantOrderDetail(merchantId, orderNo));
    }

    /**
     * 商家发货
     * POST /api/merchant/order/{orderNo}/ship
     */
    @PostMapping("/{orderNo}/ship")
    public Result<Void> ship(HttpServletRequest request,
                             @PathVariable String orderNo,
                             @Validated @RequestBody ShipOrderRequest body) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        orderService.shipOrder(merchantId, orderNo, body);
        return Result.success();
    }
}
