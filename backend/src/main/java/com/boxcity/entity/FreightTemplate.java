package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("freight_template")
public class FreightTemplate {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long merchantId;

    private String name;

    private Integer chargeType;  // 0包邮 1按件 2按重量

    private Integer isDefault;   // 0否 1是

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private List<FreightTemplateRule> rules;

    @TableField(exist = false)
    private List<FreightTemplateExclude> excludes;
}
