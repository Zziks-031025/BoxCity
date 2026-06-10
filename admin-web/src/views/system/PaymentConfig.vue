<template>
  <div class="page-shell" v-loading="loading">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Payment Config</div>
        <h1 class="hero-title">支付配置目前仍作为真实微信支付对接预留位</h1>
        <p class="hero-subtitle">
          当前项目仍以模拟支付联调为主，但平台后台已经预留了商户号、密钥、证书和回调地址的配置入口。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">当前状态</div>
        <h3 class="card-title">真实微信支付暂未接入</h3>
        <p class="card-desc">本页用于保存最终对接所需参数，方便后续从模拟支付平滑切换到正式支付。</p>
      </div>
    </section>

    <el-card shadow="never">
      <template #header>
        <div>
          <h3 class="section-title">支付参数配置</h3>
          <p class="section-subtitle">采用“说明区 + 配置区 + 保存区”的统一结构，避免像默认表单堆叠。</p>
        </div>
      </template>

      <el-alert
        title="当前项目仍为模拟支付环境，以下配置仅作为真实微信支付的最终对接入口。"
        type="info"
        :closable="false"
        class="config-alert"
      />

      <el-form label-position="top" :model="form" class="config-form">
        <div class="form-grid">
          <el-form-item label="商户号" class="span-2">
            <el-input v-model="form.mchId" placeholder="请输入微信支付商户号" />
          </el-form-item>
          <el-form-item label="API Key" class="span-2">
            <el-input v-model="form.apiKey" placeholder="请输入 API Key" />
          </el-form-item>
          <el-form-item label="证书路径" class="span-2">
            <el-input v-model="form.certPath" placeholder="请输入证书文件路径" />
          </el-form-item>
          <el-form-item label="回调地址" class="span-2">
            <el-input v-model="form.notifyUrl" placeholder="请输入支付回调地址" />
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
import { getPaymentConfig, updatePaymentConfig } from '@/api/admin'

const loading = ref(false)
const form = reactive({
  id: null,
  mchId: '',
  apiKey: '',
  certPath: '',
  notifyUrl: ''
})

async function loadData() {
  loading.value = true
  try {
    Object.assign(form, await getPaymentConfig())
  } finally {
    loading.value = false
  }
}

async function submit() {
  await updatePaymentConfig({ ...form })
  ElMessage.success('支付配置已保存')
}

onMounted(loadData)
</script>

<style scoped>
.span-2 {
  grid-column: 1 / -1;
}

.config-alert {
  margin-bottom: 20px;
}

.config-form {
  max-width: 880px;
}
</style>
