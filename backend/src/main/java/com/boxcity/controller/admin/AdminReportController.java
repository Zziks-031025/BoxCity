package com.boxcity.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.ReportHandleRequest;
import com.boxcity.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/report")
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminService adminService;

    @GetMapping("/list")
    public Result<IPage<Map<String, Object>>> list(@RequestParam(required = false) Integer status,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminService.getReportList(status, page, size));
    }

    @PutMapping("/{id}/handle")
    public Result<Void> handle(HttpServletRequest request,
                               @PathVariable Long id,
                               @Validated @RequestBody ReportHandleRequest body) {
        Long adminId = (Long) request.getAttribute(Constants.USER_ID);
        adminService.handleReport(adminId, id, body);
        return Result.success();
    }
}
