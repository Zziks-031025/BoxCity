package com.boxcity.service;

import com.boxcity.entity.City;

import java.util.List;

public interface CityService {

    /**
     * 获取完整的省市区三级树结构
     */
    List<City> getCityTree();

    /**
     * 根据父级ID查询下级城市列表
     */
    List<City> getByParentId(Long parentId);
}
