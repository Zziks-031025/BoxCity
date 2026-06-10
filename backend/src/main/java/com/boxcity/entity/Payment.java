package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment")
public class Payment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String transactionId;

    private BigDecimal payAmount;

    /** 0未支付 1已支付 2已退款 */
    private Integer payStatus;

    private LocalDateTime payTime;

    private LocalDateTime createdAt;
}
