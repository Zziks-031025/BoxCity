package com.boxcity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.dto.CreateOrderRequest;
import com.boxcity.dto.OrderListVO;
import com.boxcity.dto.OrderPreviewVO;
import com.boxcity.dto.OrderVO;
import com.boxcity.dto.ShipOrderRequest;

public interface OrderService {

    OrderPreviewVO previewOrder(Long userId, CreateOrderRequest request);

    String createOrder(Long userId, CreateOrderRequest request);

    IPage<OrderListVO> getOrderList(Long userId, Integer status, Integer page, Integer size);

    OrderVO getOrderDetail(Long userId, String orderNo);

    void mockPayOrder(Long userId, String orderNo);

    void cancelOrder(Long userId, String orderNo);

    void confirmReceive(Long userId, String orderNo);

    void deleteOrder(Long userId, String orderNo);

    IPage<OrderListVO> getMerchantOrderList(Long merchantId, Integer status, Integer page, Integer size);

    OrderVO getMerchantOrderDetail(Long merchantId, String orderNo);

    void shipOrder(Long merchantId, String orderNo, ShipOrderRequest request);

    void cancelTimeoutOrders();

    void autoConfirmOrders();
}
