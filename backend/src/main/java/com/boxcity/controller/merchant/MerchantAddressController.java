package com.boxcity.controller.merchant;

import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.MerchantAddressDTO;
import com.boxcity.entity.MerchantAddress;
import com.boxcity.service.MerchantAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/merchant/address")
@RequiredArgsConstructor
public class MerchantAddressController {

    private final MerchantAddressService merchantAddressService;

    @GetMapping("/list")
    public Result<List<MerchantAddress>> list(HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(merchantAddressService.getAddressList(merchantId));
    }

    @PostMapping
    public Result<Void> add(HttpServletRequest request,
                            @Validated @RequestBody MerchantAddressDTO dto) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        merchantAddressService.addAddress(merchantId, dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(HttpServletRequest request,
                               @PathVariable Long id,
                               @Validated @RequestBody MerchantAddressDTO dto) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        merchantAddressService.updateAddress(merchantId, id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest request,
                               @PathVariable Long id) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        merchantAddressService.deleteAddress(merchantId, id);
        return Result.success();
    }

    @PutMapping("/{id}/default")
    public Result<Void> setDefault(HttpServletRequest request,
                                   @PathVariable Long id) {
        Long merchantId = (Long) request.getAttribute(Constants.USER_ID);
        merchantAddressService.setDefault(merchantId, id);
        return Result.success();
    }
}
