package com.boxcity.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.common.Result;
import com.boxcity.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminService adminService;

    @GetMapping("/list")
    public Result<IPage<Map<String, Object>>> list(@RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) Integer status,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminService.getUserList(keyword, status, page, size));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        return Result.success(adminService.getUserDetail(id));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        adminService.updateUserStatus(id, body.get("status"));
        return Result.success();
    }
}
