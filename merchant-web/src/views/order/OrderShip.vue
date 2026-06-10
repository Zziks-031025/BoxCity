<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Shipping Action</div>
        <h1 class="hero-title">为订单 {{ route.params.orderNo }} 填写发货信息</h1>
        <p class="hero-subtitle">
          发货页强调默认发货地址、物流信息填写区和确认动作，避免关键字段散在页面各处影响操作效率。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">发货说明</div>
        <h3 class="card-title">物流信息会同步到用户订单详情</h3>
        <p class="card-desc">
          建议发货前核对默认地址、物流公司和单号，避免用户看到错误物流导致售后风险。
        </p>
      </div>
    </section>

    <div class="split-grid">
      <el-card shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">物流填写</h3>
            <p class="section-subtitle">核心动作固定在一张卡内，输入区域不会因为窗口变化覆盖按钮。</p>
          </div>
        </template>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <div class="form-grid">
            <el-form-item label="订单号" class="span-2">
              <el-input :model-value="route.params.orderNo" disabled />
            </el-form-item>
            <el-form-item label="物流公司" prop="logisticsCompany" class="span-2">
              <el-input v-model="form.logisticsCompany" placeholder="例如：顺丰速运、京东物流" />
            </el-form-item>
            <el-form-item label="物流单号" prop="logisticsNo" class="span-2">
              <el-input v-model="form.logisticsNo" placeholder="请输入物流单号" />
            </el-form-item>
          </div>
          <div class="panel-actions">
            <el-button @click="router.back()">取消</el-button>
            <el-button type="primary" :loading="saving" @click="submit">确认发货</el-button>
          </div>
        </el-form>
      </el-card>

      <div class="preview-stack">
        <el-card shadow="never">
          <template #header>
            <div>
              <h3 class="section-title">默认发货地址</h3>
              <p class="section-subtitle">当前商家后台维护的默认地址会用于发货参考。</p>
            </div>
          </template>
          <div class="data-list">
            <div class="data-row"><strong>地址状态</strong><span>{{ defaultAddress ? '已配置' : '待补充' }}</span></div>
            <div class="data-row address-row">
              <strong>发货地址</strong>
              <span>
                {{
                  defaultAddress
                    ? `${defaultAddress.province}${defaultAddress.city}${defaultAddress.district}${defaultAddress.detail}`
                    : '请先到店铺设置中维护发货地址'
                }}
              </span>
            </div>
            <div class="data-row"><strong>邮编</strong><span>{{ defaultAddress?.zipcode || '-' }}</span></div>
          </div>
        </el-card>

        <el-card shadow="never">
          <template #header>
            <div>
              <h3 class="section-title">填写建议</h3>
              <p class="section-subtitle">帮助减少因物流信息缺失导致的用户咨询与售后申请。</p>
            </div>
          </template>
          <ul class="card-list">
            <li>物流公司建议填写规范全称，方便用户识别。</li>
            <li>发货后小程序订单详情页会展示单号和物流公司。</li>
            <li>如默认地址未配置，建议先补齐再继续发货。</li>
          </ul>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAddressList, shipOrder } from '@/api/merchant'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const saving = ref(false)
const defaultAddress = ref(null)
const form = reactive({
  logisticsCompany: '',
  logisticsNo: ''
})

const rules = {
  logisticsCompany: [{ required: true, message: '请输入物流公司', trigger: 'blur' }],
  logisticsNo: [{ required: true, message: '请输入物流单号', trigger: 'blur' }]
}

async function loadAddress() {
  const list = await getAddressList()
  defaultAddress.value = list.find((item) => item.isDefault === 1) || list[0] || null
}

async function submit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await shipOrder(route.params.orderNo, form)
    ElMessage.success('发货成功')
    router.push('/order/list')
  } finally {
    saving.value = false
  }
}

onMounted(loadAddress)
</script>

<style scoped>
.span-2 {
  grid-column: 1 / -1;
}

.address-row span {
  max-width: 320px;
  text-align: right;
}

@media (max-width: 720px) {
  .address-row span {
    max-width: none;
    text-align: left;
  }
}
</style>
