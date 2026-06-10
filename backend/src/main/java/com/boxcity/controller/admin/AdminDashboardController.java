package com.boxcity.controller.admin;

import com.boxcity.common.Result;
import com.boxcity.dto.AdminDashboardVO;
import com.boxcity.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminService adminService;

    @GetMapping
    public Result<AdminDashboardVO> dashboard() {
        return Result.success(adminService.getDashboard());
    }
}
