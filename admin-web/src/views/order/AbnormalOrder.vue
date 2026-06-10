<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Abnormal Orders</div>
        <h1 class="hero-title">异常订单排查</h1>
        <p class="hero-subtitle">
          异常订单页聚焦异常原因、订单金额和时间信息，帮助平台快速定位需要追踪的问题订单。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">排查建议</div>
        <h3 class="card-title">优先根据异常原因做分类处理</h3>
        <p class="card-desc">统一的风险提示层次可以帮助平台区分支付异常、物流异常和售后异常。</p>
      </div>
    </section>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <h3 class="section-title">异常订单列表</h3>
            <p class="section-subtitle">原因列优先级更高，方便扫读和排查。</p>
          </div>
          <el-button type="primary" @click="loadData">刷新列表</el-button>
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
          <el-table-column prop="abnormalReason" label="异常原因" min-width="260" />
          <el-table-column prop="createdAt" label="下单时间" min-width="180" />
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
import { getAbnormalOrders } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const pager = reactive({ page: 1, size: 10, total: 0 })

async function loadData() {
  loading.value = true
  try {
    const res = await getAbnormalOrders({ page: pager.page, size: pager.size })
    list.value = res.records || []
    pager.total = res.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
