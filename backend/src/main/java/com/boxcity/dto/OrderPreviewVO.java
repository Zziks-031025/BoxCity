package com.boxcity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderPreviewVO {

    private BigDecimal totalAmount = BigDecimal.ZERO;

    private BigDecimal freightAmount = BigDecimal.ZERO;

    private BigDecimal payAmount = BigDecimal.ZERO;

    private List<MerchantPreviewVO> merchantOrders = new ArrayList<>();

    @Data
    public static class MerchantPreviewVO {
        private Long merchantId;
        private String shopName;
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private BigDecimal freightAmount = BigDecimal.ZERO;
        private BigDecimal payAmount = BigDecimal.ZERO;
        private List<PreviewItemVO> items = new ArrayList<>();
    }

    @Data
    public static class PreviewItemVO {
        private Long cartId;
        private Long goodsId;
        private String goodsName;
        private String imageUrl;
        private BigDecimal price;
        private Integer quantity;
    }
}
