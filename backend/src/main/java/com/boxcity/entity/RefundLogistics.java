package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("refund_logistics")
public class RefundLogistics {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long refundId;

    private String logisticsCompany;

    private String logisticsNo;

    /** 0运输中 1已签收 */
    private Integer logisticsStatus;

    /** JSON 物流轨迹 */
    private String logisticsData;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
