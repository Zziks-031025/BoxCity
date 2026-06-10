<template>
  <div class="page-shell">
    <section class="dashboard-heading">
      <div>
        <h1 class="dashboard-title">平台概览</h1>
        <p class="dashboard-subtitle">集中查看平台经营、审核和风险处理情况，首页结构统一为传统后台的信息面板布局。</p>
      </div>
    </section>

    <section class="overview-grid">
      <div v-for="card in cards" :key="card.label" class="overview-item">
        <div class="overview-label">{{ card.label }}</div>
        <div class="overview-value">{{ card.value }}</div>
        <div class="overview-note">{{ card.note }}</div>
      </div>
    </section>

    <section class="content-grid">
      <el-card shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">待处理事项</h3>
            <p class="section-subtitle">平台需要优先关注的审核和治理工作。</p>
          </div>
        </template>
        <div class="data-list">
          <div class="data-row">
            <strong>待审商家</strong>
            <span>{{ dashboard.pendingMerchantCount || 0 }}</span>
          </div>
          <div class="data-row">
            <strong>待审商品</strong>
            <span>{{ dashboard.pendingGoodsCount || 0 }}</span>
          </div>
          <div class="data-row">
            <strong>平台纠纷</strong>
            <span>{{ dashboard.disputeCount || 0 }}</span>
          </div>
        </div>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">平台概况</h3>
            <p class="section-subtitle">保留核心数据内容，压缩视觉装饰，增强可读性。</p>
          </div>
        </template>
        <div class="data-list">
          <div class="data-row">
            <strong>今日销售额</strong>
            <span>¥ {{ money(dashboard.todaySales) }}</span>
          </div>
          <div class="data-row">
            <strong>订单总量</strong>
            <span>{{ dashboard.orderCount || 0 }}</span>
          </div>
          <div class="data-row">
            <strong>在售商品数</strong>
            <span>{{ dashboard.goodsCount || 0 }}</span>
          </div>
          <div class="data-row">
            <strong>已通过商家数</strong>
            <span>{{ dashboard.merchantCount || 0 }}</span>
          </div>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getDashboard } from '@/api/admin'

const dashboard = ref({})

const cards = computed(() => [
  {
    label: '用户总量',
    value: dashboard.value.userCount || 0,
    note: '累计注册用户，用于判断平台活跃基数。'
  },
  {
    label: '商家总量',
    value: dashboard.value.merchantCount || 0,
    note: '已审核通过商家，代表供给侧规模。'
  },
  {
    label: '商品总量',
    value: dashboard.value.goodsCount || 0,
    note: '已审核通过商品，反映平台可售库存规模。'
  },
  {
    label: '订单总量',
    value: dashboard.value.orderCount || 0,
    note: '平台历史订单总量，用于长期趋势判断。'
  }
])

function money(value) {
  return Number(value || 0).toFixed(2)
}

onMounted(async () => {
  dashboard.value = await getDashboard()
})
</script>

<style scoped>
.dashboard-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.dashboard-title {
  margin: 0;
  font-size: 24px;
  line-height: 1.3;
  color: var(--text-primary);
}

.dashboard-subtitle {
  margin: 8px 0 0;
  color: var(--text-secondary);
  line-height: 1.8;
  font-size: 14px;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.overview-item {
  padding: 16px;
  border: 1px solid var(--border-color);
  background: var(--bg-panel);
}

.overview-label {
  color: var(--text-secondary);
  font-size: 13px;
}

.overview-value {
  margin-top: 8px;
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
}

.overview-note {
  margin-top: 10px;
  color: var(--text-secondary);
  line-height: 1.7;
  font-size: 13px;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

@media (max-width: 1080px) {
  .overview-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 768px) {
  .overview-grid,
  .content-grid {
    grid-template-columns: 1fr;
  }
}
</style>
