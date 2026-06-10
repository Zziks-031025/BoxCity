package com.boxcity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.dto.RefundApplyRequest;
import com.boxcity.dto.RefundVO;

public interface RefundService {

    Long applyRefund(Long userId, RefundApplyRequest request);

    RefundVO getRefundDetail(Long userId, Long refundId);

    void cancelRefund(Long userId, Long refundId);

    void fillReturnLogistics(Long userId, Long refundId, String logisticsCompany, String logisticsNo);

    void requestPlatformIntervene(Long userId, Long refundId, String reason);

    IPage<RefundVO> getMerchantRefundList(Long merchantId, Integer status, Integer page, Integer size);

    RefundVO getMerchantRefundDetail(Long merchantId, Long refundId);

    void handleRefund(Long merchantId, Long refundId, boolean agree, String rejectReason);

    void confirmReturnReceived(Long merchantId, Long refundId);

    void handleTimeoutRefunds();
}
