package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("goods_image")
public class GoodsImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long goodsId;

    private String imageUrl;

    /** 1主图 2详情图 */
    private Integer type;

    private Integer sort;
}
