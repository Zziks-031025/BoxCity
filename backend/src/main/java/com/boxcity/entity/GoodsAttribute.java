package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("goods_attribute")
public class GoodsAttribute {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long goodsId;

    private String attributeName;
}
