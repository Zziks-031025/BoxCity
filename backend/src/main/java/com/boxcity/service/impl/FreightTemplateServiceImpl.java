package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.boxcity.dto.FreightTemplateDTO;
import com.boxcity.entity.FreightTemplate;
import com.boxcity.entity.FreightTemplateExclude;
import com.boxcity.entity.FreightTemplateRule;
import com.boxcity.mapper.FreightTemplateExcludeMapper;
import com.boxcity.mapper.FreightTemplateMapper;
import com.boxcity.mapper.FreightTemplateRuleMapper;
import com.boxcity.service.FreightTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class FreightTemplateServiceImpl implements FreightTemplateService {

    @Resource
    private FreightTemplateMapper freightTemplateMapper;

    @Resource
    private FreightTemplateRuleMapper freightTemplateRuleMapper;

    @Resource
    private FreightTemplateExcludeMapper freightTemplateExcludeMapper;

    @Override
    public List<FreightTemplate> listByMerchant(Long merchantId) {
        LambdaQueryWrapper<FreightTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FreightTemplate::getMerchantId, merchantId)
               .orderByDesc(FreightTemplate::getIsDefault)
               .orderByDesc(FreightTemplate::getCreatedAt);
        List<FreightTemplate> templates = freightTemplateMapper.selectList(wrapper);
        for (FreightTemplate template : templates) {
            fillRulesAndExcludes(template);
        }
        return templates;
    }

    @Override
    public FreightTemplate getDetail(Long merchantId, Long templateId) {
        FreightTemplate template = getAndValidate(merchantId, templateId);
        fillRulesAndExcludes(template);
        return template;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTemplate(Long merchantId, FreightTemplateDTO dto) {
        if (Integer.valueOf(1).equals(dto.getIsDefault())) {
            resetDefault(merchantId);
        }

        FreightTemplate template = new FreightTemplate();
        template.setMerchantId(merchantId);
        template.setName(dto.getName());
        template.setChargeType(dto.getChargeType());
        template.setIsDefault(dto.getIsDefault() == null ? 0 : dto.getIsDefault());
        template.setDeleted(0);
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        freightTemplateMapper.insert(template);

        saveRulesAndExcludes(template.getId(), dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(Long merchantId, Long templateId, FreightTemplateDTO dto) {
        getAndValidate(merchantId, templateId);

        LambdaUpdateWrapper<FreightTemplate> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(FreightTemplate::getId, templateId)
               .set(FreightTemplate::getName, dto.getName())
               .set(FreightTemplate::getChargeType, dto.getChargeType())
               .set(dto.getIsDefault() != null, FreightTemplate::getIsDefault, dto.getIsDefault())
               .set(FreightTemplate::getUpdatedAt, LocalDateTime.now());
        freightTemplateMapper.update(null, wrapper);

        deleteRulesAndExcludes(templateId);
        saveRulesAndExcludes(templateId, dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long merchantId, Long templateId) {
        getAndValidate(merchantId, templateId);
        freightTemplateMapper.deleteById(templateId);
        deleteRulesAndExcludes(templateId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long merchantId, Long templateId) {
        getAndValidate(merchantId, templateId);
        resetDefault(merchantId);

        LambdaUpdateWrapper<FreightTemplate> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(FreightTemplate::getId, templateId)
               .set(FreightTemplate::getIsDefault, 1)
               .set(FreightTemplate::getUpdatedAt, LocalDateTime.now());
        freightTemplateMapper.update(null, wrapper);
    }

    // ---- private helpers ----

    private FreightTemplate getAndValidate(Long merchantId, Long templateId) {
        FreightTemplate template = freightTemplateMapper.selectById(templateId);
        if (template == null || !merchantId.equals(template.getMerchantId())) {
            throw new RuntimeException("运费模板不存在或无权操作");
        }
        return template;
    }

    private void fillRulesAndExcludes(FreightTemplate template) {
        LambdaQueryWrapper<FreightTemplateRule> ruleWrapper = new LambdaQueryWrapper<>();
        ruleWrapper.eq(FreightTemplateRule::getTemplateId, template.getId());
        template.setRules(freightTemplateRuleMapper.selectList(ruleWrapper));

        LambdaQueryWrapper<FreightTemplateExclude> excludeWrapper = new LambdaQueryWrapper<>();
        excludeWrapper.eq(FreightTemplateExclude::getTemplateId, template.getId());
        template.setExcludes(freightTemplateExcludeMapper.selectList(excludeWrapper));
    }

    private void saveRulesAndExcludes(Long templateId, FreightTemplateDTO dto) {
        if (!CollectionUtils.isEmpty(dto.getRules())) {
            for (FreightTemplateRule rule : dto.getRules()) {
                rule.setId(null);
                rule.setTemplateId(templateId);
                freightTemplateRuleMapper.insert(rule);
            }
        }
        if (!CollectionUtils.isEmpty(dto.getExcludes())) {
            for (FreightTemplateExclude exclude : dto.getExcludes()) {
                exclude.setId(null);
                exclude.setTemplateId(templateId);
                freightTemplateExcludeMapper.insert(exclude);
            }
        }
    }

    private void deleteRulesAndExcludes(Long templateId) {
        LambdaQueryWrapper<FreightTemplateRule> ruleWrapper = new LambdaQueryWrapper<>();
        ruleWrapper.eq(FreightTemplateRule::getTemplateId, templateId);
        freightTemplateRuleMapper.delete(ruleWrapper);

        LambdaQueryWrapper<FreightTemplateExclude> excludeWrapper = new LambdaQueryWrapper<>();
        excludeWrapper.eq(FreightTemplateExclude::getTemplateId, templateId);
        freightTemplateExcludeMapper.delete(excludeWrapper);
    }

    private void resetDefault(Long merchantId) {
        LambdaUpdateWrapper<FreightTemplate> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(FreightTemplate::getMerchantId, merchantId)
               .eq(FreightTemplate::getIsDefault, 1)
               .set(FreightTemplate::getIsDefault, 0)
               .set(FreightTemplate::getUpdatedAt, LocalDateTime.now());
        freightTemplateMapper.update(null, wrapper);
    }
}
