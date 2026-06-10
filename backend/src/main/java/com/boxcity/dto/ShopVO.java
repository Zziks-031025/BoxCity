package com.boxcity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopVO {

    private Long id;

    private String shopName;

    private String shopAvatar;

    private String shopIntro;

    private String shopNotice;

    private String contactPhone;

    private String contactEmail;

    private Integer creditScore;

    private Integer goodsCount;

    private Integer onSaleCount;

    private LocalDateTime createdAt;
}
