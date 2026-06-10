package com.boxcity.dto;

import lombok.Data;

@Data
public class MerchantRefundHandleRequest {

    private Boolean agree;

    private String rejectReason;
}
