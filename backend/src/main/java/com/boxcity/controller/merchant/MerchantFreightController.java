package com.boxcity.controller.merchant;

import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.FreightTemplateDTO;
import com.boxcity.entity.FreightTemplate;
import com.boxcity.service.FreightTemplateService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/merchant/freight")
public class MerchantFreightController {

    @Resource
    private FreightTemplateService freightTemplateService;

    /**
     * 查询商家所有运费模板
     */
    @GetMapping("/list")
    public Result<List<FreightTemplate>> list(HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(freightTemplateService.listByMerchant(merchantId));
    }

    /**
     * 查询单个运费模板详情
     */
    @GetMapping("/{id}")
    public Result<FreightTemplate> getDetail(@PathVariable Long id,
                                             HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(freightTemplateService.getDetail(merchantId, id));
    }

    /**
     * 新增运费模板
     */
    @PostMapping
    public Result<Void> add(@Validated @RequestBody FreightTemplateDTO dto,
                            HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        freightTemplateService.addTemplate(merchantId, dto);
        return Result.success();
    }

    /**
     * 更新运费模板
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @Validated @RequestBody FreightTemplateDTO dto,
                               HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        freightTemplateService.updateTemplate(merchantId, id, dto);
        return Result.success();
    }

    /**
     * 删除运费模板
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        freightTemplateService.deleteTemplate(merchantId, id);
        return Result.success();
    }

    /**
     * 设置默认运费模板
     */
    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable Long id,
                                   HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        freightTemplateService.setDefault(merchantId, id);
        return Result.success();
    }
}
