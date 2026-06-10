package com.boxcity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderListVO {

    private Long id;

    private String orderNo;

    private Integer status;

    private BigDecimal payAmount;

    private LocalDateTime createdAt;

    private Long merchantId;

    private String shopName;

    /** 订单第一个商品信息 */
    private OrderItemVO firstItem;

    /** 订单商品总数 */
    private Integer itemCount;
}
