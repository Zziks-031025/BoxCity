package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cart")
public class Cart {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long goodsId;

    private Long merchantId;

    private Integer quantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
