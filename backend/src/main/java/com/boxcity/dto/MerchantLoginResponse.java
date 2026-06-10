package com.boxcity.dto;

import lombok.Data;

@Data
public class MerchantLoginResponse {

    private String token;

    private Long merchantId;

    private String shopName;

    private Integer auditStatus;
}
