package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private Long merchantId;

    private BigDecimal totalAmount;

    private BigDecimal freightAmount;

    private BigDecimal payAmount;

    /** 收货地址快照（JSON 字符串） */
    private String addressSnapshot;

    private String buyerMessage;

    /**
     * 订单状态：
     * 0待付款 1待发货 2待收货 3已完成 4已取消 5售后中
     */
    private Integer status;

    private LocalDateTime payTime;

    private LocalDateTime shipTime;

    private LocalDateTime receiveTime;

    private LocalDateTime cancelTime;

    private LocalDateTime autoCancelTime;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
