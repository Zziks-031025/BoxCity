package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.boxcity.dto.CategoryDTO;
import com.boxcity.entity.Category;
import com.boxcity.mapper.CategoryMapper;
import com.boxcity.service.CategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> getCategoryTree() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, 1)
               .orderByAsc(Category::getSort)
               .orderByAsc(Category::getId);
        List<Category> all = categoryMapper.selectList(wrapper);

        Map<Long, List<Category>> parentMap = all.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getParentId() == null ? 0L : c.getParentId()
                ));

        List<Category> roots = all.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0L)
                .collect(Collectors.toList());

        for (Category root : roots) {
            root.setChildren(buildChildren(root.getId(), parentMap));
        }

        return roots;
    }

    @Override
    public List<Category> listByParent(Long parentId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, parentId)
               .orderByAsc(Category::getSort)
               .orderByAsc(Category::getId);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public void addCategory(CategoryDTO dto) {
        Category category = new Category();
        category.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        category.setName(dto.getName());
        category.setSort(dto.getSort() == null ? 0 : dto.getSort());
        category.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        category.setDeleted(0);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(Long id, CategoryDTO dto) {
        LambdaUpdateWrapper<Category> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Category::getId, id)
               .set(Category::getName, dto.getName())
               .set(Category::getSort, dto.getSort() == null ? 0 : dto.getSort())
               .set(Category::getStatus, dto.getStatus() == null ? 1 : dto.getStatus())
               .set(dto.getParentId() != null, Category::getParentId, dto.getParentId())
               .set(Category::getUpdatedAt, LocalDateTime.now());
        categoryMapper.update(null, wrapper);
    }

    @Override
    public void deleteCategory(Long id) {
        // 逻辑删除当前分类
        categoryMapper.deleteById(id);

        // 逻辑删除子分类
        LambdaQueryWrapper<Category> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Category::getParentId, id);
        List<Category> children = categoryMapper.selectList(childWrapper);
        for (Category child : children) {
            categoryMapper.deleteById(child.getId());
        }
    }

    private List<Category> buildChildren(Long parentId, Map<Long, List<Category>> parentMap) {
        List<Category> children = parentMap.getOrDefault(parentId, new ArrayList<>());
        for (Category child : children) {
            child.setChildren(buildChildren(child.getId(), parentMap));
        }
        return children;
    }
}
