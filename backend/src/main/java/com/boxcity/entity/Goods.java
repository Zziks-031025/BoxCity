package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("goods")
public class Goods {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long merchantId;

    private Long categoryId;

    private String title;

    private String description;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private Integer sales;

    private Long cityId;

    /** 0待审核 1通过 2不通过 */
    private Integer auditStatus;

    private String auditRemark;

    /** 0下架 1上架 */
    private Integer status;

    private Integer views;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private List<GoodsImage> images;

    @TableField(exist = false)
    private List<GoodsAttribute> attributes;
}
