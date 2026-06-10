package com.boxcity.service;

import com.boxcity.dto.MerchantAddressDTO;
import com.boxcity.entity.MerchantAddress;

import java.util.List;

public interface MerchantAddressService {

    List<MerchantAddress> getAddressList(Long merchantId);

    MerchantAddress getAddressById(Long merchantId, Long addressId);

    void addAddress(Long merchantId, MerchantAddressDTO dto);

    void updateAddress(Long merchantId, Long addressId, MerchantAddressDTO dto);

    void deleteAddress(Long merchantId, Long addressId);

    void setDefault(Long merchantId, Long addressId);
}
