package com.boxcity.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.boxcity.common.BusinessException;
import com.boxcity.common.Constants;
import com.boxcity.dto.MerchantApplyRequest;
import com.boxcity.dto.MerchantInfoVO;
import com.boxcity.dto.MerchantLoginRequest;
import com.boxcity.dto.MerchantLoginResponse;
import com.boxcity.entity.Merchant;
import com.boxcity.mapper.MerchantMapper;
import com.boxcity.service.MerchantService;
import com.boxcity.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantMapper merchantMapper;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void apply(MerchantApplyRequest request) {
        // 检查手机号是否已申请
        Merchant existing = merchantMapper.selectOne(
                new LambdaQueryWrapper<Merchant>()
                        .eq(Merchant::getContactPhone, request.getContactPhone())
        );
        if (existing != null) {
            throw new BusinessException("该手机号已提交过入驻申请");
        }

        Merchant merchant = new Merchant();
        merchant.setShopName(request.getShopName());
        merchant.setContactPhone(request.getContactPhone());
        merchant.setContactEmail(request.getContactEmail());
        merchant.setSubjectType(request.getSubjectType());
        merchant.setLicenseUrl(request.getLicenseUrl());
        merchant.setIdCardFront(request.getIdCardFront());
        merchant.setIdCardBack(request.getIdCardBack());
        merchant.setLegalPersonIdFront(request.getLegalPersonIdFront());
        merchant.setLegalPersonIdBack(request.getLegalPersonIdBack());
        merchant.setShopIntro(request.getShopIntro());
        merchant.setPassword(BCrypt.hashpw(request.getPassword()));
        merchant.setAuditStatus(0);
        merchant.setCreditScore(100);
        merchant.setDeleted(0);
        merchant.setCreatedAt(LocalDateTime.now());
        merchant.setUpdatedAt(LocalDateTime.now());

        merchantMapper.insert(merchant);
        log.info("商家入驻申请提交成功: shopName={}, phone={}", merchant.getShopName(), merchant.getContactPhone());
    }

    @Override
    public MerchantLoginResponse login(MerchantLoginRequest request) {
        Merchant merchant = merchantMapper.selectOne(
                new LambdaQueryWrapper<Merchant>()
                        .eq(Merchant::getContactPhone, request.getContactPhone())
        );
        if (merchant == null) {
            throw new BusinessException("手机号或密码错误");
        }

        if (!BCrypt.checkpw(request.getPassword(), merchant.getPassword())) {
            throw new BusinessException("手机号或密码错误");
        }

        Integer auditStatus = merchant.getAuditStatus();
        if (auditStatus == null || auditStatus != 1) {
            String msg;
            if (auditStatus == null || auditStatus == 0) {
                msg = "账号审核中，请耐心等待";
            } else if (auditStatus == 2) {
                msg = "入驻申请未通过，原因：" + (merchant.getAuditRemark() != null ? merchant.getAuditRemark() : "请联系客服");
            } else {
                msg = "账号已被冻结，请联系客服";
            }
            throw new BusinessException(403, msg);
        }

        String token = jwtUtil.generateToken(merchant.getId(), Constants.USER_TYPE_MERCHANT);

        MerchantLoginResponse response = new MerchantLoginResponse();
        response.setToken(token);
        response.setMerchantId(merchant.getId());
        response.setShopName(merchant.getShopName());
        response.setAuditStatus(auditStatus);

        log.info("商家登录成功: merchantId={}, shopName={}", merchant.getId(), merchant.getShopName());
        return response;
    }

    @Override
    public MerchantInfoVO getInfo(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商家不存在");
        }

        MerchantInfoVO vo = new MerchantInfoVO();
        vo.setId(merchant.getId());
        vo.setShopName(merchant.getShopName());
        vo.setShopAvatar(merchant.getShopAvatar());
        vo.setShopIntro(merchant.getShopIntro());
        vo.setShopNotice(merchant.getShopNotice());
        vo.setContactPhone(merchant.getContactPhone());
        vo.setContactEmail(merchant.getContactEmail());
        vo.setSubjectType(merchant.getSubjectType());
        vo.setAuditStatus(merchant.getAuditStatus());
        vo.setAuditRemark(merchant.getAuditRemark());
        vo.setCreditScore(merchant.getCreditScore());
        vo.setCreatedAt(merchant.getCreatedAt());
        return vo;
    }

    @Override
    @Transactional
    public void updateInfo(Long merchantId, MerchantInfoVO vo) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商家不存在");
        }

        if (vo.getShopName() != null) {
            merchant.setShopName(vo.getShopName());
        }
        if (vo.getShopAvatar() != null) {
            merchant.setShopAvatar(vo.getShopAvatar());
        }
        if (vo.getShopIntro() != null) {
            merchant.setShopIntro(vo.getShopIntro());
        }
        if (vo.getShopNotice() != null) {
            merchant.setShopNotice(vo.getShopNotice());
        }
        if (vo.getContactEmail() != null) {
            merchant.setContactEmail(vo.getContactEmail());
        }

        merchant.setUpdatedAt(LocalDateTime.now());
        merchantMapper.updateById(merchant);

        log.info("商家更新店铺信息: merchantId={}", merchantId);
    }
}
