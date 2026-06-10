package com.boxcity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartVO {

    private Long id;

    private Long goodsId;

    private Long merchantId;

    private String shopName;

    private String goodsTitle;

    private String mainImage;

    private BigDecimal price;

    private Integer quantity;

    /** 当前库存 */
    private Integer stock;

    /** 商品状态（0下架 1上架） */
    private Integer goodsStatus;
}
