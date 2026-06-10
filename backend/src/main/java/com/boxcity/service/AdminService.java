package com.boxcity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.boxcity.dto.*;
import com.boxcity.entity.Banner;
import com.boxcity.entity.City;
import com.boxcity.entity.FreightTemplateGlobal;
import com.boxcity.entity.PaymentConfig;

import java.util.Map;

public interface AdminService {

    AdminLoginResponse login(AdminLoginRequest request);

    AdminDashboardVO getDashboard();

    IPage<Map<String, Object>> getUserList(String keyword, Integer status, Integer page, Integer size);

    Map<String, Object> getUserDetail(Long userId);

    void updateUserStatus(Long userId, Integer status);

    IPage<Map<String, Object>> getReportList(Integer status, Integer page, Integer size);

    void handleReport(Long adminId, Long reportId, ReportHandleRequest request);

    IPage<Map<String, Object>> getMerchantAuditList(Integer auditStatus, Integer page, Integer size);

    Map<String, Object> getMerchantDetail(Long merchantId);

    void auditMerchant(Long merchantId, boolean approved, String remark);

    IPage<Map<String, Object>> getMerchantList(Integer auditStatus, Integer page, Integer size);

    void updateMerchantStatus(Long merchantId, Integer status, String remark);

    IPage<Map<String, Object>> getGoodsAuditList(Integer auditStatus, Integer page, Integer size);

    Map<String, Object> getGoodsDetail(Long goodsId);

    void auditGoods(Long goodsId, boolean approved, String remark);

    IPage<Map<String, Object>> getOrderList(String keyword, Integer status, Integer page, Integer size);

    IPage<Map<String, Object>> getDisputeList(Integer page, Integer size);

    void handleDispute(Long adminId, Long refundId, DisputeHandleRequest request);

    IPage<Map<String, Object>> getAbnormalOrderList(Integer page, Integer size);

    PaymentConfig getPaymentConfig();

    void savePaymentConfig(PaymentConfig config);

    FreightTemplateGlobal getGlobalFreightConfig();

    void saveGlobalFreightConfig(FreightTemplateGlobal config);

    IPage<Banner> getBannerList(Integer page, Integer size);

    void saveBanner(Banner banner);

    void updateBanner(Long id, Banner banner);

    void deleteBanner(Long id);

    IPage<City> getCityList(String keyword, Long parentId, Integer level, Integer page, Integer size);

    void addCity(City city);

    void updateCity(Long id, City city);

    void deleteCity(Long id);
}
