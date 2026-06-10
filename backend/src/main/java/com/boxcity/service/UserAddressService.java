package com.boxcity.service;

import com.boxcity.dto.AddressDTO;
import com.boxcity.entity.UserAddress;

import java.util.List;

public interface UserAddressService {

    List<UserAddress> getAddressList(Long userId);

    UserAddress getAddressById(Long userId, Long addressId);

    void addAddress(Long userId, AddressDTO dto);

    void updateAddress(Long userId, Long addressId, AddressDTO dto);

    void deleteAddress(Long userId, Long addressId);

    void setDefault(Long userId, Long addressId);
}
