package com.boxcity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ReturnLogisticsRequest {

    @NotBlank(message = "物流公司不能为空")
    private String logisticsCompany;

    @NotBlank(message = "物流单号不能为空")
    private String logisticsNo;
}
