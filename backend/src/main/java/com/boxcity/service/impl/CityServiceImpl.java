package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.boxcity.entity.City;
import com.boxcity.mapper.CityMapper;
import com.boxcity.service.CityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {

    @Resource
    private CityMapper cityMapper;

    @Override
    public List<City> getCityTree() {
        // 一次性查出所有城市数据
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(City::getSort).orderByAsc(City::getId);
        List<City> allCities = cityMapper.selectList(wrapper);

        // 按 parentId 分组
        Map<Long, List<City>> parentMap = allCities.stream()
                .collect(Collectors.groupingBy(
                        city -> city.getParentId() == null ? 0L : city.getParentId()
                ));

        // 取出省级节点（level=1，parentId=0 或 null）
        List<City> provinces = allCities.stream()
                .filter(city -> city.getLevel() != null && city.getLevel() == 1)
                .collect(Collectors.toList());

        // 递归构建树
        for (City province : provinces) {
            province.setChildren(buildChildren(province.getId(), parentMap));
        }

        return provinces;
    }

    @Override
    public List<City> getByParentId(Long parentId) {
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(City::getParentId, parentId)
               .orderByAsc(City::getSort)
               .orderByAsc(City::getId);
        return cityMapper.selectList(wrapper);
    }

    private List<City> buildChildren(Long parentId, Map<Long, List<City>> parentMap) {
        List<City> children = parentMap.getOrDefault(parentId, new ArrayList<>());
        for (City child : children) {
            child.setChildren(buildChildren(child.getId(), parentMap));
        }
        return children;
    }
}
