package com.boxcity.dto;

import com.boxcity.entity.GoodsAttribute;
import com.boxcity.entity.GoodsImage;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GoodsVO {

    private Long id;
    private Long merchantId;
    private String shopName;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private Long cityId;
    private String cityName;
    private Integer auditStatus;
    private String auditRemark;
    private Integer status;
    private Integer views;
    private LocalDateTime createdAt;
    private List<GoodsImage> images;
    private List<GoodsAttribute> attributes;
    private Integer reviewCount;
    private Double avgRating;
    private List<ReviewVO> reviews;
}
