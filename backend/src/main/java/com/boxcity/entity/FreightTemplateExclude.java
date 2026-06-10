package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("freight_template_exclude")
public class FreightTemplateExclude {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long templateId;

    private String regionIds;  // 不配送地区ID，逗号分隔
}
