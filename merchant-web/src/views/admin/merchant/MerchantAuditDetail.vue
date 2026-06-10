<template>
  <div class="page-shell" v-loading="loading">
    <section v-if="detail" class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Merchant Review Detail</div>
        <h1 class="hero-title">{{ detail.shopName }}</h1>
        <p class="hero-subtitle">
          将基础资料、经营信息和审核动作拆成更清楚的层次，便于平台快速做出审核决策。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">审核动作</div>
        <h3 class="card-title">审核结论会直接影响商家后台是否可登录</h3>
        <p class="card-desc">待审核状态下可以直接通过或驳回，并要求填写备注，方便后续追踪。</p>
        <div class="panel-actions">
          <el-button @click="router.back()">返回</el-button>
          <el-button v-if="detail?.auditStatus === 0" type="success" @click="submitAudit(true)">审核通过</el-button>
          <el-button v-if="detail?.auditStatus === 0" type="danger" @click="submitAudit(false)">驳回申请</el-button>
        </div>
      </div>
    </section>

    <div v-if="detail" class="detail-grid">
      <el-card shadow="never">
        <template #header><h3 class="section-title">基础资料</h3></template>
        <div class="data-list">
          <div class="data-row"><strong>店铺名称</strong><span>{{ detail.shopName }}</span></div>
          <div class="data-row"><strong>联系电话</strong><span>{{ detail.contactPhone || '-' }}</span></div>
          <div class="data-row"><strong>联系邮箱</strong><span>{{ detail.contactEmail || '-' }}</span></div>
          <div class="data-row"><strong>主体类型</strong><span>{{ detail.subjectType === 2 ? '企业' : '个人' }}</span></div>
        </div>
      </el-card>

      <el-card shadow="never">
        <template #header><h3 class="section-title">经营数据</h3></template>
        <div class="data-list">
          <div class="data-row"><strong>审核状态</strong><span>{{ auditStatusText }}</span></div>
          <div class="data-row"><strong>信用分</strong><span>{{ detail.creditScore || 0 }}</span></div>
          <div class="data-row"><strong>商品数</strong><span>{{ detail.goodsCount || 0 }}</span></div>
          <div class="data-row"><strong>订单数</strong><span>{{ detail.orderCount || 0 }}</span></div>
        </div>
      </el-card>

      <el-card class="span-2" shadow="never">
        <template #header><h3 class="section-title">审核记录</h3></template>
        <div class="data-list">
          <div class="data-row"><strong>审核备注</strong><span>{{ detail.auditRemark || '-' }}</span></div>
          <div class="data-row"><strong>申请时间</strong><span>{{ detail.createdAt || '-' }}</span></div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { auditMerchant, getMerchantAuditDetail } from '@/api/admin'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref(null)

const auditStatusText = computed(() =>
  detail.value?.auditStatus === 1 ? '已通过' : detail.value?.auditStatus === 2 ? '已驳回' : '待审核'
)

async function loadData() {
  loading.value = true
  try {
    detail.value = await getMerchantAuditDetail(route.params.id)
  } finally {
    loading.value = false
  }
}

async function submitAudit(approved) {
  const { value } = await ElMessageBox.prompt(`请输入${approved ? '通过' : '驳回'}备注`, '审核操作', {
    confirmButtonText: '提交',
    cancelButtonText: '取消',
    inputValue: detail.value?.auditRemark || ''
  })
  await auditMerchant(route.params.id, { approved, remark: value || '' })
  ElMessage.success('审核结果已提交')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.span-2 {
  grid-column: 1 / -1;
}
</style>
