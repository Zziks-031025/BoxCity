package com.boxcity.service.impl;

import com.boxcity.entity.Goods;
import com.boxcity.entity.Merchant;
import com.boxcity.entity.Message;
import com.boxcity.entity.Order;
import com.boxcity.entity.Refund;
import com.boxcity.entity.User;
import com.boxcity.mapper.MessageMapper;
import com.boxcity.mapper.UserMapper;
import com.boxcity.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final int TYPE_ORDER = 1;
    private static final int TYPE_REFUND = 2;

    private final UserMapper userMapper;
    private final MessageMapper messageMapper;

    @Override
    public void sendOrderPaidNotice(Order order) {
        String title = "订单支付成功";
        String content = String.format("订单 %s 已支付成功，商家将尽快为您发货。", order.getOrderNo());
        createUserMessage(order.getUserId(), TYPE_ORDER, title, content);
        notifyUser(order.getUserId(), "ORDER_PAID", title, content);
    }

    @Override
    public void sendOrderShippedNotice(Order order, String logisticsCompany, String logisticsNo) {
        String title = "订单已发货";
        String content = String.format("订单 %s 已由 %s 发出，运单号 %s。", order.getOrderNo(), logisticsCompany, logisticsNo);
        createUserMessage(order.getUserId(), TYPE_ORDER, title, content);
        notifyUser(order.getUserId(), "ORDER_SHIPPED", title, content);
    }

    @Override
    public void sendOrderCompletedNotice(Order order) {
        String title = "订单已完成";
        String content = String.format("订单 %s 已完成，感谢您的购买，欢迎发表评价。", order.getOrderNo());
        createUserMessage(order.getUserId(), TYPE_ORDER, title, content);
        notifyUser(order.getUserId(), "ORDER_COMPLETED", title, content);
    }

    @Override
    public void sendOrderCancelledNotice(Order order, String reason) {
        String title = "订单已取消";
        String suffix = StringUtils.hasText(reason) ? "，原因：" + reason : "";
        String content = String.format("订单 %s 已取消%s。", order.getOrderNo(), suffix);
        createUserMessage(order.getUserId(), TYPE_ORDER, title, content);
        notifyUser(order.getUserId(), "ORDER_CANCELLED", title, content);
    }

    @Override
    public void sendRefundAppliedNotice(Refund refund, Order order, Merchant merchant) {
        String title = "售后申请已提交";
        String content = String.format("订单 %s 的售后申请已提交，平台会同步商家处理进度。", order.getOrderNo());
        createUserMessage(order.getUserId(), TYPE_REFUND, title, content);
        notifyUser(order.getUserId(), "REFUND_APPLIED", title, content);

        if (merchant != null) {
            String merchantContent = String.format("订单 %s 收到新的售后申请，请尽快处理。", order.getOrderNo());
            sendSms(merchant.getContactPhone(), "MERCHANT_REFUND_APPLIED", merchantContent);
        }
    }

    @Override
    public void sendRefundStatusNotice(Refund refund, Order order, String title, String content) {
        createUserMessage(order.getUserId(), TYPE_REFUND, title, content);
        notifyUser(order.getUserId(), "REFUND_STATUS", title, content);
    }

    @Override
    public void sendMerchantAuditNotice(Merchant merchant, boolean approved, String remark) {
        if (merchant == null) {
            return;
        }
        String title = approved ? "入驻审核通过" : "入驻审核未通过";
        String content = approved
                ? String.format("店铺 %s 的入驻审核已通过，请登录商家后台完善资料。", merchant.getShopName())
                : String.format("店铺 %s 的入驻审核未通过，原因：%s。", merchant.getShopName(), defaultRemark(remark));
        sendSms(merchant.getContactPhone(), "MERCHANT_AUDIT", content);
        sendWechat(null, "MERCHANT_AUDIT", title, content);
    }

    @Override
    public void sendGoodsAuditNotice(Goods goods, Merchant merchant, boolean approved, String remark) {
        if (goods == null || merchant == null) {
            return;
        }
        String title = approved ? "商品审核通过" : "商品审核未通过";
        String content = approved
                ? String.format("商品《%s》已通过平台审核并可正常展示。", goods.getTitle())
                : String.format("商品《%s》审核未通过，原因：%s。", goods.getTitle(), defaultRemark(remark));
        sendSms(merchant.getContactPhone(), "GOODS_AUDIT", content);
        sendWechat(null, "GOODS_AUDIT", title, content);
    }

    private void createUserMessage(Long userId, Integer type, String title, String content) {
        if (userId == null) {
            return;
        }
        Message message = new Message();
        message.setUserId(userId);
        message.setType(type);
        message.setTitle(title);
        message.setContent(content);
        message.setIsRead(0);
        message.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);
    }

    private void notifyUser(Long userId, String scene, String title, String content) {
        User user = userId == null ? null : userMapper.selectById(userId);
        if (user == null) {
            return;
        }
        sendSms(user.getPhone(), scene, content);
        sendWechat(user.getOpenid(), scene, title, content);
    }

    private void sendSms(String phone, String scene, String content) {
        if (!StringUtils.hasText(phone)) {
            return;
        }
        // TODO: 接入真实短信服务商
        log.info("【模拟短信通知】scene={}, phone={}, content={}", scene, phone, content);
    }

    private void sendWechat(String openid, String scene, String title, String content) {
        // TODO: 接入真实微信模板消息/订阅消息
        log.info("【模拟微信通知】scene={}, openid={}, title={}, content={}", scene, openid, title, content);
    }

    private String defaultRemark(String remark) {
        return StringUtils.hasText(remark) ? remark : "请联系平台客服";
    }
}
