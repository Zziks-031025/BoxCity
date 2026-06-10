package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.boxcity.common.BusinessException;
import com.boxcity.dto.MerchantAddressDTO;
import com.boxcity.entity.MerchantAddress;
import com.boxcity.mapper.MerchantAddressMapper;
import com.boxcity.service.MerchantAddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MerchantAddressServiceImpl implements MerchantAddressService {

    @Resource
    private MerchantAddressMapper merchantAddressMapper;

    @Override
    public List<MerchantAddress> getAddressList(Long merchantId) {
        return merchantAddressMapper.selectList(
                new LambdaQueryWrapper<MerchantAddress>()
                        .eq(MerchantAddress::getMerchantId, merchantId)
                        .orderByDesc(MerchantAddress::getIsDefault)
                        .orderByDesc(MerchantAddress::getCreatedAt));
    }

    @Override
    public MerchantAddress getAddressById(Long merchantId, Long addressId) {
        MerchantAddress address = merchantAddressMapper.selectOne(
                new LambdaQueryWrapper<MerchantAddress>()
                        .eq(MerchantAddress::getId, addressId)
                        .eq(MerchantAddress::getMerchantId, merchantId));
        if (address == null) {
            throw new BusinessException("发货地址不存在");
        }
        return address;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAddress(Long merchantId, MerchantAddressDTO dto) {
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            resetDefault(merchantId);
        }

        MerchantAddress address = new MerchantAddress();
        address.setMerchantId(merchantId);
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetail(dto.getDetail());
        address.setZipcode(dto.getZipcode());
        address.setIsDefault(Boolean.TRUE.equals(dto.getIsDefault()) ? 1 : 0);
        merchantAddressMapper.insert(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long merchantId, Long addressId, MerchantAddressDTO dto) {
        MerchantAddress address = getAddressById(merchantId, addressId);
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            resetDefault(merchantId);
        }

        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetail(dto.getDetail());
        address.setZipcode(dto.getZipcode());
        if (dto.getIsDefault() != null) {
            address.setIsDefault(Boolean.TRUE.equals(dto.getIsDefault()) ? 1 : 0);
        }
        merchantAddressMapper.updateById(address);
    }

    @Override
    public void deleteAddress(Long merchantId, Long addressId) {
        getAddressById(merchantId, addressId);
        merchantAddressMapper.deleteById(addressId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long merchantId, Long addressId) {
        getAddressById(merchantId, addressId);
        resetDefault(merchantId);
        merchantAddressMapper.update(
                null,
                new LambdaUpdateWrapper<MerchantAddress>()
                        .eq(MerchantAddress::getId, addressId)
                        .eq(MerchantAddress::getMerchantId, merchantId)
                        .set(MerchantAddress::getIsDefault, 1));
    }

    private void resetDefault(Long merchantId) {
        merchantAddressMapper.update(
                null,
                new LambdaUpdateWrapper<MerchantAddress>()
                        .eq(MerchantAddress::getMerchantId, merchantId)
                        .set(MerchantAddress::getIsDefault, 0));
    }
}
