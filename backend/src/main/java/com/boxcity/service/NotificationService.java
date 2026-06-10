package com.boxcity.service;

import com.boxcity.entity.Goods;
import com.boxcity.entity.Merchant;
import com.boxcity.entity.Order;
import com.boxcity.entity.Refund;

public interface NotificationService {

    void sendOrderPaidNotice(Order order);

    void sendOrderShippedNotice(Order order, String logisticsCompany, String logisticsNo);

    void sendOrderCompletedNotice(Order order);

    void sendOrderCancelledNotice(Order order, String reason);

    void sendRefundAppliedNotice(Refund refund, Order order, Merchant merchant);

    void sendRefundStatusNotice(Refund refund, Order order, String title, String content);

    void sendMerchantAuditNotice(Merchant merchant, boolean approved, String remark);

    void sendGoodsAuditNotice(Goods goods, Merchant merchant, boolean approved, String remark);
}

