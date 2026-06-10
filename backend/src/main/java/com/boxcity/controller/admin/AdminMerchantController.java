package com.boxcity.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.common.Result;
import com.boxcity.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/merchant")
@RequiredArgsConstructor
public class AdminMerchantController {

    private final AdminService adminService;

    @GetMapping("/audit/list")
    public Result<IPage<Map<String, Object>>> auditList(@RequestParam(required = false) Integer auditStatus,
                                                        @RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminService.getMerchantAuditList(auditStatus, page, size));
    }

    @GetMapping("/audit/{id}")
    public Result<Map<String, Object>> auditDetail(@PathVariable Long id) {
        return Result.success(adminService.getMerchantDetail(id));
    }

    @PutMapping("/audit/{id}")
    public Result<Void> audit(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Boolean approved = body.get("approved") == null ? null : Boolean.valueOf(body.get("approved").toString());
        adminService.auditMerchant(id, Boolean.TRUE.equals(approved), body.get("remark") == null ? null : body.get("remark").toString());
        return Result.success();
    }

    @GetMapping("/list")
    public Result<IPage<Map<String, Object>>> list(@RequestParam(required = false) Integer auditStatus,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminService.getMerchantList(auditStatus, page, size));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer status = body.get("status") == null ? null : Integer.valueOf(body.get("status").toString());
        String remark = body.get("remark") == null ? null : body.get("remark").toString();
        adminService.updateMerchantStatus(id, status, remark);
        return Result.success();
    }
}
