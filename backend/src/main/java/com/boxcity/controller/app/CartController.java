package com.boxcity.controller.app;

import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.CartVO;
import com.boxcity.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 获取购物车列表
     * GET /api/app/cart
     */
    @GetMapping
    public Result<List<CartVO>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(cartService.getCartList(userId));
    }

    /**
     * 加入购物车
     * POST /api/app/cart  body: {goodsId, quantity}
     */
    @PostMapping
    public Result<Void> add(HttpServletRequest request,
                            @RequestBody Map<String, Object> body) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        Long goodsId = Long.valueOf(body.get("goodsId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        cartService.addToCart(userId, goodsId, quantity);
        return Result.success();
    }

    /**
     * 修改购物车数量
     * PUT /api/app/cart/{id}  body: {quantity}
     */
    @PutMapping("/{id}")
    public Result<Void> update(HttpServletRequest request,
                               @PathVariable Long id,
                               @RequestBody Map<String, Integer> body) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        Integer quantity = body.get("quantity");
        if (quantity == null || quantity < 1) {
            return Result.error("数量不合法");
        }
        cartService.updateQuantity(userId, id, quantity);
        return Result.success();
    }

    /**
     * 删除购物车单条记录
     * DELETE /api/app/cart/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest request,
                               @PathVariable Long id) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        cartService.removeFromCart(userId, id);
        return Result.success();
    }
}
