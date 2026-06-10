package com.boxcity.controller.admin;

import com.boxcity.common.Result;
import com.boxcity.dto.AdminLoginRequest;
import com.boxcity.dto.AdminLoginResponse;
import com.boxcity.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminService adminService;

    @PostMapping("/login")
    public Result<AdminLoginResponse> login(@Validated @RequestBody AdminLoginRequest request) {
        return Result.success(adminService.login(request));
    }
}
