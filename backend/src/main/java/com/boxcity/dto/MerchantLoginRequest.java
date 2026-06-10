package com.boxcity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MerchantLoginRequest {

    @NotBlank(message = "手机号不能为空")
    private String contactPhone;

    @NotBlank(message = "密码不能为空")
    private String password;
}
