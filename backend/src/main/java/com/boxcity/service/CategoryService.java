package com.boxcity.service;

import com.boxcity.dto.CategoryDTO;
import com.boxcity.entity.Category;

import java.util.List;

public interface CategoryService {

    /**
     * 获取分类树形结构（含子分类）
     */
    List<Category> getCategoryTree();

    /**
     * 按父级ID查询分类列表，按sort排序
     */
    List<Category> listByParent(Long parentId);

    /**
     * 新增分类
     */
    void addCategory(CategoryDTO dto);

    /**
     * 更新分类
     */
    void updateCategory(Long id, CategoryDTO dto);

    /**
     * 删除分类（逻辑删除，同时删除子分类）
     */
    void deleteCategory(Long id);
}
