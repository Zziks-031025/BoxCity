package com.boxcity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminDashboardVO {

    private Integer userCount;

    private Integer merchantCount;

    private Integer goodsCount;

    private Integer orderCount;

    private Integer pendingMerchantCount;

    private Integer pendingGoodsCount;

    private Integer disputeCount;

    private BigDecimal todaySales;
}
