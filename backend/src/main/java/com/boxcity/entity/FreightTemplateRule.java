package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("freight_template_rule")
public class FreightTemplateRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long templateId;

    private String regionIds;       // 逗号分隔的城市ID，空表示全国

    private Integer firstUnit;      // 首件/首重

    private BigDecimal firstFee;    // 首费

    private Integer additionalUnit; // 续件/续重

    private BigDecimal additionalFee; // 续费
}
