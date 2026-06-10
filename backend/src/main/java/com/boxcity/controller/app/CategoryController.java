package com.boxcity.controller.app;

import com.boxcity.common.Result;
import com.boxcity.entity.Category;
import com.boxcity.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 消费者端分类接口，挂载在 /api/common 下，无需 token
 */
@RestController
@RequestMapping("/api/common/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 获取分类树形结构
     */
    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree() {
        return Result.success(categoryService.getCategoryTree());
    }

    /**
     * 按父级ID获取分类列表
     */
    @GetMapping("/list")
    public Result<List<Category>> listByParent(@RequestParam(defaultValue = "0") Long parentId) {
        return Result.success(categoryService.listByParent(parentId));
    }
}
