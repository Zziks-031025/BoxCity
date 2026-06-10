package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_item")
public class OrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long goodsId;

    /** 商品快照（JSON 字符串） */
    private String goodsSnapshot;

    private Integer quantity;

    private BigDecimal price;

    private LocalDateTime createdAt;
}
