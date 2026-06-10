package com.boxcity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MerchantInfoVO {

    private Long id;

    private String shopName;

    private String shopAvatar;

    private String shopIntro;

    private String shopNotice;

    private String contactPhone;

    private String contactEmail;

    private Integer subjectType;

    private Integer auditStatus;

    private String auditRemark;

    private Integer creditScore;

    private LocalDateTime createdAt;
}
