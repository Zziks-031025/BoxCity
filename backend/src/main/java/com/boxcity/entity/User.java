package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String openid;

    private String unionId;

    private String nickname;

    private String avatar;

    private String phone;

    /** 0未知 1男 2女 */
    private Integer gender;

    private LocalDate birthday;

    private Long cityId;

    /** 1正常 0禁用 */
    private Integer status;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
