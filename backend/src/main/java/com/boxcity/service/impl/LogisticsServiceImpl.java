package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.boxcity.common.BusinessException;
import com.boxcity.dto.LogisticsVO;
import com.boxcity.entity.Order;
import com.boxcity.entity.OrderLogistics;
import com.boxcity.entity.Refund;
import com.boxcity.entity.RefundLogistics;
import com.boxcity.mapper.OrderLogisticsMapper;
import com.boxcity.mapper.OrderMapper;
import com.boxcity.mapper.RefundLogisticsMapper;
import com.boxcity.mapper.RefundMapper;
import com.boxcity.service.LogisticsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LogisticsServiceImpl implements LogisticsService {

    private final OrderMapper orderMapper;
    private final OrderLogisticsMapper orderLogisticsMapper;
    private final RefundMapper refundMapper;
    private final RefundLogisticsMapper refundLogisticsMapper;
    private final ObjectMapper objectMapper;

    @Override
    public LogisticsVO getLogistics(Long userId, String orderNo) {
        Order order = getOrder(orderNo);
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(403, "无权查看该订单物流");
        }
        return toLogisticsVO(order.getId());
    }

    @Override
    public LogisticsVO getMerchantLogistics(Long merchantId, String orderNo) {
        Order order = getOrder(orderNo);
        if (!merchantId.equals(order.getMerchantId())) {
            throw new BusinessException(403, "无权查看该订单物流");
        }
        return toLogisticsVO(order.getId());
    }

    @Override
    public void syncOrderLogistics() {
        List<Order> orders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, 2)
                        .isNotNull(Order::getShipTime));
        for (Order order : orders) {
            OrderLogistics logistics = orderLogisticsMapper.selectOne(
                    new LambdaQueryWrapper<OrderLogistics>()
                            .eq(OrderLogistics::getOrderId, order.getId())
                            .last("LIMIT 1"));
            if (logistics != null) {
                updateOrderLogistics(order, logistics);
            }
        }
    }

    @Override
    public void syncRefundLogistics() {
        List<Refund> refunds = refundMapper.selectList(
                new LambdaQueryWrapper<Refund>().eq(Refund::getStatus, 2));
        for (Refund refund : refunds) {
            RefundLogistics logistics = refundLogisticsMapper.selectOne(
                    new LambdaQueryWrapper<RefundLogistics>()
                            .eq(RefundLogistics::getRefundId, refund.getId())
                            .last("LIMIT 1"));
            if (logistics != null) {
                updateRefundLogistics(logistics);
            }
        }
    }

    private Order getOrder(String orderNo) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
                        .last("LIMIT 1"));
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private LogisticsVO toLogisticsVO(Long orderId) {
        OrderLogistics logistics = orderLogisticsMapper.selectOne(
                new LambdaQueryWrapper<OrderLogistics>()
                        .eq(OrderLogistics::getOrderId, orderId)
                        .last("LIMIT 1"));
        if (logistics == null) {
            throw new BusinessException(404, "暂无物流信息");
        }

        LogisticsVO vo = new LogisticsVO();
        vo.setLogisticsCompany(logistics.getLogisticsCompany());
        vo.setLogisticsNo(logistics.getLogisticsNo());
        vo.setLogisticsStatus(logistics.getLogisticsStatus());
        vo.setTracks(parseTracks(logistics.getLogisticsData()));
        return vo;
    }

    private void updateOrderLogistics(Order order, OrderLogistics logistics) {
        long days = Duration.between(order.getShipTime(), LocalDateTime.now()).toDays();
        int targetStatus;
        String content;
        if (days >= 4) {
            targetStatus = 4;
            content = "快件已由收件人签收";
        } else if (days >= 3) {
            targetStatus = 3;
            content = "快件正在派送，请保持电话畅通";
        } else if (days >= 2) {
            targetStatus = 2;
            content = "快件正在运输途中";
        } else {
            targetStatus = 1;
            content = "快件已从发货地发出";
        }

        if (logistics.getLogisticsStatus() != null && logistics.getLogisticsStatus() >= targetStatus) {
            return;
        }

        List<Map<String, Object>> tracks = parseRawTracks(logistics.getLogisticsData());
        tracks.add(buildTrack(LocalDateTime.now(), content));
        logistics.setLogisticsStatus(targetStatus);
        logistics.setLogisticsData(toJson(tracks));
        orderLogisticsMapper.updateById(logistics);
    }

    private void updateRefundLogistics(RefundLogistics logistics) {
        long days = Duration.between(logistics.getCreatedAt(), LocalDateTime.now()).toDays();
        int targetStatus;
        String content;
        if (days >= 3) {
            targetStatus = 4;
            content = "商家已签收退货包裹";
        } else if (days >= 2) {
            targetStatus = 2;
            content = "退货包裹运输中";
        } else {
            targetStatus = 1;
            content = "退货包裹已寄出";
        }

        if (logistics.getLogisticsStatus() != null && logistics.getLogisticsStatus() >= targetStatus) {
            return;
        }

        List<Map<String, Object>> tracks = parseRawTracks(logistics.getLogisticsData());
        tracks.add(buildTrack(LocalDateTime.now(), content));
        logistics.setLogisticsStatus(targetStatus);
        logistics.setLogisticsData(toJson(tracks));
        refundLogisticsMapper.updateById(logistics);
    }

    private List<LogisticsVO.LogisticsTrackVO> parseTracks(String logisticsData) {
        List<LogisticsVO.LogisticsTrackVO> tracks = new ArrayList<>();
        for (Map<String, Object> rawTrack : parseRawTracks(logisticsData)) {
            LogisticsVO.LogisticsTrackVO track = new LogisticsVO.LogisticsTrackVO();
            track.setTime(String.valueOf(rawTrack.getOrDefault("time", "")));
            track.setContext(String.valueOf(rawTrack.getOrDefault("content", "")));
            track.setLocation(String.valueOf(rawTrack.getOrDefault("location", "")));
            tracks.add(track);
        }
        return tracks;
    }

    private List<Map<String, Object>> parseRawTracks(String logisticsData) {
        if (logisticsData == null || logisticsData.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(logisticsData, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private Map<String, Object> buildTrack(LocalDateTime time, String content) {
        Map<String, Object> track = new HashMap<>();
        track.put("time", time);
        track.put("content", content);
        track.put("location", "");
        return track;
    }

    private String toJson(List<Map<String, Object>> tracks) {
        try {
            return objectMapper.writeValueAsString(tracks);
        } catch (Exception e) {
            return "[]";
        }
    }
}
