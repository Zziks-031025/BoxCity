package com.boxcity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    private String buyerMessage;

    /** 购物车下单时传入购物车 ID 列表 */
    private List<Long> cartIds;

    /** 直接购买时传入商品 ID */
    private Long goodsId;

    /** 直接购买时传入数量 */
    private Integer quantity;
}
