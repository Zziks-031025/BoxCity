<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Order Monitor</div>
        <h1 class="hero-title">全平台订单监控</h1>
        <p class="hero-subtitle">
          订单监控页聚焦订单号、用户、店铺、金额和状态，帮助平台快速完成排查和联调回归。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">治理视角</div>
        <h3 class="card-title">订单状态、售后状态与支付状态应保持一致</h3>
        <p class="card-desc">当前仍为模拟支付环境，但订单数据链路已可用于前后台联调与验收。</p>
      </div>
    </section>

    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <h3 class="section-title">筛选条件</h3>
            <p class="section-subtitle">支持按订单号关键词和订单状态快速定位。</p>
          </div>
          <el-button type="primary" @click="loadData">刷新列表</el-button>
        </div>
      </template>
      <div class="toolbar">
        <div class="filters">
          <el-input v-model="filters.keyword" placeholder="订单号" clearable @keyup.enter="loadData" />
          <el-select v-model="filters.status" placeholder="订单状态" clearable>
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
            <el-option label="售后中" :value="5" />
          </el-select>
        </div>
      </div>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div>
          <h3 class="section-title">订单监控列表</h3>
          <p class="section-subtitle">金额与状态已统一成更清晰的阅读顺序。</p>
        </div>
      </template>
      <div class="table-scroll">
        <el-table :data="list" v-loading="loading" border>
          <el-table-column prop="orderNo" label="订单号" min-width="200" />
          <el-table-column prop="userName" label="用户" min-width="140" />
          <el-table-column prop="shopName" label="店铺" min-width="180" />
          <el-table-column label="实付金额" min-width="120">
            <template #default="{ row }"><strong>¥ {{ Number(row.payAmount || 0).toFixed(2) }}</strong></template>
          </el-table-column>
          <el-table-column prop="status" label="订单状态" min-width="120" />
          <el-table-column prop="refundStatus" label="售后状态" min-width="120" />
          <el-table-column prop="createdAt" label="下单时间" min-width="180" />
          <el-table-column prop="payTime" label="支付时间" min-width="180" />
        </el-table>
      </div>
      <el-pagination
        v-model:current-page="pager.page"
        v-model:page-size="pager.size"
        layout="total, prev, pager, next"
        :total="pager.total"
        @current-change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getOrderList } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const filters = reactive({ keyword: '', status: null })
const pager = reactive({ page: 1, size: 10, total: 0 })

async function loadData() {
  loading.value = true
  try {
    const res = await getOrderList({
      keyword: filters.keyword || undefined,
      status: filters.status,
      page: pager.page,
      size: pager.size
    })
    list.value = res.records || []
    pager.total = res.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
