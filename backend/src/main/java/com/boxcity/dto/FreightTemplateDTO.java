package com.boxcity.dto;

import com.boxcity.entity.FreightTemplateExclude;
import com.boxcity.entity.FreightTemplateRule;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class FreightTemplateDTO {

    @NotBlank(message = "模板名称不能为空")
    private String name;

    @NotNull(message = "计费类型不能为空")
    private Integer chargeType;  // 0包邮 1按件 2按重量

    private Integer isDefault = 0;

    private List<FreightTemplateRule> rules;

    private List<FreightTemplateExclude> excludes;
}
