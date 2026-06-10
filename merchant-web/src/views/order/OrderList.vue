<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Order Center</div>
        <h1 class="hero-title">订单列表按状态、商品和操作重新聚焦</h1>
        <p class="hero-subtitle">
          页面结构参考了小程序订单阅读逻辑，但保留了后台表格效率，优先强调状态、商品信息和处理动作。
        </p>
        <div class="metric-inline">
          <div class="metric-chip">
            <div class="metric-chip-label">当前页订单</div>
            <div class="metric-chip-value">{{ list.length }}</div>
          </div>
          <div class="metric-chip">
            <div class="metric-chip-label">总订单数</div>
            <div class="metric-chip-value">{{ total }}</div>
          </div>
          <div class="metric-chip">
            <div class="metric-chip-label">筛选状态</div>
            <div class="metric-chip-value">{{ statusLabel(query.status) }}</div>
          </div>
        </div>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">处理建议</div>
        <h3 class="card-title">优先处理待发货订单和异常售后状态</h3>
        <p class="card-desc">
          发货动作和物流信息会同步影响小程序订单详情页展示，建议每天优先巡检待发货与售后中的订单。
        </p>
      </div>
    </section>

    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <h3 class="section-title">筛选条件</h3>
            <p class="section-subtitle">筛选区与列表区分开，避免在窄屏下挤压表格内容。</p>
          </div>
        </div>
      </template>
      <div class="toolbar">
        <div class="filters">
          <el-select v-model="query.status" clearable placeholder="订单状态" @change="loadList">
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
            <el-option label="售后中" :value="5" />
          </el-select>
        </div>
        <div class="toolbar-note">订单卡片化信息已经压缩进表格单元格里，能更快扫读商品、金额和状态。</div>
      </div>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <h3 class="section-title">订单清单</h3>
            <p class="section-subtitle">状态、金额和操作区经过统一，不会出现按钮扎堆或布局失衡。</p>
          </div>
          <div class="status-pill">共 {{ total }} 笔订单</div>
        </div>
      </template>
      <div class="table-scroll">
        <el-table :data="list" v-loading="loading" border>
          <el-table-column prop="orderNo" label="订单号" min-width="220" />
          <el-table-column label="商品信息" min-width="280">
            <template #default="{ row }">
              <div class="goods-inline">
                <el-image :src="row.firstItem?.mainImage" class="thumb" fit="cover" />
                <div>
                  <div class="goods-inline-title">{{ row.firstItem?.goodsTitle || '-' }}</div>
                  <div class="goods-inline-meta">共 {{ row.itemCount || 0 }} 件商品</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="实付金额" min-width="130">
            <template #default="{ row }">
              <strong>¥ {{ Number(row.payAmount || 0).toFixed(2) }}</strong>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="下单时间" min-width="180" />
          <el-table-column label="状态" min-width="120">
            <template #default="{ row }">
              <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="180" fixed="right">
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link type="primary" @click="router.push(`/order/detail/${row.orderNo}`)">查看详情</el-button>
                <el-button v-if="row.status === 1" link type="success" @click="router.push(`/order/ship/${row.orderNo}`)">去发货</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadList"
      />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList } from '@/api/merchant'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ status: undefined, page: 1, size: 10 })

function statusLabel(status) {
  return { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消', 5: '售后中' }[status] || '全部'
}

function statusType(status) {
  return { 0: 'warning', 1: 'warning', 2: 'primary', 3: 'success', 4: 'info', 5: 'danger' }[status] || 'info'
}

async function loadList() {
  loading.value = true
  try {
    const data = await getOrderList(query)
    list.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(loadList)
</script>
