package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("refund")
public class Refund {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long orderItemId;

    private Long userId;

    private Long merchantId;

    /** 1仅退款 2退货退款 */
    private Integer refundType;

    private String refundReason;

    /** JSON 数组 */
    private String evidenceImages;

    private BigDecimal refundAmount;

    /**
     * 0待商家处理 1商家同意待退货 2退货中 3退款成功
     * 4商家拒绝 5平台介入中 6售后关闭
     */
    private Integer status;

    private String rejectReason;

    private Integer platformIntervene;

    private LocalDateTime merchantDeadline;

    private LocalDateTime returnDeadline;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
