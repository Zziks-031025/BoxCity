package com.boxcity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewVO {

    private Long id;

    private Long orderId;

    private Long orderItemId;

    private Long goodsId;

    private Long merchantId;

    private Integer rating;

    private String content;

    private List<String> images;

    private Integer isAppend;

    private Long parentId;

    private String userName;

    private String userAvatar;

    private LocalDateTime createdAt;
}
