package com.boxcity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class MerchantAddressDTO {

    @NotBlank(message = "省份不能为空")
    private String province;

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "区县不能为空")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    private String detail;

    @Pattern(regexp = "(^$|^\\d{6}$)", message = "邮编格式不正确")
    private String zipcode;

    private Boolean isDefault;
}
