package com.boxcity.controller.app;

import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.AddressDTO;
import com.boxcity.entity.UserAddress;
import com.boxcity.service.UserAddressService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/app/address")
public class AddressController {

    @Resource
    private UserAddressService userAddressService;

    /**
     * 获取当前用户的地址列表
     */
    @GetMapping("/list")
    public Result<List<UserAddress>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(userAddressService.getAddressList(userId));
    }

    /**
     * 新增地址
     */
    @PostMapping
    public Result<Void> add(HttpServletRequest request,
                            @Validated @RequestBody AddressDTO dto) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        userAddressService.addAddress(userId, dto);
        return Result.success();
    }

    /**
     * 修改地址
     */
    @PutMapping("/{id}")
    public Result<Void> update(HttpServletRequest request,
                               @PathVariable Long id,
                               @Validated @RequestBody AddressDTO dto) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        userAddressService.updateAddress(userId, id, dto);
        return Result.success();
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest request,
                               @PathVariable Long id) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        userAddressService.deleteAddress(userId, id);
        return Result.success();
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/{id}/default")
    public Result<Void> setDefault(HttpServletRequest request,
                                   @PathVariable Long id) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        userAddressService.setDefault(userId, id);
        return Result.success();
    }
}
