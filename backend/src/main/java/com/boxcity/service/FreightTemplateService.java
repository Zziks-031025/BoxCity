package com.boxcity.service;

import com.boxcity.dto.FreightTemplateDTO;
import com.boxcity.entity.FreightTemplate;

import java.util.List;

public interface FreightTemplateService {

    /**
     * 查询商家所有运费模板（含rules和excludes）
     */
    List<FreightTemplate> listByMerchant(Long merchantId);

    /**
     * 查询单个模板详情（含rules和excludes），验证归属
     */
    FreightTemplate getDetail(Long merchantId, Long templateId);

    /**
     * 新增运费模板
     */
    void addTemplate(Long merchantId, FreightTemplateDTO dto);

    /**
     * 更新运费模板
     */
    void updateTemplate(Long merchantId, Long templateId, FreightTemplateDTO dto);

    /**
     * 删除运费模板（逻辑删除模板，物理删除rules和excludes）
     */
    void deleteTemplate(Long merchantId, Long templateId);

    /**
     * 设置默认运费模板
     */
    void setDefault(Long merchantId, Long templateId);
}
