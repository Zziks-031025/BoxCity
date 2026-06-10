package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxcity.common.BusinessException;
import com.boxcity.dto.CreateOrderRequest;
import com.boxcity.dto.OrderItemVO;
import com.boxcity.dto.OrderListVO;
import com.boxcity.dto.OrderPreviewVO;
import com.boxcity.dto.OrderVO;
import com.boxcity.dto.ShipOrderRequest;
import com.boxcity.entity.Cart;
import com.boxcity.entity.FreightTemplate;
import com.boxcity.entity.FreightTemplateRule;
import com.boxcity.entity.Goods;
import com.boxcity.entity.GoodsImage;
import com.boxcity.entity.Merchant;
import com.boxcity.entity.Order;
import com.boxcity.entity.OrderItem;
import com.boxcity.entity.OrderLogistics;
import com.boxcity.entity.Payment;
import com.boxcity.entity.Refund;
import com.boxcity.entity.Review;
import com.boxcity.entity.UserAddress;
import com.boxcity.mapper.CartMapper;
import com.boxcity.mapper.FreightTemplateMapper;
import com.boxcity.mapper.FreightTemplateRuleMapper;
import com.boxcity.mapper.GoodsImageMapper;
import com.boxcity.mapper.GoodsMapper;
import com.boxcity.mapper.MerchantMapper;
import com.boxcity.mapper.OrderItemMapper;
import com.boxcity.mapper.OrderLogisticsMapper;
import com.boxcity.mapper.OrderMapper;
import com.boxcity.mapper.PaymentMapper;
import com.boxcity.mapper.RefundMapper;
import com.boxcity.mapper.ReviewMapper;
import com.boxcity.mapper.UserAddressMapper;
import com.boxcity.service.NotificationService;
import com.boxcity.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final GoodsMapper goodsMapper;
    private final GoodsImageMapper goodsImageMapper;
    private final UserAddressMapper userAddressMapper;
    private final MerchantMapper merchantMapper;
    private final OrderLogisticsMapper orderLogisticsMapper;
    private final PaymentMapper paymentMapper;
    private final RefundMapper refundMapper;
    private final ReviewMapper reviewMapper;
    private final CartMapper cartMapper;
    private final FreightTemplateMapper freightTemplateMapper;
    private final FreightTemplateRuleMapper freightTemplateRuleMapper;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Override
    public OrderPreviewVO previewOrder(Long userId, CreateOrderRequest request) {
        loadAddress(userId, request.getAddressId());
        List<Cart> cartItems = resolveCartItems(userId, request);
        if (CollectionUtils.isEmpty(cartItems)) {
            throw new BusinessException("请选择商品");
        }
        return buildPreview(cartItems);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(Long userId, CreateOrderRequest request) {
        UserAddress address = loadAddress(userId, request.getAddressId());
        List<Cart> cartItems = resolveCartItems(userId, request);
        if (CollectionUtils.isEmpty(cartItems)) {
            throw new BusinessException("请选择商品");
        }

        Map<Long, List<Cart>> merchantCartMap = cartItems.stream()
                .collect(Collectors.groupingBy(Cart::getMerchantId, LinkedHashMap::new, Collectors.toList()));

        List<String> orderNos = new ArrayList<>();
        for (Map.Entry<Long, List<Cart>> entry : merchantCartMap.entrySet()) {
            MerchantOrderData data = validateMerchantOrder(entry.getKey(), entry.getValue());
            Order order = createSingleOrder(userId, address, request.getBuyerMessage(), data);
            orderNos.add(order.getOrderNo());
        }

        if (!CollectionUtils.isEmpty(request.getCartIds())) {
            cartMapper.delete(new LambdaQueryWrapper<Cart>()
                    .eq(Cart::getUserId, userId)
                    .in(Cart::getId, request.getCartIds()));
        }

        return String.join(",", orderNos);
    }

    @Override
    public IPage<OrderListVO> getOrderList(Long userId, Integer status, Integer page, Integer size) {
        IPage<Order> orderPage = orderMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .eq(status != null, Order::getStatus, status)
                        .orderByDesc(Order::getCreatedAt));
        return orderPage.convert(this::toOrderListVO);
    }

    @Override
    public OrderVO getOrderDetail(Long userId, String orderNo) {
        Order order = getOrderByNo(orderNo);
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(403, "无权查看该订单");
        }
        return toOrderVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mockPayOrder(Long userId, String orderNo) {
        Order order = getOrderByNo(orderNo);
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(403, "无权操作该订单");
        }
        if (order.getStatus() != null && order.getStatus() == 1 && order.getPayTime() != null) {
            return;
        }
        if (order.getStatus() == null || order.getStatus() != 0) {
            throw new BusinessException("当前订单状态不可支付");
        }

        LocalDateTime now = LocalDateTime.now();
        order.setStatus(1);
        order.setPayTime(now);
        orderMapper.updateById(order);

        recordMockPayment(order, now);
        increaseSales(order.getId());
        notificationService.sendOrderPaidNotice(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, String orderNo) {
        Order order = getOrderByNo(orderNo);
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(403, "无权操作该订单");
        }
        if (order.getStatus() == null || order.getStatus() != 0) {
            throw new BusinessException("只有待付款订单可以取消");
        }
        order.setStatus(4);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);
        restoreStock(order.getId());
        notificationService.sendOrderCancelledNotice(order, "用户主动取消");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(Long userId, String orderNo) {
        Order order = getOrderByNo(orderNo);
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(403, "无权操作该订单");
        }
        if (order.getStatus() == null || order.getStatus() != 2) {
            throw new BusinessException("只有待收货订单可以确认收货");
        }
        order.setStatus(3);
        order.setReceiveTime(LocalDateTime.now());
        orderMapper.updateById(order);
        notificationService.sendOrderCompletedNotice(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long userId, String orderNo) {
        Order order = getOrderByNo(orderNo);
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(403, "无权操作该订单");
        }
        if (order.getStatus() == null || (order.getStatus() != 3 && order.getStatus() != 4)) {
            throw new BusinessException("只有已完成或已取消的订单可以删除");
        }
        orderMapper.deleteById(order.getId());
    }

    @Override
    public IPage<OrderListVO> getMerchantOrderList(Long merchantId, Integer status, Integer page, Integer size) {
        IPage<Order> orderPage = orderMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getMerchantId, merchantId)
                        .eq(status != null, Order::getStatus, status)
                        .orderByDesc(Order::getCreatedAt));
        return orderPage.convert(this::toOrderListVO);
    }

    @Override
    public OrderVO getMerchantOrderDetail(Long merchantId, String orderNo) {
        Order order = getOrderByNo(orderNo);
        if (!merchantId.equals(order.getMerchantId())) {
            throw new BusinessException(403, "无权查看该订单");
        }
        return toOrderVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(Long merchantId, String orderNo, ShipOrderRequest request) {
        Order order = getOrderByNo(orderNo);
        if (!merchantId.equals(order.getMerchantId())) {
            throw new BusinessException(403, "无权操作该订单");
        }
        if (order.getStatus() == null || order.getStatus() != 1) {
            throw new BusinessException("只有待发货订单可以发货");
        }

        LocalDateTime now = LocalDateTime.now();
        List<Map<String, Object>> traces = new ArrayList<>();
        traces.add(buildTrace(now, "商家已发货", "包裹已交由" + request.getLogisticsCompany() + "配送"));

        OrderLogistics logistics = orderLogisticsMapper.selectOne(
                new LambdaQueryWrapper<OrderLogistics>()
                        .eq(OrderLogistics::getOrderId, order.getId())
                        .last("LIMIT 1"));
        if (logistics == null) {
            logistics = new OrderLogistics();
            logistics.setOrderId(order.getId());
            logistics.setLogisticsCompany(request.getLogisticsCompany());
            logistics.setLogisticsNo(request.getLogisticsNo());
            logistics.setLogisticsStatus(1);
            logistics.setLogisticsData(toJson(traces));
            orderLogisticsMapper.insert(logistics);
        } else {
            logistics.setLogisticsCompany(request.getLogisticsCompany());
            logistics.setLogisticsNo(request.getLogisticsNo());
            logistics.setLogisticsStatus(1);
            logistics.setLogisticsData(toJson(traces));
            orderLogisticsMapper.updateById(logistics);
        }

        order.setStatus(2);
        order.setShipTime(now);
        orderMapper.updateById(order);
        notificationService.sendOrderShippedNotice(order, request.getLogisticsCompany(), request.getLogisticsNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTimeoutOrders() {
        List<Order> timeoutOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, 0)
                        .lt(Order::getAutoCancelTime, LocalDateTime.now()));
        for (Order order : timeoutOrders) {
            order.setStatus(4);
            order.setCancelTime(LocalDateTime.now());
            orderMapper.updateById(order);
            restoreStock(order.getId());
            notificationService.sendOrderCancelledNotice(order, "订单超时未支付");
            log.info("取消超时未支付订单: {}", order.getOrderNo());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoConfirmOrders() {
        List<Order> orders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, 2)
                        .lt(Order::getShipTime, LocalDateTime.now().minusDays(15)));
        for (Order order : orders) {
            order.setStatus(3);
            order.setReceiveTime(LocalDateTime.now());
            orderMapper.updateById(order);
            notificationService.sendOrderCompletedNotice(order);
            log.info("自动确认收货: {}", order.getOrderNo());
        }
    }

    private OrderPreviewVO buildPreview(List<Cart> cartItems) {
        OrderPreviewVO preview = new OrderPreviewVO();
        Map<Long, List<Cart>> merchantCartMap = cartItems.stream()
                .collect(Collectors.groupingBy(Cart::getMerchantId, LinkedHashMap::new, Collectors.toList()));

        for (Map.Entry<Long, List<Cart>> entry : merchantCartMap.entrySet()) {
            MerchantOrderData data = validateMerchantOrder(entry.getKey(), entry.getValue());
            OrderPreviewVO.MerchantPreviewVO merchantPreview = new OrderPreviewVO.MerchantPreviewVO();
            merchantPreview.setMerchantId(data.getMerchantId());
            merchantPreview.setShopName(data.getShopName());
            merchantPreview.setTotalAmount(data.getTotalAmount());
            merchantPreview.setFreightAmount(data.getFreightAmount());
            merchantPreview.setPayAmount(data.getPayAmount());

            for (ResolvedCartItem item : data.getItems()) {
                OrderPreviewVO.PreviewItemVO previewItem = new OrderPreviewVO.PreviewItemVO();
                previewItem.setCartId(item.getCart().getId());
                previewItem.setGoodsId(item.getGoods().getId());
                previewItem.setGoodsName(item.getGoods().getTitle());
                previewItem.setImageUrl(findMainImage(item.getGoods().getId()));
                previewItem.setPrice(item.getGoods().getPrice());
                previewItem.setQuantity(item.getCart().getQuantity());
                merchantPreview.getItems().add(previewItem);
            }

            preview.getMerchantOrders().add(merchantPreview);
            preview.setTotalAmount(preview.getTotalAmount().add(merchantPreview.getTotalAmount()));
            preview.setFreightAmount(preview.getFreightAmount().add(merchantPreview.getFreightAmount()));
            preview.setPayAmount(preview.getPayAmount().add(merchantPreview.getPayAmount()));
        }
        return preview;
    }

    private Order createSingleOrder(Long userId, UserAddress address, String buyerMessage, MerchantOrderData data) {
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setMerchantId(data.getMerchantId());
        order.setTotalAmount(data.getTotalAmount());
        order.setFreightAmount(data.getFreightAmount());
        order.setPayAmount(data.getPayAmount());
        order.setAddressSnapshot(toJson(address));
        order.setBuyerMessage(buyerMessage);
        order.setStatus(0);
        order.setAutoCancelTime(LocalDateTime.now().plusMinutes(30));
        orderMapper.insert(order);

        for (ResolvedCartItem item : data.getItems()) {
            Goods goods = item.getGoods();
            Cart cart = item.getCart();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setGoodsId(goods.getId());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setPrice(goods.getPrice());
            orderItem.setGoodsSnapshot(toJson(buildGoodsSnapshot(goods)));
            orderItemMapper.insert(orderItem);

            int updated = goodsMapper.update(
                    null,
                    new LambdaUpdateWrapper<Goods>()
                            .eq(Goods::getId, goods.getId())
                            .ge(Goods::getStock, cart.getQuantity())
                            .setSql("stock = stock - " + cart.getQuantity()));
            if (updated == 0) {
                throw new BusinessException("商品库存不足: " + goods.getTitle());
            }
        }
        return order;
    }

    private Map<String, Object> buildGoodsSnapshot(Goods goods) {
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("goodsId", goods.getId());
        snapshot.put("title", goods.getTitle());
        snapshot.put("price", goods.getPrice());
        snapshot.put("imageUrl", findMainImage(goods.getId()));
        return snapshot;
    }

    private MerchantOrderData validateMerchantOrder(Long merchantId, List<Cart> carts) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException("店铺不存在");
        }

        MerchantOrderData data = new MerchantOrderData();
        data.setMerchantId(merchantId);
        data.setShopName(merchant.getShopName());
        int totalQuantity = 0;

        for (Cart cart : carts) {
            Goods goods = goodsMapper.selectById(cart.getGoodsId());
            validateGoods(goods, cart.getQuantity());

            ResolvedCartItem item = new ResolvedCartItem();
            item.setCart(cart);
            item.setGoods(goods);
            data.getItems().add(item);

            data.setTotalAmount(data.getTotalAmount().add(goods.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()))));
            totalQuantity += cart.getQuantity();
        }

        BigDecimal freightAmount = calculateFreight(merchantId, totalQuantity);
        data.setFreightAmount(freightAmount);
        data.setPayAmount(data.getTotalAmount().add(freightAmount));
        return data;
    }

    private void validateGoods(Goods goods, Integer quantity) {
        if (goods == null || (goods.getDeleted() != null && goods.getDeleted() == 1)) {
            throw new BusinessException("商品不存在");
        }
        if (goods.getStatus() == null || goods.getStatus() != 1) {
            throw new BusinessException("商品已下架: " + goods.getTitle());
        }
        if (goods.getAuditStatus() == null || goods.getAuditStatus() != 1) {
            throw new BusinessException("商品未通过审核: " + goods.getTitle());
        }
        if (goods.getStock() == null || goods.getStock() < quantity) {
            throw new BusinessException("商品库存不足: " + goods.getTitle());
        }
    }

    private BigDecimal calculateFreight(Long merchantId, int totalQuantity) {
        FreightTemplate template = freightTemplateMapper.selectOne(
                new LambdaQueryWrapper<FreightTemplate>()
                        .eq(FreightTemplate::getMerchantId, merchantId)
                        .orderByDesc(FreightTemplate::getIsDefault)
                        .orderByDesc(FreightTemplate::getUpdatedAt)
                        .last("LIMIT 1"));
        if (template == null || template.getChargeType() == null || template.getChargeType() == 0) {
            return BigDecimal.ZERO;
        }

        List<FreightTemplateRule> rules = freightTemplateRuleMapper.selectList(
                new LambdaQueryWrapper<FreightTemplateRule>()
                        .eq(FreightTemplateRule::getTemplateId, template.getId())
                        .orderByAsc(FreightTemplateRule::getId));
        if (rules.isEmpty()) {
            return BigDecimal.ZERO;
        }

        FreightTemplateRule rule = rules.get(0);
        int firstUnit = rule.getFirstUnit() == null || rule.getFirstUnit() < 1 ? 1 : rule.getFirstUnit();
        int additionalUnit = rule.getAdditionalUnit() == null || rule.getAdditionalUnit() < 1 ? 1 : rule.getAdditionalUnit();
        BigDecimal firstFee = rule.getFirstFee() == null ? BigDecimal.ZERO : rule.getFirstFee();
        BigDecimal additionalFee = rule.getAdditionalFee() == null ? BigDecimal.ZERO : rule.getAdditionalFee();
        if (totalQuantity <= firstUnit) {
            return firstFee.setScale(2, RoundingMode.HALF_UP);
        }
        int extraUnits = totalQuantity - firstUnit;
        int step = (int) Math.ceil(extraUnits / (double) additionalUnit);
        return firstFee.add(additionalFee.multiply(BigDecimal.valueOf(step))).setScale(2, RoundingMode.HALF_UP);
    }

    private List<Cart> resolveCartItems(Long userId, CreateOrderRequest request) {
        if (!CollectionUtils.isEmpty(request.getCartIds())) {
            List<Cart> carts = cartMapper.selectList(
                    new LambdaQueryWrapper<Cart>()
                            .eq(Cart::getUserId, userId)
                            .in(Cart::getId, request.getCartIds())
                            .orderByAsc(Cart::getId));
            if (carts.size() != request.getCartIds().size()) {
                throw new BusinessException("部分购物车商品不存在");
            }
            return carts;
        }

        if (request.getGoodsId() != null && request.getQuantity() != null) {
            Goods goods = goodsMapper.selectById(request.getGoodsId());
            validateGoods(goods, request.getQuantity());
            Cart cart = new Cart();
            cart.setGoodsId(goods.getId());
            cart.setMerchantId(goods.getMerchantId());
            cart.setQuantity(request.getQuantity());
            return List.of(cart);
        }
        return new ArrayList<>();
    }

    private UserAddress loadAddress(Long userId, Long addressId) {
        UserAddress address = userAddressMapper.selectOne(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getId, addressId)
                        .eq(UserAddress::getUserId, userId)
                        .last("LIMIT 1"));
        if (address == null) {
            throw new BusinessException("收货地址不存在");
        }
        return address;
    }

    private void restoreStock(Long orderId) {
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            goodsMapper.update(
                    null,
                    new LambdaUpdateWrapper<Goods>()
                            .eq(Goods::getId, item.getGoodsId())
                            .setSql("stock = stock + " + item.getQuantity()));
        }
    }

    private void increaseSales(Long orderId) {
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            goodsMapper.update(
                    null,
                    new LambdaUpdateWrapper<Goods>()
                            .eq(Goods::getId, item.getGoodsId())
                            .setSql("sales = sales + " + item.getQuantity()));
        }
    }

    private void recordMockPayment(Order order, LocalDateTime payTime) {
        Payment payment = paymentMapper.selectOne(
                new LambdaQueryWrapper<Payment>()
                        .eq(Payment::getOrderId, order.getId())
                        .last("LIMIT 1"));
        if (payment == null) {
            payment = new Payment();
            payment.setOrderId(order.getId());
            payment.setTransactionId("MOCK" + generateOrderNo());
            payment.setPayAmount(order.getPayAmount());
            payment.setPayStatus(1);
            payment.setPayTime(payTime);
            paymentMapper.insert(payment);
            return;
        }
        payment.setTransactionId(payment.getTransactionId() == null ? "MOCK" + generateOrderNo() : payment.getTransactionId());
        payment.setPayAmount(order.getPayAmount());
        payment.setPayStatus(1);
        payment.setPayTime(payTime);
        paymentMapper.updateById(payment);
    }

    private Order getOrderByNo(String orderNo) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
                        .last("LIMIT 1"));
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private OrderVO toOrderVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setMerchantId(order.getMerchantId());
        vo.setStatus(order.getStatus());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setFreightAmount(order.getFreightAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setAddressSnapshot(order.getAddressSnapshot());
        vo.setBuyerMessage(order.getBuyerMessage());
        vo.setPayTime(order.getPayTime());
        vo.setShipTime(order.getShipTime());
        vo.setReceiveTime(order.getReceiveTime());
        vo.setCancelTime(order.getCancelTime());
        vo.setCreatedAt(order.getCreatedAt());

        Merchant merchant = merchantMapper.selectById(order.getMerchantId());
        if (merchant != null) {
            vo.setShopName(merchant.getShopName());
        }

        OrderLogistics logistics = orderLogisticsMapper.selectOne(
                new LambdaQueryWrapper<OrderLogistics>()
                        .eq(OrderLogistics::getOrderId, order.getId())
                        .last("LIMIT 1"));
        if (logistics != null) {
            vo.setLogisticsCompany(logistics.getLogisticsCompany());
            vo.setLogisticsNo(logistics.getLogisticsNo());
            vo.setLogisticsStatus(logistics.getLogisticsStatus());
            vo.setLogisticsData(logistics.getLogisticsData());
        }

        Refund refund = refundMapper.selectOne(
                new LambdaQueryWrapper<Refund>()
                        .eq(Refund::getOrderId, order.getId())
                        .in(Refund::getStatus, 0, 1, 2, 3, 4, 5)
                        .orderByDesc(Refund::getCreatedAt)
                        .last("LIMIT 1"));
        if (refund != null) {
            vo.setRefundId(refund.getId());
            vo.setRefundStatus(refund.getStatus());
        }

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        vo.setItems(items.stream().map(this::toOrderItemVO).collect(Collectors.toList()));
        return vo;
    }

    private OrderListVO toOrderListVO(Order order) {
        OrderListVO vo = new OrderListVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setStatus(order.getStatus());
        vo.setPayAmount(order.getPayAmount());
        vo.setCreatedAt(order.getCreatedAt());
        vo.setMerchantId(order.getMerchantId());

        Merchant merchant = merchantMapper.selectById(order.getMerchantId());
        if (merchant != null) {
            vo.setShopName(merchant.getShopName());
        }

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        vo.setItemCount(items.size());
        if (!items.isEmpty()) {
            vo.setFirstItem(toOrderItemVO(items.get(0)));
        }
        return vo;
    }

    private OrderItemVO toOrderItemVO(OrderItem item) {
        OrderItemVO vo = new OrderItemVO();
        vo.setId(item.getId());
        vo.setGoodsId(item.getGoodsId());
        vo.setPrice(item.getPrice());
        vo.setQuantity(item.getQuantity());
        vo.setReviewed(reviewMapper.selectCount(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getOrderItemId, item.getId())
                        .eq(Review::getIsAppend, 0)) > 0);

        Goods goods = goodsMapper.selectById(item.getGoodsId());
        if (goods != null) {
            vo.setGoodsTitle(goods.getTitle());
        }
        vo.setMainImage(findMainImage(item.getGoodsId()));
        return vo;
    }

    private String findMainImage(Long goodsId) {
        GoodsImage mainImage = goodsImageMapper.selectOne(
                new LambdaQueryWrapper<GoodsImage>()
                        .eq(GoodsImage::getGoodsId, goodsId)
                        .eq(GoodsImage::getType, 1)
                        .orderByAsc(GoodsImage::getSort)
                        .last("LIMIT 1"));
        return mainImage == null ? "" : mainImage.getImageUrl();
    }

    private Map<String, Object> buildTrace(LocalDateTime time, String status, String content) {
        Map<String, Object> trace = new HashMap<>();
        trace.put("time", time);
        trace.put("status", status);
        trace.put("content", content);
        return trace;
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(9000) + 1000;
        return "BC" + timestamp + random;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON 序列化失败", e);
            return "{}";
        }
    }

    private static class ResolvedCartItem {
        private Cart cart;
        private Goods goods;

        public Cart getCart() {
            return cart;
        }

        public void setCart(Cart cart) {
            this.cart = cart;
        }

        public Goods getGoods() {
            return goods;
        }

        public void setGoods(Goods goods) {
            this.goods = goods;
        }
    }

    private static class MerchantOrderData {
        private Long merchantId;
        private String shopName;
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private BigDecimal freightAmount = BigDecimal.ZERO;
        private BigDecimal payAmount = BigDecimal.ZERO;
        private final List<ResolvedCartItem> items = new ArrayList<>();

        public Long getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(Long merchantId) {
            this.merchantId = merchantId;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public BigDecimal getFreightAmount() {
            return freightAmount;
        }

        public void setFreightAmount(BigDecimal freightAmount) {
            this.freightAmount = freightAmount;
        }

        public BigDecimal getPayAmount() {
            return payAmount;
        }

        public void setPayAmount(BigDecimal payAmount) {
            this.payAmount = payAmount;
        }

        public List<ResolvedCartItem> getItems() {
            return items;
        }
    }
}
