package com.boxcity.dto;

import lombok.Data;

@Data
public class UserProfileVO {
    private Long id;
    private String nickname;
    private String avatar;
    private String phone;
    private Integer gender;
    private String birthday;
    private Long cityId;
    /** 城市名称 */
    private String cityName;
    private Integer unpaidCount;
    private Integer unshippedCount;
    private Integer unreceivedCount;
    private Integer completedCount;
}
