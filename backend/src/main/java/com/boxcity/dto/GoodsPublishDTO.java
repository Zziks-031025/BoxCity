package com.boxcity.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class GoodsPublishDTO {

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotBlank(message = "商品标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    private BigDecimal originalPrice;

    @NotNull(message = "库存不能为空")
    private Integer stock;

    private Long cityId;

    @Valid
    private List<GoodsImageDTO> images;

    private List<String> attributes;
}
