package com.boxcity.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private String nickname;
    private String avatar;
    /** 0未知 1男 2女 */
    private Integer gender;
    /** yyyy-MM-dd format */
    private String birthday;
    private Long cityId;
}
