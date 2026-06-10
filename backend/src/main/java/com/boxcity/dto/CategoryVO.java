package com.boxcity.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVO {

    private Long id;
    private Long parentId;
    private String name;
    private Integer sort;
    private List<CategoryVO> children;
}
