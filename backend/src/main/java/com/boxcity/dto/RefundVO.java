package com.boxcity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RefundVO {

    private Long id;

    private Long orderId;

    private Long orderItemId;

    /** 1仅退款 2退货退款 */
    private Integer refundType;

    private String refundReason;

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

    private LocalDateTime createdAt;

    private String orderNo;

    private String shopName;

    private String goodsTitle;

    private String goodsImage;

    private String logisticsCompany;

    private String logisticsNo;

    private Integer logisticsStatus;

    private String logisticsData;
}
