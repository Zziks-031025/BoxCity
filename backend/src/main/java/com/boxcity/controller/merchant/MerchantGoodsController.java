package com.boxcity.controller.merchant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.GoodsListVO;
import com.boxcity.dto.GoodsPublishDTO;
import com.boxcity.dto.GoodsVO;
import com.boxcity.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/goods")
@RequiredArgsConstructor
public class MerchantGoodsController {

    private final GoodsService goodsService;

    /**
     * 商家商品列表
     * GET /api/merchant/goods/list?auditStatus=&status=&page=1&size=10
     */
    @GetMapping("/list")
    public Result<IPage<GoodsListVO>> list(
            HttpServletRequest request,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(goodsService.merchantGoodsList(merchantId, auditStatus, status, page, size));
    }

    @GetMapping("/{id}")
    public Result<GoodsVO> detail(HttpServletRequest request,
                                  @PathVariable Long id) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(goodsService.getMerchantGoodsDetail(merchantId, id));
    }

    /**
     * 发布商品
     * POST /api/merchant/goods
     */
    @PostMapping
    public Result<Void> publish(HttpServletRequest request,
                                @Validated @RequestBody GoodsPublishDTO dto) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        goodsService.publishGoods(merchantId, dto);
        return Result.success();
    }

    /**
     * 编辑商品
     * PUT /api/merchant/goods/{id}
     */
    @PutMapping("/{id}")
    public Result<Void> update(HttpServletRequest request,
                               @PathVariable Long id,
                               @Validated @RequestBody GoodsPublishDTO dto) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        goodsService.updateGoods(merchantId, id, dto);
        return Result.success();
    }

    /**
     * 删除商品
     * DELETE /api/merchant/goods/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest request,
                               @PathVariable Long id) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        goodsService.deleteGoods(merchantId, id);
        return Result.success();
    }

    /**
     * 上架/下架
     * PUT /api/merchant/goods/{id}/status  body: {"status": 1}
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(HttpServletRequest request,
                                     @PathVariable Long id,
                                     @RequestBody Map<String, Integer> body) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        Integer status = body.get("status");
        if (status == null) {
            return Result.error("status 不能为空");
        }
        goodsService.updateStatus(merchantId, id, status);
        return Result.success();
    }

    @PutMapping("/{id}/stock")
    public Result<Void> updateStock(HttpServletRequest request,
                                    @PathVariable Long id,
                                    @RequestBody Map<String, Integer> body) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        Integer stock = body.get("stock");
        if (stock == null || stock < 0) {
            return Result.error("stock 不能为空且不能小于 0");
        }
        goodsService.updateStock(merchantId, id, stock);
        return Result.success();
    }
}
