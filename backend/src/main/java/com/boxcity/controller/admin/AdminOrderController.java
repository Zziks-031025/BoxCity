package com.boxcity.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.DisputeHandleRequest;
import com.boxcity.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminService adminService;

    @GetMapping("/list")
    public Result<IPage<Map<String, Object>>> list(@RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) Integer status,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminService.getOrderList(keyword, status, page, size));
    }

    @GetMapping("/dispute/list")
    public Result<IPage<Map<String, Object>>> disputeList(@RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminService.getDisputeList(page, size));
    }

    @PutMapping("/dispute/{id}/handle")
    public Result<Void> handleDispute(HttpServletRequest request,
                                      @PathVariable Long id,
                                      @Validated @RequestBody DisputeHandleRequest body) {
        Long adminId = (Long) request.getAttribute(Constants.USER_ID);
        adminService.handleDispute(adminId, id, body);
        return Result.success();
    }

    @GetMapping("/abnormal/list")
    public Result<IPage<Map<String, Object>>> abnormalList(@RequestParam(defaultValue = "1") Integer page,
                                                           @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminService.getAbnormalOrderList(page, size));
    }
}
