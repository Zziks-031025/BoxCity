package com.boxcity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GoodsListVO {

    private Long id;
    private Long merchantId;
    private String shopName;
    private String title;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private Integer auditStatus;
    private Integer status;
    /** 第一张主图URL */
    private String mainImage;
    private LocalDateTime createdAt;
}
