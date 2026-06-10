package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("payment_config")
public class PaymentConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String mchId;

    private String apiKey;

    private String certPath;

    private String notifyUrl;

    private LocalDateTime updatedAt;
}
