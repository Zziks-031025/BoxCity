<template>
  <div class="page-shell" v-loading="loading">
    <section v-if="detail" class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">User Detail</div>
        <h1 class="hero-title">{{ detail.nickname || `用户 ${detail.id}` }}</h1>
        <p class="hero-subtitle">
          用户详情按基础信息、最近订单和最近举报分区，避免在同一张卡里堆叠过多数据。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">治理视角</div>
        <h3 class="card-title">订单与举报可以辅助判断账号风险</h3>
        <p class="card-desc">详情页的结构已经与平台其他治理页面统一，便于快速横向切换排查。</p>
        <div class="panel-actions">
          <el-button @click="router.back()">返回列表</el-button>
        </div>
      </div>
    </section>

    <div v-if="detail" class="detail-grid">
      <el-card shadow="never">
        <template #header><h3 class="section-title">基础信息</h3></template>
        <div class="data-list">
          <div class="data-row"><strong>用户 ID</strong><span>{{ detail.id }}</span></div>
          <div class="data-row"><strong>昵称</strong><span>{{ detail.nickname || '-' }}</span></div>
          <div class="data-row"><strong>手机号</strong><span>{{ detail.phone || '-' }}</span></div>
          <div class="data-row"><strong>状态</strong><span>{{ detail.status === 1 ? '正常' : '禁用' }}</span></div>
          <div class="data-row"><strong>性别</strong><span>{{ genderText(detail.gender) }}</span></div>
          <div class="data-row"><strong>生日</strong><span>{{ detail.birthday || '-' }}</span></div>
        </div>
      </el-card>

      <el-card shadow="never">
        <template #header><h3 class="section-title">账户统计</h3></template>
        <div class="data-list">
          <div class="data-row"><strong>注册时间</strong><span>{{ detail.createdAt || '-' }}</span></div>
          <div class="data-row"><strong>订单数</strong><span>{{ detail.orderCount || 0 }}</span></div>
        </div>
      </el-card>

      <el-card class="span-2" shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">最近订单</h3>
            <p class="section-subtitle">用于了解用户近期下单行为和履约状态。</p>
          </div>
        </template>
        <div class="table-scroll">
          <el-table :data="detail?.orders || []" border>
            <el-table-column prop="orderNo" label="订单号" min-width="200" />
            <el-table-column prop="shopName" label="店铺" min-width="160" />
            <el-table-column label="实付金额" min-width="120">
              <template #default="{ row }"><strong>¥ {{ Number(row.payAmount || 0).toFixed(2) }}</strong></template>
            </el-table-column>
            <el-table-column prop="status" label="状态" min-width="100" />
            <el-table-column prop="createdAt" label="下单时间" min-width="180" />
          </el-table>
        </div>
      </el-card>

      <el-card class="span-2" shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">最近举报</h3>
            <p class="section-subtitle">举报处理结果会帮助平台判断账号风险。</p>
          </div>
        </template>
        <div class="table-scroll">
          <el-table :data="detail?.reports || []" border>
            <el-table-column prop="reason" label="举报原因" min-width="220" />
            <el-table-column prop="status" label="处理状态" min-width="120" />
            <el-table-column prop="handleResult" label="处理结果" min-width="240" />
            <el-table-column prop="createdAt" label="提交时间" min-width="180" />
          </el-table>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserDetail } from '@/api/admin'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref(null)

function genderText(gender) {
  return { 0: '未知', 1: '男', 2: '女' }[gender] || '未知'
}

onMounted(async () => {
  loading.value = true
  try {
    detail.value = await getUserDetail(route.params.id)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.span-2 {
  grid-column: 1 / -1;
}
</style>
