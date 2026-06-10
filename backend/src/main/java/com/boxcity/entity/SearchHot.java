package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("search_hot")
public class SearchHot {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String keyword;

    private Integer sort;

    /** 1启用 0禁用 */
    private Integer status;
}
