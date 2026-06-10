package com.boxcity.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.CreateOrderRequest;
import com.boxcity.dto.OrderListVO;
import com.boxcity.dto.OrderPreviewVO;
import com.boxcity.dto.OrderVO;
import com.boxcity.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/app/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/preview")
    public Result<OrderPreviewVO> preview(HttpServletRequest request,
                                          @Validated @RequestBody CreateOrderRequest body) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(orderService.previewOrder(userId, body));
    }

    @PostMapping("/create")
    public Result<String> create(HttpServletRequest request,
                                 @Validated @RequestBody CreateOrderRequest body) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(orderService.createOrder(userId, body));
    }

    @GetMapping("/list")
    public Result<IPage<OrderListVO>> list(HttpServletRequest request,
                                           @RequestParam(required = false) Integer status,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(orderService.getOrderList(userId, status, page, size));
    }

    @GetMapping("/{orderNo}")
    public Result<OrderVO> detail(HttpServletRequest request,
                                  @PathVariable String orderNo) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(orderService.getOrderDetail(userId, orderNo));
    }

    @PostMapping("/{orderNo}/pay/mock")
    public Result<Void> mockPay(HttpServletRequest request,
                                @PathVariable String orderNo) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        orderService.mockPayOrder(userId, orderNo);
        return Result.success();
    }

    @PutMapping("/{orderNo}/cancel")
    public Result<Void> cancel(HttpServletRequest request,
                               @PathVariable String orderNo) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        orderService.cancelOrder(userId, orderNo);
        return Result.success();
    }

    @PutMapping("/{orderNo}/confirm")
    public Result<Void> confirm(HttpServletRequest request,
                                @PathVariable String orderNo) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        orderService.confirmReceive(userId, orderNo);
        return Result.success();
    }

    @DeleteMapping("/{orderNo}")
    public Result<Void> delete(HttpServletRequest request,
                               @PathVariable String orderNo) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        orderService.deleteOrder(userId, orderNo);
        return Result.success();
    }
}
