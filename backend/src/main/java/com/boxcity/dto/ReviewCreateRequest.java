package com.boxcity.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ReviewCreateRequest {

    @NotNull(message = "订单号不能为空")
    private String orderNo;

    @NotNull(message = "订单商品不能为空")
    private Long orderItemId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最少为1星")
    @Max(value = 5, message = "评分最多为5星")
    private Integer rating;

    private String content;

    private List<String> images;

    /**
     * 0 首评 1 追评
     */
    private Integer isAppend = 0;

    /**
     * 追评时可显式传入父评价 ID，不传则自动查找
     */
    private Long parentId;
}
