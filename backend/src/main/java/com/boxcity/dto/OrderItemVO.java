package com.boxcity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {

    private Long id;
    private Long goodsId;
    private String goodsTitle;
    private String mainImage;
    private BigDecimal price;
    private Integer quantity;
    private Boolean reviewed;
}
