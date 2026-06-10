package com.boxcity.service;

import com.boxcity.dto.LogisticsVO;

public interface LogisticsService {

    LogisticsVO getLogistics(Long userId, String orderNo);

    LogisticsVO getMerchantLogistics(Long merchantId, String orderNo);

    void syncOrderLogistics();

    void syncRefundLogistics();
}
