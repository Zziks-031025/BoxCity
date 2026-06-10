package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.boxcity.common.BusinessException;
import com.boxcity.dto.AddressDTO;
import com.boxcity.entity.UserAddress;
import com.boxcity.mapper.UserAddressMapper;
import com.boxcity.service.UserAddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Resource
    private UserAddressMapper userAddressMapper;

    @Override
    public List<UserAddress> getAddressList(Long userId) {
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId)
               .orderByDesc(UserAddress::getIsDefault)
               .orderByDesc(UserAddress::getCreatedAt);
        return userAddressMapper.selectList(wrapper);
    }

    @Override
    public UserAddress getAddressById(Long userId, Long addressId) {
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getId, addressId)
               .eq(UserAddress::getUserId, userId);
        UserAddress address = userAddressMapper.selectOne(wrapper);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        return address;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAddress(Long userId, AddressDTO dto) {
        // 如果设为默认，先重置该用户所有地址为非默认
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            resetDefault(userId);
        }
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setName(dto.getName());
        address.setPhone(dto.getPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetail(dto.getDetail());
        address.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : 0);
        userAddressMapper.insert(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long userId, Long addressId, AddressDTO dto) {
        // 校验地址归属
        UserAddress address = getAddressById(userId, addressId);

        // 如果设为默认，先重置该用户所有地址为非默认
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            resetDefault(userId);
        }

        address.setName(dto.getName());
        address.setPhone(dto.getPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetail(dto.getDetail());
        if (dto.getIsDefault() != null) {
            address.setIsDefault(dto.getIsDefault());
        }
        userAddressMapper.updateById(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        // 校验地址归属
        getAddressById(userId, addressId);
        userAddressMapper.deleteById(addressId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long userId, Long addressId) {
        // 校验地址归属
        getAddressById(userId, addressId);

        // 重置该用户所有地址为非默认
        resetDefault(userId);

        // 设置目标地址为默认
        LambdaUpdateWrapper<UserAddress> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserAddress::getId, addressId)
               .eq(UserAddress::getUserId, userId)
               .set(UserAddress::getIsDefault, 1);
        userAddressMapper.update(null, wrapper);
    }

    /**
     * 重置用户所有地址为非默认
     */
    private void resetDefault(Long userId) {
        LambdaUpdateWrapper<UserAddress> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId)
               .set(UserAddress::getIsDefault, 0);
        userAddressMapper.update(null, wrapper);
    }
}
