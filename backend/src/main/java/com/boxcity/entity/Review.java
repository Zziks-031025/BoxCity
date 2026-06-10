package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("review")
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long orderItemId;

    private Long userId;

    private Long goodsId;

    private Long merchantId;

    private Integer rating;

    private String content;

    private String images;

    private Integer isAppend;

    private Long parentId;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;
}
