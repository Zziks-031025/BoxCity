package com.boxcity.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxcity.common.BusinessException;
import com.boxcity.common.Constants;
import com.boxcity.dto.*;
import com.boxcity.entity.*;
import com.boxcity.mapper.*;
import com.boxcity.service.AdminService;
import com.boxcity.service.NotificationService;
import com.boxcity.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final UserMapper userMapper;
    private final MerchantMapper merchantMapper;
    private final GoodsMapper goodsMapper;
    private final GoodsImageMapper goodsImageMapper;
    private final GoodsAttributeMapper goodsAttributeMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final RefundMapper refundMapper;
    private final ReportMapper reportMapper;
    private final BannerMapper bannerMapper;
    private final CityMapper cityMapper;
    private final PaymentConfigMapper paymentConfigMapper;
    private final FreightTemplateGlobalMapper freightTemplateGlobalMapper;
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, request.getUsername())
                        .last("LIMIT 1"));
        if (admin == null || !BCrypt.checkpw(request.getPassword(), admin.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        if (admin.getStatus() == null || admin.getStatus() != 1) {
            throw new BusinessException(403, "账号已被禁用");
        }

        AdminLoginResponse response = new AdminLoginResponse();
        response.setAdminId(admin.getId());
        response.setUsername(admin.getUsername());
        response.setRole(admin.getRole());
        response.setToken(jwtUtil.generateToken(admin.getId(), Constants.USER_TYPE_ADMIN));
        return response;
    }

    @Override
    public AdminDashboardVO getDashboard() {
        AdminDashboardVO vo = new AdminDashboardVO();
        vo.setUserCount(Math.toIntExact(userMapper.selectCount(new LambdaQueryWrapper<>())));
        vo.setMerchantCount(Math.toIntExact(merchantMapper.selectCount(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getAuditStatus, 1))));
        vo.setGoodsCount(Math.toIntExact(goodsMapper.selectCount(
                new LambdaQueryWrapper<Goods>().eq(Goods::getAuditStatus, 1))));
        vo.setOrderCount(Math.toIntExact(orderMapper.selectCount(new LambdaQueryWrapper<>())));
        vo.setPendingMerchantCount(Math.toIntExact(merchantMapper.selectCount(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getAuditStatus, 0))));
        vo.setPendingGoodsCount(Math.toIntExact(goodsMapper.selectCount(
                new LambdaQueryWrapper<Goods>().eq(Goods::getAuditStatus, 0))));
        vo.setDisputeCount(Math.toIntExact(refundMapper.selectCount(
                new LambdaQueryWrapper<Refund>().eq(Refund::getStatus, 5))));

        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();
        List<Order> todayPaidOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .ge(Order::getPayTime, start)
                        .lt(Order::getPayTime, end)
                        .in(Order::getStatus, 1, 2, 3, 5));
        vo.setTodaySales(sumAmount(todayPaidOrders));
        return vo;
    }

    @Override
    public IPage<Map<String, Object>> getUserList(String keyword, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(status != null, User::getStatus, status)
                .and(StringUtils.hasText(keyword), w -> w.like(User::getNickname, keyword).or().like(User::getPhone, keyword))
                .orderByDesc(User::getCreatedAt);
        IPage<User> userPage = userMapper.selectPage(new Page<>(page, size), wrapper);
        return userPage.convert(this::toUserMap);
    }

    @Override
    public Map<String, Object> getUserDetail(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        Map<String, Object> detail = toUserMap(user);
        List<Order> orders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreatedAt)
                        .last("LIMIT 10"));
        detail.put("orders", buildOrderMaps(orders));
        detail.put("reports", reportMapper.selectList(
                new LambdaQueryWrapper<Report>()
                        .eq(Report::getReporterId, userId)
                        .orderByDesc(Report::getCreatedAt)
                        .last("LIMIT 10")));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public IPage<Map<String, Object>> getReportList(Integer status, Integer page, Integer size) {
        IPage<Report> reportPage = reportMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Report>()
                        .eq(status != null, Report::getStatus, status)
                        .orderByDesc(Report::getCreatedAt));
        return reportPage.convert(this::toReportMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleReport(Long adminId, Long reportId, ReportHandleRequest request) {
        Report report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException(404, "举报记录不存在");
        }
        report.setStatus(1);
        report.setHandleResult(request.getHandleResult());
        report.setHandlerId(adminId);
        reportMapper.updateById(report);
    }

    @Override
    public IPage<Map<String, Object>> getMerchantAuditList(Integer auditStatus, Integer page, Integer size) {
        IPage<Merchant> merchantPage = merchantMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Merchant>()
                        .eq(auditStatus != null, Merchant::getAuditStatus, auditStatus)
                        .orderByDesc(Merchant::getCreatedAt));
        return merchantPage.convert(this::toMerchantMap);
    }

    @Override
    public Map<String, Object> getMerchantDetail(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException(404, "商家不存在");
        }
        Map<String, Object> detail = toMerchantMap(merchant);
        detail.put("goodsCount", goodsMapper.selectCount(new LambdaQueryWrapper<Goods>().eq(Goods::getMerchantId, merchantId)));
        detail.put("orderCount", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getMerchantId, merchantId)));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditMerchant(Long merchantId, boolean approved, String remark) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException(404, "商家不存在");
        }
        merchant.setAuditStatus(approved ? 1 : 2);
        merchant.setAuditRemark(remark);
        merchantMapper.updateById(merchant);
        notificationService.sendMerchantAuditNotice(merchant, approved, remark);
    }

    @Override
    public IPage<Map<String, Object>> getMerchantList(Integer auditStatus, Integer page, Integer size) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<Merchant>()
                .eq(auditStatus != null, Merchant::getAuditStatus, auditStatus)
                .orderByDesc(Merchant::getCreatedAt);
        IPage<Merchant> merchantPage = merchantMapper.selectPage(new Page<>(page, size), wrapper);
        return merchantPage.convert(this::toMerchantMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMerchantStatus(Long merchantId, Integer status, String remark) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException(404, "商家不存在");
        }
        merchant.setAuditStatus(status);
        merchant.setAuditRemark(remark);
        merchantMapper.updateById(merchant);
    }

    @Override
    public IPage<Map<String, Object>> getGoodsAuditList(Integer auditStatus, Integer page, Integer size) {
        IPage<Goods> goodsPage = goodsMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Goods>()
                        .eq(auditStatus != null, Goods::getAuditStatus, auditStatus)
                        .orderByDesc(Goods::getCreatedAt));
        return goodsPage.convert(this::toGoodsMap);
    }

    @Override
    public Map<String, Object> getGoodsDetail(Long goodsId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException(404, "商品不存在");
        }
        Map<String, Object> detail = toGoodsMap(goods);
        detail.put("images", goodsImageMapper.selectList(new LambdaQueryWrapper<GoodsImage>().eq(GoodsImage::getGoodsId, goodsId)));
        detail.put("attributes", goodsAttributeMapper.selectList(new LambdaQueryWrapper<GoodsAttribute>().eq(GoodsAttribute::getGoodsId, goodsId)));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditGoods(Long goodsId, boolean approved, String remark) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException(404, "商品不存在");
        }
        goods.setAuditStatus(approved ? 1 : 2);
        goods.setAuditRemark(remark);
        if (!approved) {
            goods.setStatus(0);
        }
        goodsMapper.updateById(goods);
        notificationService.sendGoodsAuditNotice(goods, merchantMapper.selectById(goods.getMerchantId()), approved, remark);
    }

    @Override
    public IPage<Map<String, Object>> getOrderList(String keyword, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(status != null, Order::getStatus, status)
                .like(StringUtils.hasText(keyword), Order::getOrderNo, keyword)
                .orderByDesc(Order::getCreatedAt);
        IPage<Order> orderPage = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return orderPage.convert(this::toOrderMap);
    }

    @Override
    public IPage<Map<String, Object>> getDisputeList(Integer page, Integer size) {
        IPage<Refund> refundPage = refundMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Refund>()
                        .eq(Refund::getStatus, 5)
                        .orderByDesc(Refund::getCreatedAt));
        return refundPage.convert(this::toDisputeMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleDispute(Long adminId, Long refundId, DisputeHandleRequest request) {
        Refund refund = refundMapper.selectById(refundId);
        if (refund == null) {
            throw new BusinessException(404, "售后记录不存在");
        }
        Order order = orderMapper.selectById(refund.getOrderId());
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        if (request.getApprove()) {
            refund.setStatus(3);
            refund.setRejectReason(request.getRemark());
            refundMapper.updateById(refund);
            order.setStatus(4);
            order.setCancelTime(LocalDateTime.now());
            orderMapper.updateById(order);
            if (order.getShipTime() == null || (refund.getRefundType() != null && refund.getRefundType() == 1)) {
                restoreStock(order.getId());
            }
        } else {
            refund.setStatus(6);
            refund.setRejectReason(request.getRemark());
            refundMapper.updateById(refund);
            restoreOrderStatus(order);
        }
    }

    @Override
    public IPage<Map<String, Object>> getAbnormalOrderList(Integer page, Integer size) {
        List<Order> allOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>().orderByDesc(Order::getCreatedAt));
        List<Map<String, Object>> abnormal = new ArrayList<>();
        for (Order order : allOrders) {
            long refundCount = refundMapper.selectCount(new LambdaQueryWrapper<Refund>().eq(Refund::getOrderId, order.getId()));
            if ((order.getStatus() != null && order.getStatus() == 5) || refundCount > 1) {
                Map<String, Object> row = toOrderMap(order);
                row.put("abnormalReason", order.getStatus() != null && order.getStatus() == 5 ? "售后处理中" : "同订单多次退款");
                abnormal.add(row);
            }
        }
        return pageFromList(abnormal, page, size);
    }

    @Override
    public PaymentConfig getPaymentConfig() {
        PaymentConfig config = paymentConfigMapper.selectOne(new LambdaQueryWrapper<PaymentConfig>().last("LIMIT 1"));
        return config == null ? new PaymentConfig() : config;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePaymentConfig(PaymentConfig config) {
        PaymentConfig existing = paymentConfigMapper.selectOne(new LambdaQueryWrapper<PaymentConfig>().last("LIMIT 1"));
        if (existing == null) {
            paymentConfigMapper.insert(config);
        } else {
            config.setId(existing.getId());
            paymentConfigMapper.updateById(config);
        }
    }

    @Override
    public FreightTemplateGlobal getGlobalFreightConfig() {
        FreightTemplateGlobal config = freightTemplateGlobalMapper.selectOne(
                new LambdaQueryWrapper<FreightTemplateGlobal>().last("LIMIT 1"));
        return config == null ? new FreightTemplateGlobal() : config;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveGlobalFreightConfig(FreightTemplateGlobal config) {
        FreightTemplateGlobal existing = freightTemplateGlobalMapper.selectOne(
                new LambdaQueryWrapper<FreightTemplateGlobal>().last("LIMIT 1"));
        if (existing == null) {
            freightTemplateGlobalMapper.insert(config);
        } else {
            config.setId(existing.getId());
            freightTemplateGlobalMapper.updateById(config);
        }
    }

    @Override
    public IPage<Banner> getBannerList(Integer page, Integer size) {
        return bannerMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Banner>().orderByAsc(Banner::getSort).orderByDesc(Banner::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBanner(Banner banner) {
        bannerMapper.insert(banner);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBanner(Long id, Banner banner) {
        banner.setId(id);
        bannerMapper.updateById(banner);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBanner(Long id) {
        bannerMapper.deleteById(id);
    }

    @Override
    public IPage<City> getCityList(String keyword, Long parentId, Integer level, Integer page, Integer size) {
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<City>()
                .eq(parentId != null, City::getParentId, parentId)
                .eq(level != null, City::getLevel, level)
                .like(StringUtils.hasText(keyword), City::getName, keyword)
                .orderByAsc(City::getSort)
                .orderByAsc(City::getId);
        return cityMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCity(City city) {
        city.setCreatedAt(LocalDateTime.now());
        cityMapper.insert(city);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCity(Long id, City city) {
        city.setId(id);
        cityMapper.updateById(city);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCity(Long id) {
        cityMapper.deleteById(id);
    }

    private Map<String, Object> toUserMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("nickname", user.getNickname());
        map.put("avatar", user.getAvatar());
        map.put("phone", user.getPhone());
        map.put("gender", user.getGender());
        map.put("birthday", user.getBirthday());
        map.put("cityId", user.getCityId());
        map.put("status", user.getStatus());
        map.put("createdAt", user.getCreatedAt());
        map.put("orderCount", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getUserId, user.getId())));
        return map;
    }

    private Map<String, Object> toReportMap(Report report) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", report.getId());
        map.put("targetType", report.getTargetType());
        map.put("targetId", report.getTargetId());
        map.put("reason", report.getReason());
        map.put("evidenceImages", report.getEvidenceImages());
        map.put("status", report.getStatus());
        map.put("handleResult", report.getHandleResult());
        map.put("createdAt", report.getCreatedAt());
        User reporter = userMapper.selectById(report.getReporterId());
        map.put("reporterName", reporter == null ? "" : reporter.getNickname());
        return map;
    }

    private Map<String, Object> toMerchantMap(Merchant merchant) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", merchant.getId());
        map.put("shopName", merchant.getShopName());
        map.put("shopAvatar", merchant.getShopAvatar());
        map.put("contactPhone", merchant.getContactPhone());
        map.put("contactEmail", merchant.getContactEmail());
        map.put("subjectType", merchant.getSubjectType());
        map.put("auditStatus", merchant.getAuditStatus());
        map.put("auditRemark", merchant.getAuditRemark());
        map.put("creditScore", merchant.getCreditScore());
        map.put("createdAt", merchant.getCreatedAt());
        return map;
    }

    private Map<String, Object> toGoodsMap(Goods goods) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", goods.getId());
        map.put("merchantId", goods.getMerchantId());
        map.put("title", goods.getTitle());
        map.put("price", goods.getPrice());
        map.put("stock", goods.getStock());
        map.put("sales", goods.getSales());
        map.put("auditStatus", goods.getAuditStatus());
        map.put("auditRemark", goods.getAuditRemark());
        map.put("status", goods.getStatus());
        map.put("createdAt", goods.getCreatedAt());
        Merchant merchant = merchantMapper.selectById(goods.getMerchantId());
        map.put("shopName", merchant == null ? "" : merchant.getShopName());
        GoodsImage mainImage = goodsImageMapper.selectOne(
                new LambdaQueryWrapper<GoodsImage>()
                        .eq(GoodsImage::getGoodsId, goods.getId())
                        .eq(GoodsImage::getType, 1)
                        .orderByAsc(GoodsImage::getSort)
                        .last("LIMIT 1"));
        map.put("mainImage", mainImage == null ? "" : mainImage.getImageUrl());
        return map;
    }

    private Map<String, Object> toOrderMap(Order order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("orderNo", order.getOrderNo());
        map.put("status", order.getStatus());
        map.put("payAmount", order.getPayAmount());
        map.put("createdAt", order.getCreatedAt());
        map.put("payTime", order.getPayTime());
        map.put("userId", order.getUserId());
        map.put("merchantId", order.getMerchantId());
        User user = userMapper.selectById(order.getUserId());
        Merchant merchant = merchantMapper.selectById(order.getMerchantId());
        map.put("userName", user == null ? "" : user.getNickname());
        map.put("shopName", merchant == null ? "" : merchant.getShopName());
        Refund refund = refundMapper.selectOne(
                new LambdaQueryWrapper<Refund>()
                        .eq(Refund::getOrderId, order.getId())
                        .orderByDesc(Refund::getCreatedAt)
                        .last("LIMIT 1"));
        map.put("refundStatus", refund == null ? null : refund.getStatus());
        return map;
    }

    private Map<String, Object> toDisputeMap(Refund refund) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", refund.getId());
        map.put("orderId", refund.getOrderId());
        map.put("orderItemId", refund.getOrderItemId());
        map.put("refundType", refund.getRefundType());
        map.put("refundReason", refund.getRefundReason());
        map.put("refundAmount", refund.getRefundAmount());
        map.put("status", refund.getStatus());
        map.put("rejectReason", refund.getRejectReason());
        map.put("createdAt", refund.getCreatedAt());

        Order order = orderMapper.selectById(refund.getOrderId());
        if (order != null) {
            map.put("orderNo", order.getOrderNo());
        }
        Merchant merchant = merchantMapper.selectById(refund.getMerchantId());
        map.put("shopName", merchant == null ? "" : merchant.getShopName());
        if (refund.getOrderItemId() != null) {
            OrderItem item = orderItemMapper.selectById(refund.getOrderItemId());
            if (item != null) {
                Goods goods = goodsMapper.selectById(item.getGoodsId());
                map.put("goodsTitle", goods == null ? "" : goods.getTitle());
            }
        }
        return map;
    }

    private List<Map<String, Object>> buildOrderMaps(List<Order> orders) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Order order : orders) {
            result.add(toOrderMap(order));
        }
        return result;
    }

    private BigDecimal sumAmount(List<Order> orders) {
        BigDecimal total = BigDecimal.ZERO;
        for (Order order : orders) {
            total = total.add(order.getPayAmount() == null ? BigDecimal.ZERO : order.getPayAmount());
        }
        return total;
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

    private void restoreOrderStatus(Order order) {
        if (order.getReceiveTime() != null) {
            order.setStatus(3);
        } else if (order.getShipTime() != null) {
            order.setStatus(2);
        } else if (order.getPayTime() != null) {
            order.setStatus(1);
        } else if (order.getCancelTime() != null) {
            order.setStatus(4);
        } else {
            order.setStatus(0);
        }
        orderMapper.updateById(order);
    }

    private IPage<Map<String, Object>> pageFromList(List<Map<String, Object>> records, Integer page, Integer size) {
        int current = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 10 : size;
        int fromIndex = Math.min((current - 1) * pageSize, records.size());
        int toIndex = Math.min(fromIndex + pageSize, records.size());

        Page<Map<String, Object>> result = new Page<>(current, pageSize, records.size());
        result.setRecords(records.subList(fromIndex, toIndex));
        return result;
    }
}
