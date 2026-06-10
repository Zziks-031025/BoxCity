<template>
  <div class="page-shell">
    <section class="dashboard-heading">
      <div>
        <h1 class="dashboard-title">{{ shopName }} 经营概览</h1>
        <p class="dashboard-subtitle">汇总今日订单、销售趋势与关键经营数据，保留原有数据内容但使用更传统的后台结构展示。</p>
      </div>
    </section>

    <section class="overview-grid">
      <div v-for="card in cards" :key="card.label" class="overview-item">
        <div class="overview-label">{{ card.label }}</div>
        <div class="overview-value">{{ card.value }}</div>
        <div class="overview-note">{{ card.note }}</div>
      </div>
    </section>

    <section class="main-grid">
      <el-card shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">近 7 天销售趋势</h3>
            <p class="section-subtitle">订单量与销售额保持同图展示，方便快速判断波动情况。</p>
          </div>
        </template>
        <div ref="chartRef" class="chart"></div>
      </el-card>

      <div class="side-stack">
        <el-card shadow="never">
          <template #header>
            <div>
              <h3 class="section-title">今日重点</h3>
              <p class="section-subtitle">帮助商家快速判断今天需要优先处理的工作。</p>
            </div>
          </template>
          <div class="data-list">
            <div class="data-row">
              <strong>今日订单</strong>
              <span>{{ dashboard.todayOrderCount || 0 }}</span>
            </div>
            <div class="data-row">
              <strong>今日销售额</strong>
              <span>¥ {{ money(dashboard.todaySalesAmount) }}</span>
            </div>
            <div class="data-row">
              <strong>7 天订单总量</strong>
              <span>{{ trendOrderCount }}</span>
            </div>
          </div>
        </el-card>

        <el-card shadow="never">
          <template #header>
            <div>
              <h3 class="section-title">经营建议</h3>
              <p class="section-subtitle">保留后台与小程序联动相关的关键提醒。</p>
            </div>
          </template>
          <ul class="plain-list">
            <li>商品上新、发货时效和店铺资料完整度会直接影响小程序前台展示。</li>
            <li>近期销量上升时建议同步检查库存、发货地址与运费模板。</li>
            <li>高浏览低转化的商品建议重点复查主图、价格和属性描述。</li>
          </ul>
        </el-card>
      </div>
    </section>

    <el-card shadow="never">
      <template #header>
        <div>
          <h3 class="section-title">趋势明细</h3>
          <p class="section-subtitle">保留表格化数据视图，适合核对每日变化。</p>
        </div>
      </template>
      <el-table :data="dashboard.trends" border>
        <el-table-column prop="date" label="日期" min-width="150" />
        <el-table-column prop="orderCount" label="订单数" min-width="120" />
        <el-table-column label="销售额" min-width="140">
          <template #default="{ row }">¥ {{ money(row.salesAmount) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { getDashboard } from '@/api/merchant'

const chartRef = ref()
const dashboard = ref({
  todayOrderCount: 0,
  todaySalesAmount: 0,
  totalSalesAmount: 0,
  goodsViewCount: 0,
  trends: []
})
const shopName = ref(localStorage.getItem('merchant_shop_name') || '我的店铺')
let chart
let resizeHandler

const trendOrderCount = computed(() =>
  (dashboard.value.trends || []).reduce((sum, item) => sum + Number(item.orderCount || 0), 0)
)

const cards = computed(() => [
  {
    label: '今日订单数',
    value: dashboard.value.todayOrderCount || 0,
    note: '适合查看是否需要优先处理待发货订单。'
  },
  {
    label: '今日销售额',
    value: `¥ ${money(dashboard.value.todaySalesAmount)}`,
    note: '便于判断活动效果与短期波动。'
  },
  {
    label: '累计销售额',
    value: `¥ ${money(dashboard.value.totalSalesAmount)}`,
    note: '持续观察店铺整体经营规模变化。'
  },
  {
    label: '商品浏览量',
    value: dashboard.value.goodsViewCount || 0,
    note: '结合销量判断商品主图和标题表现。'
  }
])

function money(value) {
  return Number(value || 0).toFixed(2)
}

function renderChart() {
  if (!chartRef.value) return
  if (!chart) {
    chart = echarts.init(chartRef.value)
  }

  chart.setOption({
    grid: { left: 48, right: 48, top: 28, bottom: 28 },
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    xAxis: {
      type: 'category',
      data: dashboard.value.trends.map((item) => item.date),
      axisLine: { lineStyle: { color: '#cfd5de' } },
      axisLabel: { color: '#5f6b7a' }
    },
    yAxis: [
      {
        type: 'value',
        name: '订单数',
        splitLine: { lineStyle: { color: '#e7eaef' } },
        axisLabel: { color: '#5f6b7a' }
      },
      {
        type: 'value',
        name: '销售额',
        position: 'right',
        splitLine: { show: false },
        axisLabel: { color: '#5f6b7a' }
      }
    ],
    series: [
      {
        name: '订单数',
        type: 'bar',
        barWidth: 22,
        data: dashboard.value.trends.map((item) => item.orderCount),
        itemStyle: {
          color: '#d06b34'
        }
      },
      {
        name: '销售额',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        symbolSize: 8,
        data: dashboard.value.trends.map((item) => Number(item.salesAmount || 0)),
        lineStyle: { color: '#ad5528', width: 3 },
        itemStyle: { color: '#ad5528' }
      }
    ]
  })
}

onMounted(async () => {
  dashboard.value = await getDashboard()
  shopName.value = localStorage.getItem('merchant_shop_name') || '我的店铺'
  await nextTick()
  renderChart()
  resizeHandler = () => chart?.resize()
  window.addEventListener('resize', resizeHandler)
})

onBeforeUnmount(() => {
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
  }
  chart?.dispose()
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

.main-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) minmax(320px, 0.8fr);
  gap: 16px;
}

.side-stack {
  display: grid;
  gap: 16px;
}

.chart {
  height: 340px;
}

.plain-list {
  margin: 0;
  padding-left: 18px;
  color: var(--text-secondary);
  line-height: 1.9;
}

@media (max-width: 1080px) {
  .overview-grid,
  .main-grid {
    grid-template-columns: 1fr 1fr;
  }

  .main-grid > :first-child {
    grid-column: 1 / -1;
  }
}

@media (max-width: 768px) {
  .overview-grid,
  .main-grid {
    grid-template-columns: 1fr;
  }
}
</style>
