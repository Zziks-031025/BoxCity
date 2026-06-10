package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.boxcity.dto.MerchantDashboardVO;
import com.boxcity.dto.TrendPointVO;
import com.boxcity.entity.Goods;
import com.boxcity.entity.Order;
import com.boxcity.mapper.GoodsMapper;
import com.boxcity.mapper.OrderMapper;
import com.boxcity.service.MerchantDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantDashboardServiceImpl implements MerchantDashboardService {

    private final OrderMapper orderMapper;
    private final GoodsMapper goodsMapper;

    @Override
    public MerchantDashboardVO getDashboard(Long merchantId) {
        MerchantDashboardVO vo = new MerchantDashboardVO();

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrowStart = LocalDate.now().plusDays(1).atStartOfDay();

        List<Order> todayOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getMerchantId, merchantId)
                        .ge(Order::getCreatedAt, todayStart)
                        .lt(Order::getCreatedAt, tomorrowStart));
        vo.setTodayOrderCount(todayOrders.size());

        List<Order> paidOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getMerchantId, merchantId)
                        .isNotNull(Order::getPayTime)
                        .in(Order::getStatus, 1, 2, 3, 5));
        vo.setTotalSalesAmount(sumAmount(paidOrders));

        List<Order> todayPaidOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getMerchantId, merchantId)
                        .ge(Order::getPayTime, todayStart)
                        .lt(Order::getPayTime, tomorrowStart)
                        .in(Order::getStatus, 1, 2, 3, 5));
        vo.setTodaySalesAmount(sumAmount(todayPaidOrders));

        List<Goods> goodsList = goodsMapper.selectList(
                new LambdaQueryWrapper<Goods>().eq(Goods::getMerchantId, merchantId));
        int views = 0;
        for (Goods goods : goodsList) {
            views += goods.getViews() == null ? 0 : goods.getViews();
        }
        vo.setGoodsViewCount(views);

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            List<Order> dayOrders = orderMapper.selectList(
                    new LambdaQueryWrapper<Order>()
                            .eq(Order::getMerchantId, merchantId)
                            .ge(Order::getCreatedAt, start)
                            .lt(Order::getCreatedAt, end));
            List<Order> dayPaidOrders = orderMapper.selectList(
                    new LambdaQueryWrapper<Order>()
                            .eq(Order::getMerchantId, merchantId)
                            .ge(Order::getPayTime, start)
                            .lt(Order::getPayTime, end)
                            .in(Order::getStatus, 1, 2, 3, 5));

            TrendPointVO trend = new TrendPointVO();
            trend.setDate(date.toString());
            trend.setOrderCount(dayOrders.size());
            trend.setSalesAmount(sumAmount(dayPaidOrders));
            vo.getTrends().add(trend);
        }

        return vo;
    }

    private BigDecimal sumAmount(List<Order> orders) {
        BigDecimal total = BigDecimal.ZERO;
        for (Order order : orders) {
            total = total.add(order.getPayAmount() == null ? BigDecimal.ZERO : order.getPayAmount());
        }
        return total;
    }
}
