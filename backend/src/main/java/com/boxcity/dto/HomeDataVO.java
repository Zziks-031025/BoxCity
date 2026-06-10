package com.boxcity.dto;

import com.boxcity.entity.Banner;
import lombok.Data;

import java.util.List;

@Data
public class HomeDataVO {

    /** 轮播图 */
    private List<Banner> banners;

    /** 同城推荐（按用户城市） */
    private List<GoodsListVO> recommended;

    /** 热门商品（按销量） */
    private List<GoodsListVO> hotGoods;

    /** 分类导航（一级分类，最多8个） */
    private List<CategoryVO> categories;
}
