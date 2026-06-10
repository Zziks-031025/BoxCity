package com.boxcity.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.GoodsListVO;
import com.boxcity.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/app/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 搜索商品
     * GET /api/app/search/goods?keyword=&page=1&size=10
     */
    @GetMapping("/goods")
    public Result<IPage<GoodsListVO>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(searchService.search(userId, keyword, page, size));
    }

    /**
     * 获取搜索历史
     * GET /api/app/search/history
     */
    @GetMapping("/history")
    public Result<List<String>> getSearchHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(searchService.getSearchHistory(userId));
    }

    /**
     * 清空搜索历史
     * DELETE /api/app/search/history
     */
    @DeleteMapping("/history")
    public Result<Void> clearSearchHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        searchService.clearSearchHistory(userId);
        return Result.success();
    }

    /**
     * 热门搜索关键词
     * GET /api/app/search/hot
     */
    @GetMapping("/hot")
    public Result<List<String>> getHotKeywords() {
        return Result.success(searchService.getHotKeywords());
    }
}
