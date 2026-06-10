package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("merchant")
public class Merchant {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String shopName;

    private String shopAvatar;

    private String shopIntro;

    private String shopNotice;

    private String contactPhone;

    private String contactEmail;

    /** 1个人 2企业 */
    private Integer subjectType;

    private String licenseUrl;

    private String idCardFront;

    private String idCardBack;

    private String legalPersonIdFront;

    private String legalPersonIdBack;

    /** 0待审核 1通过 2不通过 3冻结 */
    private Integer auditStatus;

    private String auditRemark;

    private Integer creditScore;

    /** BCrypt加密 */
    private String password;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
