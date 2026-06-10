<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">User Governance</div>
        <h1 class="hero-title">用户管理</h1>
        <p class="hero-subtitle">
          用户列表重点展示昵称、手机号、订单量和账号状态，方便平台快速进行状态治理与详情排查。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">治理说明</div>
        <h3 class="card-title">禁用与启用动作会影响前台登录和下单体验</h3>
        <p class="card-desc">建议仅在明确违规或风险场景下进行状态调整，并保留处理依据。</p>
      </div>
    </section>

    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <h3 class="section-title">筛选条件</h3>
            <p class="section-subtitle">支持按昵称、手机号和状态快速筛选。</p>
          </div>
          <el-button type="primary" @click="loadData">查询</el-button>
        </div>
      </template>
      <div class="toolbar">
        <div class="filters">
          <el-input v-model="filters.keyword" placeholder="昵称 / 手机号" clearable @keyup.enter="loadData" />
          <el-select v-model="filters.status" placeholder="状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </div>
      </div>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div>
          <h3 class="section-title">用户列表</h3>
          <p class="section-subtitle">状态与操作按钮已统一，避免列表页按钮扎堆或一边留白。</p>
        </div>
      </template>
      <div class="table-scroll">
        <el-table :data="list" v-loading="loading" border>
          <el-table-column prop="id" label="ID" min-width="90" />
          <el-table-column prop="nickname" label="昵称" min-width="150" />
          <el-table-column prop="phone" label="手机号" min-width="150" />
          <el-table-column prop="orderCount" label="订单数" min-width="100" />
          <el-table-column label="状态" min-width="110">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="注册时间" min-width="180" />
          <el-table-column label="操作" min-width="220" fixed="right">
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link type="primary" @click="goDetail(row.id)">详情</el-button>
                <el-button link :type="row.status === 1 ? 'danger' : 'success'" @click="toggleStatus(row)">
                  {{ row.status === 1 ? '禁用' : '启用' }}
                </el-button>
              </div>
            </template>
          </el-table-column>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUserStatus } from '@/api/admin'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const filters = reactive({
  keyword: '',
  status: null
})
const pager = reactive({
  page: 1,
  size: 10,
  total: 0
})

async function loadData() {
  loading.value = true
  try {
    const res = await getUserList({
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

function goDetail(id) {
  router.push(`/user/detail/${id}`)
}

async function toggleStatus(row) {
  const nextStatus = row.status === 1 ? 0 : 1
  await ElMessageBox.confirm(`确认${nextStatus === 1 ? '启用' : '禁用'}该用户吗？`, '状态变更', { type: 'warning' })
  await updateUserStatus(row.id, nextStatus)
  ElMessage.success('用户状态已更新')
  loadData()
}

onMounted(loadData)
</script>
