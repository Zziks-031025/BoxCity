package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("city")
public class City {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private String name;

    /** 1省 2市 3区 */
    private Integer level;

    private Integer sort;

    private LocalDateTime createdAt;

    @TableField(exist = false)
    private List<City> children;
}
