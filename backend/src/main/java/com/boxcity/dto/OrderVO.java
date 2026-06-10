package com.boxcity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {

    private Long id;

    private String orderNo;

    private Long merchantId;

    private String shopName;

    private Integer status;

    private BigDecimal totalAmount;

    private BigDecimal freightAmount;

    private BigDecimal payAmount;

    private String addressSnapshot;

    private String buyerMessage;

    private LocalDateTime payTime;

    private LocalDateTime shipTime;

    private LocalDateTime receiveTime;

    private LocalDateTime cancelTime;

    private LocalDateTime createdAt;

    private String logisticsCompany;

    private String logisticsNo;

    private Integer logisticsStatus;

    private String logisticsData;

    private Long refundId;

    private Integer refundStatus;

    private List<OrderItemVO> items;
}
