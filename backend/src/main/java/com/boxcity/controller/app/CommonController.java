package com.boxcity.controller.app;

import com.boxcity.common.Result;
import com.boxcity.entity.City;
import com.boxcity.service.CityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/common")
public class CommonController {

    @Resource
    private CityService cityService;

    /**
     * 获取省市区三级树结构
     */
    @GetMapping("/city/tree")
    public Result<List<City>> getCityTree() {
        return Result.success(cityService.getCityTree());
    }

    /**
     * 根据父级ID获取下级城市列表
     */
    @GetMapping("/city/children")
    public Result<List<City>> getCityChildren(@RequestParam Long parentId) {
        return Result.success(cityService.getByParentId(parentId));
    }
}
