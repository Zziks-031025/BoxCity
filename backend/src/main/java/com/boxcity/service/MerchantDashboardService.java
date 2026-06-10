package com.boxcity.service;

import com.boxcity.dto.MerchantDashboardVO;

public interface MerchantDashboardService {

    MerchantDashboardVO getDashboard(Long merchantId);
}
