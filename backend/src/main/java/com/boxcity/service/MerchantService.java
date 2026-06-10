package com.boxcity.service;

import com.boxcity.dto.MerchantApplyRequest;
import com.boxcity.dto.MerchantInfoVO;
import com.boxcity.dto.MerchantLoginRequest;
import com.boxcity.dto.MerchantLoginResponse;

public interface MerchantService {

    /** 提交入驻申请 */
    void apply(MerchantApplyRequest request);

    /** 商家登录 */
    MerchantLoginResponse login(MerchantLoginRequest request);

    /** 获取商家信息 */
    MerchantInfoVO getInfo(Long merchantId);

    /** 更新店铺信息 */
    void updateInfo(Long merchantId, MerchantInfoVO vo);
}
