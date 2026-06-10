package com.boxcity.controller.admin;

import com.boxcity.common.Result;
import com.boxcity.dto.CategoryDTO;
import com.boxcity.entity.Category;
import com.boxcity.service.CategoryService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
public class AdminCategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 获取分类树形结构
     */
    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree() {
        return Result.success(categoryService.getCategoryTree());
    }

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.getCategoryTree());
    }

    /**
     * 新增分类
     */
    @PostMapping
    public Result<Void> addCategory(@Validated @RequestBody CategoryDTO dto) {
        categoryService.addCategory(dto);
        return Result.success();
    }

    @PostMapping("/add")
    public Result<Void> add(@Validated @RequestBody CategoryDTO dto) {
        categoryService.addCategory(dto);
        return Result.success();
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<Void> updateCategory(@PathVariable Long id,
                                       @Validated @RequestBody CategoryDTO dto) {
        categoryService.updateCategory(id, dto);
        return Result.success();
    }

    /**
     * 删除分类（逻辑删除，同时删除子分类）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
