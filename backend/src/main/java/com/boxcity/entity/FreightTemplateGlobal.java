package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("freight_template_global")
public class FreightTemplateGlobal {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer chargeType;

    private String rules;

    private LocalDateTime updatedAt;
}
