<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Merchant Management</div>
        <h1 class="hero-title">商家列表支持状态治理与备注追踪</h1>
        <p class="hero-subtitle">
          这页重点强调商家状态、联系方式和处理动作，方便平台快速识别需要冻结或恢复的商家账号。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">治理规则</div>
        <h3 class="card-title">状态变化应配合备注记录</h3>
        <p class="card-desc">处理备注会帮助后续追踪冻结原因、恢复原因和商家沟通结果。</p>
      </div>
    </section>

    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <h3 class="section-title">筛选条件</h3>
            <p class="section-subtitle">通过状态筛选快速聚焦需要治理的商家。</p>
          </div>
          <el-button type="primary" @click="loadData">刷新列表</el-button>
        </div>
      </template>
      <div class="toolbar">
        <div class="filters">
          <el-select v-model="filters.auditStatus" placeholder="状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="正常" :value="1" />
            <el-option label="驳回" :value="2" />
            <el-option label="冻结" :value="3" />
          </el-select>
        </div>
      </div>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div>
          <h3 class="section-title">商家清单</h3>
          <p class="section-subtitle">状态、备注和操作区使用统一语义与配色。</p>
        </div>
      </template>
      <div class="table-scroll">
        <el-table :data="list" v-loading="loading" border>
          <el-table-column prop="id" label="ID" min-width="90" />
          <el-table-column prop="shopName" label="店铺名称" min-width="180" />
          <el-table-column prop="contactPhone" label="联系电话" min-width="150" />
          <el-table-column prop="contactEmail" label="联系邮箱" min-width="200" />
          <el-table-column label="状态" min-width="120">
            <template #default="{ row }">
              <el-tag :type="statusType(row.auditStatus)">{{ statusText(row.auditStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="auditRemark" label="备注" min-width="220" />
          <el-table-column label="操作" min-width="220" fixed="right">
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link type="primary" @click="goDetail(row.id)">详情</el-button>
                <el-button v-if="row.auditStatus === 1" link type="danger" @click="changeStatus(row, 3)">冻结</el-button>
                <el-button v-if="row.auditStatus === 3" link type="success" @click="changeStatus(row, 1)">恢复</el-button>
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
import { getMerchantList, updateMerchantStatus } from '@/api/admin'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const filters = reactive({ auditStatus: null })
const pager = reactive({ page: 1, size: 10, total: 0 })

function statusText(status) {
  return { 0: '待审核', 1: '正常', 2: '驳回', 3: '冻结' }[status] || '未知'
}

function statusType(status) {
  return { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }[status] || 'info'
}

async function loadData() {
  loading.value = true
  try {
    const res = await getMerchantList({
      auditStatus: filters.auditStatus,
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
  router.push(`/merchant/audit/${id}`)
}

async function changeStatus(row, status) {
  const { value } = await ElMessageBox.prompt('请输入处理备注', '状态变更', {
    inputValue: row.auditRemark || '',
    confirmButtonText: '提交'
  })
  await updateMerchantStatus(row.id, { status, remark: value || '' })
  ElMessage.success('商家状态已更新')
  loadData()
}

onMounted(loadData)
</script>
