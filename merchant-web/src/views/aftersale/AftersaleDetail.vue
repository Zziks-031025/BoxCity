<template>
  <div class="page-shell" v-loading="loading">
    <section v-if="detail" class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">After-sales Detail</div>
        <h1 class="hero-title">售后单 {{ detail.id }}</h1>
        <p class="hero-subtitle">
          售后详情按申请信息、物流、退款金额和处理动作拆开展示，减少状态、原因和按钮混在一起造成的阅读压力。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">处理动作</div>
        <h3 class="card-title">优先做出明确结论，避免升级为平台介入</h3>
        <p class="card-desc">
          处理结果会同步给用户端。待处理状态下可以直接同意或拒绝，退货中状态下可确认收货并退款。
        </p>
        <div class="panel-actions">
          <el-button @click="router.back()">返回列表</el-button>
          <template v-if="detail.status === 0">
            <el-button type="success" @click="handle(true)">同意申请</el-button>
            <el-button type="danger" @click="rejectDialog = true">拒绝申请</el-button>
          </template>
          <el-button v-if="detail.status === 2" type="primary" @click="confirmReturn">确认收货并退款</el-button>
        </div>
      </div>
    </section>

    <div v-if="detail" class="detail-grid">
      <el-card shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">申请信息</h3>
            <p class="section-subtitle">优先确认订单、商品、售后类型和当前状态。</p>
          </div>
        </template>
        <div class="data-list">
          <div class="data-row"><strong>售后单号</strong><span>{{ detail.id }}</span></div>
          <div class="data-row"><strong>订单号</strong><span>{{ detail.orderNo }}</span></div>
          <div class="data-row"><strong>商品名称</strong><span>{{ detail.goodsTitle }}</span></div>
          <div class="data-row"><strong>售后类型</strong><span>{{ detail.refundType === 1 ? '仅退款' : '退货退款' }}</span></div>
          <div class="data-row"><strong>当前状态</strong><span>{{ statusLabel(detail.status) }}</span></div>
        </div>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">退款信息</h3>
            <p class="section-subtitle">突出退款金额与申请原因，便于快速判断。</p>
          </div>
        </template>
        <div class="amount-list">
          <div class="amount-row total">
            <strong>退款金额</strong>
            <span>¥ {{ Number(detail.refundAmount || 0).toFixed(2) }}</span>
          </div>
          <div class="data-row text-row">
            <strong>退款原因</strong>
            <span>{{ detail.refundReason || '-' }}</span>
          </div>
          <div class="data-row text-row">
            <strong>拒绝原因</strong>
            <span>{{ detail.rejectReason || '-' }}</span>
          </div>
        </div>
      </el-card>

      <el-card class="span-2" shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">退货物流</h3>
            <p class="section-subtitle">退货场景下可用于核对物流公司与单号。</p>
          </div>
        </template>
        <div class="data-list">
          <div class="data-row"><strong>物流公司</strong><span>{{ detail.logisticsCompany || '-' }}</span></div>
          <div class="data-row"><strong>物流单号</strong><span>{{ detail.logisticsNo || '-' }}</span></div>
        </div>
      </el-card>
    </div>

    <el-dialog v-model="rejectDialog" title="填写拒绝原因" width="420px">
      <el-input v-model="rejectReason" type="textarea" :rows="4" placeholder="请说明拒绝原因，用户端会同步展示这部分内容" />
      <template #footer>
        <el-button @click="rejectDialog = false">取消</el-button>
        <el-button type="primary" @click="handle(false)">提交结果</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { confirmAftersaleReturn, getAftersaleDetail, handleAftersale } from '@/api/merchant'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref(null)
const rejectDialog = ref(false)
const rejectReason = ref('')

function statusLabel(status) {
  return { 0: '待处理', 1: '待退货', 2: '退货中', 3: '退款成功', 4: '商家拒绝', 5: '平台介入中', 6: '已关闭' }[status] || '未知'
}

async function loadDetail() {
  loading.value = true
  try {
    detail.value = await getAftersaleDetail(route.params.id)
  } finally {
    loading.value = false
  }
}

async function handle(agree) {
  await handleAftersale(route.params.id, { agree, rejectReason: agree ? '' : rejectReason.value })
  ElMessage.success('售后结果已更新')
  rejectDialog.value = false
  rejectReason.value = ''
  loadDetail()
}

async function confirmReturn() {
  await confirmAftersaleReturn(route.params.id)
  ElMessage.success('退款已完成')
  loadDetail()
}

onMounted(loadDetail)
</script>

<style scoped>
.page-hero {
  margin-bottom: 24px;
}

.hero-side-card .panel-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.span-2 {
  grid-column: 1 / -1;
}

.text-row span {
  max-width: 420px;
  text-align: right;
}

@media (max-width: 720px) {
  .text-row span {
    max-width: none;
    text-align: left;
  }
}
</style>
