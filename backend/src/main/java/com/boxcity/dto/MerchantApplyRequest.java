package com.boxcity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class MerchantApplyRequest {

    @NotBlank(message = "店铺名称不能为空")
    private String shopName;

    @NotBlank(message = "联系手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;

    private String contactEmail;

    @NotNull(message = "主体类型不能为空")
    private Integer subjectType;

    private String licenseUrl;

    private String idCardFront;

    private String idCardBack;

    private String legalPersonIdFront;

    private String legalPersonIdBack;

    private String shopIntro;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码至少6位")
    private String password;
}
