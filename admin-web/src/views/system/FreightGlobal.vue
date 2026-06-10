<template>
  <div class="page-shell" v-loading="loading">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Global Freight</div>
        <h1 class="hero-title">全局运费配置</h1>
        <p class="hero-subtitle">
          全局运费用于提供平台级默认规则，需要在说明区、配置区和保存区之间形成清晰边界。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">风险提示</div>
        <h3 class="card-title">全局配置会影响默认运费计算</h3>
        <p class="card-desc">建议优先作为兜底规则使用，避免与商家自定义模板产生混淆。</p>
      </div>
    </section>

    <el-card shadow="never">
      <template #header>
        <div>
          <h3 class="section-title">运费规则配置</h3>
          <p class="section-subtitle">三种计费模式已经按语义统一成同一配置结构。</p>
        </div>
      </template>
      <el-form label-position="top" :model="form" class="config-form">
        <div class="form-grid">
          <el-form-item label="计费类型" class="span-2">
            <el-select v-model="form.chargeType">
              <el-option label="全国包邮" :value="0" />
              <el-option label="按件计费" :value="1" />
              <el-option label="按重量计费" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="规则 JSON" class="span-2">
            <el-input
              v-model="form.rules"
              type="textarea"
              :rows="10"
              placeholder='例如: [{"region":"全国","firstUnit":1,"firstFee":8.00}]'
            />
          </el-form-item>
        </div>
        <div class="panel-actions">
          <el-button type="primary" @click="submit">保存配置</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getFreightConfig, updateFreightConfig } from '@/api/admin'

const loading = ref(false)
const form = reactive({
  id: null,
  chargeType: 0,
  rules: '[]'
})

async function loadData() {
  loading.value = true
  try {
    const res = await getFreightConfig()
    Object.assign(form, {
      id: res.id || null,
      chargeType: res.chargeType ?? 0,
      rules: res.rules || '[]'
    })
  } finally {
    loading.value = false
  }
}

async function submit() {
  await updateFreightConfig({ ...form })
  ElMessage.success('全局运费配置已保存')
}

onMounted(loadData)
</script>

<style scoped>
.span-2 {
  grid-column: 1 / -1;
}

.config-form {
  max-width: 880px;
}
</style>
