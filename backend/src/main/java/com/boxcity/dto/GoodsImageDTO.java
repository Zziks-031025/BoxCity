package com.boxcity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GoodsImageDTO {

    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;

    /** 1主图 2详情图，默认1 */
    private Integer type = 1;

    /** 排序，默认0 */
    private Integer sort = 0;
}
