package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxcity.common.BusinessException;
import com.boxcity.dto.RefundApplyRequest;
import com.boxcity.dto.RefundVO;
import com.boxcity.entity.Goods;
import com.boxcity.entity.GoodsImage;
import com.boxcity.entity.Merchant;
import com.boxcity.entity.Order;
import com.boxcity.entity.OrderItem;
import com.boxcity.entity.Refund;
import com.boxcity.entity.RefundLogistics;
import com.boxcity.mapper.GoodsImageMapper;
import com.boxcity.mapper.GoodsMapper;
import com.boxcity.mapper.MerchantMapper;
import com.boxcity.mapper.OrderItemMapper;
import com.boxcity.mapper.OrderMapper;
import com.boxcity.mapper.RefundLogisticsMapper;
import com.boxcity.mapper.RefundMapper;
import com.boxcity.service.NotificationService;
import com.boxcity.service.RefundService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

    private final RefundMapper refundMapper;
    private final RefundLogisticsMapper refundLogisticsMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final MerchantMapper merchantMapper;
    private final GoodsMapper goodsMapper;
    private final GoodsImageMapper goodsImageMapper;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long applyRefund(Long userId, RefundApplyRequest request) {
        Order order = getOrder(request.getOrderId());
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(403, "无权申请该订单售后");
        }
        if (order.getStatus() == null || (order.getStatus() != 1 && order.getStatus() != 2 && order.getStatus() != 3)) {
            throw new BusinessException("当前订单状态不可申请售后");
        }

        validateRefundType(order, request.getRefundType());
        ensureNoActiveRefund(order.getId());

        Long orderItemId = request.getOrderItemId();
        if (orderItemId == null) {
            OrderItem firstItem = orderItemMapper.selectOne(
                    new LambdaQueryWrapper<OrderItem>()
                            .eq(OrderItem::getOrderId, order.getId())
                            .orderByAsc(OrderItem::getId)
                            .last("LIMIT 1"));
            orderItemId = firstItem == null ? null : firstItem.getId();
        }

        Refund refund = new Refund();
        refund.setOrderId(order.getId());
        refund.setOrderItemId(orderItemId);
        refund.setUserId(userId);
        refund.setMerchantId(order.getMerchantId());
        refund.setRefundType(request.getRefundType());
        refund.setRefundReason(request.getRefundReason());
        refund.setEvidenceImages(request.getEvidenceImages());
        refund.setRefundAmount(request.getRefundAmount());
        refund.setStatus(0);
        refund.setPlatformIntervene(0);
        refund.setMerchantDeadline(LocalDateTime.now().plusHours(48));
        refundMapper.insert(refund);

        order.setStatus(5);
        orderMapper.updateById(order);
        notificationService.sendRefundAppliedNotice(refund, order, merchantMapper.selectById(order.getMerchantId()));
        return refund.getId();
    }

    @Override
    public RefundVO getRefundDetail(Long userId, Long refundId) {
        Refund refund = getRefund(refundId);
        if (!userId.equals(refund.getUserId())) {
            throw new BusinessException(403, "无权查看该售后记录");
        }
        return toRefundVO(refund);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelRefund(Long userId, Long refundId) {
        Refund refund = getRefund(refundId);
        if (!userId.equals(refund.getUserId())) {
            throw new BusinessException(403, "无权操作该售后记录");
        }
        if (refund.getStatus() == null || refund.getStatus() != 0) {
            throw new BusinessException("当前售后状态不可撤销");
        }

        refund.setStatus(6);
        refundMapper.updateById(refund);
        restoreOrderStatus(refund.getOrderId());
        notificationService.sendRefundStatusNotice(refund, getOrder(refund.getOrderId()), "售后申请已撤销", "您的售后申请已撤销，订单状态已恢复。");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void fillReturnLogistics(Long userId, Long refundId, String logisticsCompany, String logisticsNo) {
        Refund refund = getRefund(refundId);
        if (!userId.equals(refund.getUserId())) {
            throw new BusinessException(403, "无权操作该售后记录");
        }
        if (refund.getStatus() == null || refund.getStatus() != 1) {
            throw new BusinessException("当前售后状态不可填写退货物流");
        }

        RefundLogistics logistics = refundLogisticsMapper.selectOne(
                new LambdaQueryWrapper<RefundLogistics>()
                        .eq(RefundLogistics::getRefundId, refundId)
                        .last("LIMIT 1"));

        List<Map<String, Object>> traces = new ArrayList<>();
        Map<String, Object> trace = new HashMap<>();
        trace.put("time", LocalDateTime.now());
        trace.put("content", "用户已提交退货物流，等待商家签收");
        traces.add(trace);

        if (logistics == null) {
            logistics = new RefundLogistics();
            logistics.setRefundId(refundId);
            logistics.setLogisticsCompany(logisticsCompany);
            logistics.setLogisticsNo(logisticsNo);
            logistics.setLogisticsStatus(0);
            logistics.setLogisticsData(toJson(traces));
            refundLogisticsMapper.insert(logistics);
        } else {
            logistics.setLogisticsCompany(logisticsCompany);
            logistics.setLogisticsNo(logisticsNo);
            logistics.setLogisticsStatus(0);
            logistics.setLogisticsData(toJson(traces));
            refundLogisticsMapper.updateById(logistics);
        }

        refund.setStatus(2);
        refundMapper.updateById(refund);
        notificationService.sendRefundStatusNotice(refund, getOrder(refund.getOrderId()), "退货物流已提交", "退货物流信息已提交，等待商家确认收货。");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void requestPlatformIntervene(Long userId, Long refundId, String reason) {
        Refund refund = getRefund(refundId);
        if (!userId.equals(refund.getUserId())) {
            throw new BusinessException(403, "无权操作该售后记录");
        }
        if (refund.getStatus() == null || refund.getStatus() != 4) {
            throw new BusinessException("当前售后状态不可申请平台介入");
        }

        refund.setStatus(5);
        refund.setPlatformIntervene(1);
        if (reason != null && !reason.isBlank()) {
            refund.setRejectReason((refund.getRejectReason() == null ? "" : refund.getRejectReason() + "；")
                    + "用户申请平台介入：" + reason);
        }
        refundMapper.updateById(refund);
        notificationService.sendRefundStatusNotice(refund, getOrder(refund.getOrderId()), "平台介入申请已提交", "平台已收到您的介入申请，将尽快处理该售后纠纷。");
    }

    @Override
    public IPage<RefundVO> getMerchantRefundList(Long merchantId, Integer status, Integer page, Integer size) {
        IPage<Refund> refundPage = refundMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Refund>()
                        .eq(Refund::getMerchantId, merchantId)
                        .eq(status != null, Refund::getStatus, status)
                        .orderByDesc(Refund::getCreatedAt));
        return refundPage.convert(this::toRefundVO);
    }

    @Override
    public RefundVO getMerchantRefundDetail(Long merchantId, Long refundId) {
        Refund refund = getRefund(refundId);
        if (!merchantId.equals(refund.getMerchantId())) {
            throw new BusinessException(403, "无权查看该售后记录");
        }
        return toRefundVO(refund);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRefund(Long merchantId, Long refundId, boolean agree, String rejectReason) {
        Refund refund = getRefund(refundId);
        if (!merchantId.equals(refund.getMerchantId())) {
            throw new BusinessException(403, "无权处理该售后记录");
        }
        if (refund.getStatus() == null || refund.getStatus() != 0) {
            throw new BusinessException("当前售后状态不可处理");
        }

        Order order = getOrder(refund.getOrderId());
        if (agree) {
            if (refund.getRefundType() != null && refund.getRefundType() == 1) {
                completeRefund(refund, order, order.getShipTime() == null);
            } else {
                refund.setStatus(1);
                refund.setRejectReason(null);
                refund.setReturnDeadline(LocalDateTime.now().plusDays(7));
                refundMapper.updateById(refund);
                notificationService.sendRefundStatusNotice(refund, order, "商家已同意退货退款", "商家已同意您的退货退款申请，请在时限内寄回商品。");
            }
            return;
        }

        refund.setStatus(4);
        refund.setRejectReason((rejectReason == null || rejectReason.isBlank()) ? "商家拒绝退款" : rejectReason);
        refundMapper.updateById(refund);
        notificationService.sendRefundStatusNotice(refund, order, "商家已拒绝售后申请", "商家已拒绝您的售后申请，您可以申请平台介入。");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReturnReceived(Long merchantId, Long refundId) {
        Refund refund = getRefund(refundId);
        if (!merchantId.equals(refund.getMerchantId())) {
            throw new BusinessException(403, "无权处理该售后记录");
        }
        if (refund.getStatus() == null || refund.getStatus() != 2) {
            throw new BusinessException("当前售后状态不可确认收货退款");
        }
        completeRefund(refund, getOrder(refund.getOrderId()), true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleTimeoutRefunds() {
        LocalDateTime now = LocalDateTime.now();

        List<Refund> waitMerchant = refundMapper.selectList(
                new LambdaQueryWrapper<Refund>()
                        .eq(Refund::getStatus, 0)
                        .lt(Refund::getMerchantDeadline, now));
        for (Refund refund : waitMerchant) {
            Order order = getOrder(refund.getOrderId());
            if (refund.getRefundType() != null && refund.getRefundType() == 1) {
                completeRefund(refund, order, order.getShipTime() == null);
            } else {
                refund.setStatus(1);
                refund.setReturnDeadline(now.plusDays(7));
                refundMapper.updateById(refund);
                notificationService.sendRefundStatusNotice(refund, order, "售后已自动同意", "商家超时未处理，系统已自动同意您的售后申请。");
            }
        }

        List<Refund> waitReturn = refundMapper.selectList(
                new LambdaQueryWrapper<Refund>()
                        .eq(Refund::getStatus, 1)
                        .lt(Refund::getReturnDeadline, now));
        for (Refund refund : waitReturn) {
            refund.setStatus(6);
            refundMapper.updateById(refund);
            restoreOrderStatus(refund.getOrderId());
            notificationService.sendRefundStatusNotice(refund, getOrder(refund.getOrderId()), "售后申请已关闭", "您未在规定时间内寄回商品，售后申请已关闭。");
        }

        List<Refund> rejectedRefunds = refundMapper.selectList(
                new LambdaQueryWrapper<Refund>()
                        .eq(Refund::getStatus, 4)
                        .lt(Refund::getUpdatedAt, now.minusDays(7)));
        for (Refund refund : rejectedRefunds) {
            refund.setStatus(6);
            refundMapper.updateById(refund);
            restoreOrderStatus(refund.getOrderId());
            notificationService.sendRefundStatusNotice(refund, getOrder(refund.getOrderId()), "售后申请已关闭", "售后申请已超过处理时限，系统已自动关闭。");
        }

        List<Refund> returningRefunds = refundMapper.selectList(
                new LambdaQueryWrapper<Refund>().eq(Refund::getStatus, 2));
        for (Refund refund : returningRefunds) {
            RefundLogistics logistics = refundLogisticsMapper.selectOne(
                    new LambdaQueryWrapper<RefundLogistics>()
                            .eq(RefundLogistics::getRefundId, refund.getId())
                            .last("LIMIT 1"));
            if (logistics != null && logistics.getUpdatedAt() != null && logistics.getUpdatedAt().isBefore(now.minusDays(3))) {
                completeRefund(refund, getOrder(refund.getOrderId()), true);
            }
        }
    }

    private void validateRefundType(Order order, Integer refundType) {
        if (refundType == null) {
            throw new BusinessException("退款类型不能为空");
        }
        if (order.getStatus() == 1 && refundType != 1) {
            throw new BusinessException("待发货订单仅支持仅退款");
        }
        if (order.getStatus() == 2 && refundType != 1) {
            throw new BusinessException("待收货订单当前仅支持仅退款");
        }
        if (order.getStatus() == 3 && refundType != 2) {
            throw new BusinessException("已完成订单请申请退货退款");
        }
    }

    private void ensureNoActiveRefund(Long orderId) {
        Refund activeRefund = refundMapper.selectOne(
                new LambdaQueryWrapper<Refund>()
                        .eq(Refund::getOrderId, orderId)
                        .in(Refund::getStatus, 0, 1, 2, 4, 5)
                        .last("LIMIT 1"));
        if (activeRefund != null) {
            throw new BusinessException("该订单已有进行中的售后申请");
        }
    }

    private Refund getRefund(Long refundId) {
        Refund refund = refundMapper.selectById(refundId);
        if (refund == null) {
            throw new BusinessException(404, "售后记录不存在");
        }
        return refund;
    }

    private Order getOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private RefundVO toRefundVO(Refund refund) {
        RefundVO vo = new RefundVO();
        vo.setId(refund.getId());
        vo.setOrderId(refund.getOrderId());
        vo.setOrderItemId(refund.getOrderItemId());
        vo.setRefundType(refund.getRefundType());
        vo.setRefundReason(refund.getRefundReason());
        vo.setEvidenceImages(refund.getEvidenceImages());
        vo.setRefundAmount(refund.getRefundAmount());
        vo.setStatus(refund.getStatus());
        vo.setRejectReason(refund.getRejectReason());
        vo.setPlatformIntervene(refund.getPlatformIntervene());
        vo.setMerchantDeadline(refund.getMerchantDeadline());
        vo.setReturnDeadline(refund.getReturnDeadline());
        vo.setCreatedAt(refund.getCreatedAt());

        Order order = orderMapper.selectById(refund.getOrderId());
        if (order != null) {
            vo.setOrderNo(order.getOrderNo());
            Merchant merchant = merchantMapper.selectById(order.getMerchantId());
            if (merchant != null) {
                vo.setShopName(merchant.getShopName());
            }
        }

        if (refund.getOrderItemId() != null) {
            OrderItem item = orderItemMapper.selectById(refund.getOrderItemId());
            if (item != null) {
                Goods goods = goodsMapper.selectById(item.getGoodsId());
                if (goods != null) {
                    vo.setGoodsTitle(goods.getTitle());
                }
                GoodsImage mainImage = goodsImageMapper.selectOne(
                        new LambdaQueryWrapper<GoodsImage>()
                                .eq(GoodsImage::getGoodsId, item.getGoodsId())
                                .eq(GoodsImage::getType, 1)
                                .orderByAsc(GoodsImage::getSort)
                                .last("LIMIT 1"));
                if (mainImage != null) {
                    vo.setGoodsImage(mainImage.getImageUrl());
                }
            }
        }

        RefundLogistics logistics = refundLogisticsMapper.selectOne(
                new LambdaQueryWrapper<RefundLogistics>()
                        .eq(RefundLogistics::getRefundId, refund.getId())
                        .last("LIMIT 1"));
        if (logistics != null) {
            vo.setLogisticsCompany(logistics.getLogisticsCompany());
            vo.setLogisticsNo(logistics.getLogisticsNo());
            vo.setLogisticsStatus(logistics.getLogisticsStatus());
            vo.setLogisticsData(logistics.getLogisticsData());
        }
        return vo;
    }

    private void completeRefund(Refund refund, Order order, boolean restoreStock) {
        refund.setStatus(3);
        refundMapper.updateById(refund);

        order.setStatus(4);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);
        notificationService.sendRefundStatusNotice(refund, order, "退款成功", "您的售后申请已处理完成，退款将按原路返回。");

        if (restoreStock) {
            restoreStock(order.getId());
        }
    }

    private void restoreOrderStatus(Long orderId) {
        Order order = getOrder(orderId);
        order.setStatus(resolveOrderStatus(order));
        orderMapper.updateById(order);
    }

    private Integer resolveOrderStatus(Order order) {
        if (order.getReceiveTime() != null) {
            return 3;
        }
        if (order.getShipTime() != null) {
            return 2;
        }
        if (order.getPayTime() != null) {
            return 1;
        }
        if (order.getCancelTime() != null) {
            return 4;
        }
        return 0;
    }

    private void restoreStock(Long orderId) {
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            goodsMapper.update(
                    null,
                    new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Goods>()
                            .eq(Goods::getId, item.getGoodsId())
                            .setSql("stock = stock + " + item.getQuantity()));
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON 序列化失败", e);
            return "[]";
        }
    }
}
