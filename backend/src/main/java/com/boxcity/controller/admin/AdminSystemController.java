package com.boxcity.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.common.Result;
import com.boxcity.entity.Banner;
import com.boxcity.entity.City;
import com.boxcity.entity.FreightTemplateGlobal;
import com.boxcity.entity.PaymentConfig;
import com.boxcity.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class AdminSystemController {

    private final AdminService adminService;

    @GetMapping("/payment")
    public Result<PaymentConfig> paymentConfig() {
        return Result.success(adminService.getPaymentConfig());
    }

    @PutMapping("/payment")
    public Result<Void> savePaymentConfig(@RequestBody PaymentConfig config) {
        adminService.savePaymentConfig(config);
        return Result.success();
    }

    @GetMapping("/freight")
    public Result<FreightTemplateGlobal> globalFreightConfig() {
        return Result.success(adminService.getGlobalFreightConfig());
    }

    @PutMapping("/freight")
    public Result<Void> saveGlobalFreightConfig(@RequestBody FreightTemplateGlobal config) {
        adminService.saveGlobalFreightConfig(config);
        return Result.success();
    }

    @GetMapping("/banner/list")
    public Result<IPage<Banner>> bannerList(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminService.getBannerList(page, size));
    }

    @PostMapping("/banner")
    public Result<Void> addBanner(@RequestBody Banner banner) {
        adminService.saveBanner(banner);
        return Result.success();
    }

    @PutMapping("/banner/{id}")
    public Result<Void> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        adminService.updateBanner(id, banner);
        return Result.success();
    }

    @DeleteMapping("/banner/{id}")
    public Result<Void> deleteBanner(@PathVariable Long id) {
        adminService.deleteBanner(id);
        return Result.success();
    }

    @GetMapping("/city/list")
    public Result<IPage<City>> cityList(@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Long parentId,
                                        @RequestParam(required = false) Integer level,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(adminService.getCityList(keyword, parentId, level, page, size));
    }

    @PostMapping("/city")
    public Result<Void> addCity(@RequestBody City city) {
        adminService.addCity(city);
        return Result.success();
    }

    @PutMapping("/city/{id}")
    public Result<Void> updateCity(@PathVariable Long id, @RequestBody City city) {
        adminService.updateCity(id, city);
        return Result.success();
    }

    @DeleteMapping("/city/{id}")
    public Result<Void> deleteCity(@PathVariable Long id) {
        adminService.deleteCity(id);
        return Result.success();
    }
}
