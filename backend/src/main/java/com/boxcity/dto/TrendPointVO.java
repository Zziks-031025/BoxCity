package com.boxcity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TrendPointVO {

    private String date;

    private Integer orderCount;

    private BigDecimal salesAmount;
}
