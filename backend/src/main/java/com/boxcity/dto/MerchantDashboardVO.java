package com.boxcity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class MerchantDashboardVO {

    private Integer todayOrderCount;

    private BigDecimal todaySalesAmount;

    private BigDecimal totalSalesAmount;

    private Integer goodsViewCount;

    private List<TrendPointVO> trends = new ArrayList<>();
}
