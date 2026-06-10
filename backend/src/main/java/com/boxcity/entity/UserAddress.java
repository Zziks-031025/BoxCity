package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_address")
public class UserAddress {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private String phone;

    private String province;

    private String city;

    private String district;

    private String detail;

    /** 是否默认地址：0否 1是 */
    private Integer isDefault;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
