package com.boxcity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryDTO {

    private Long parentId = 0L;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Integer sort = 0;

    private Integer status = 1;
}
