package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_logistics")
public class OrderLogistics {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String logisticsCompany;

    private String logisticsNo;

    /** 0运输中 1已签收 */
    private Integer logisticsStatus;

    /** JSON 物流轨迹 */
    private String logisticsData;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
