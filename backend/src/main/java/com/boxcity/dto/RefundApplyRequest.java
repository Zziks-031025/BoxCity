package com.boxcity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class RefundApplyRequest {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    private Long orderItemId;

    /** 1仅退款 2退货退款 */
    @NotNull(message = "退款类型不能为空")
    private Integer refundType;

    @NotBlank(message = "退款原因不能为空")
    private String refundReason;

    /** JSON 数组字符串 */
    private String evidenceImages;

    @NotNull(message = "退款金额不能为空")
    private BigDecimal refundAmount;
}
