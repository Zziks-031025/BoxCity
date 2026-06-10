package com.boxcity.task;

import com.boxcity.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutTask {

    private final OrderService orderService;

    /**
     * 每分钟执行一次，检查超时未支付订单
     * 查找 status=0 且 autoCancelTime < now 的订单，批量取消并恢复库存
     */
    @Scheduled(fixedRate = 60000)
    public void cancelTimeoutOrders() {
        log.info("执行订单超时取消任务");
        orderService.cancelTimeoutOrders();
    }

    /**
     * 每小时执行一次，检查超时未确认收货订单（发货后15天）
     * 查找 status=2 且 shipTime < now-15天 的订单，自动确认收货
     */
    @Scheduled(fixedRate = 3600000)
    public void autoConfirmOrders() {
        log.info("执行自动确认收货任务");
        orderService.autoConfirmOrders();
    }
}
