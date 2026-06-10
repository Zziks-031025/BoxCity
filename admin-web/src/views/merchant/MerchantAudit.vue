<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Merchant Review</div>
        <h1 class="hero-title">商家审核工作台</h1>
        <p class="hero-subtitle">
          通过更稳定的筛选区、状态表达和操作区，让审核列表兼顾桌面后台效率与品牌一致性。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">审核说明</div>
        <h3 class="card-title">待审核、通过、驳回使用统一状态语言</h3>
        <p class="card-desc">状态颜色和语义已与平台其他治理页面统一，降低来回切页时的认知成本。</p>
      </div>
    </section>

    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <h3 class="section-title">筛选条件</h3>
            <p class="section-subtitle">审核筛选区独立成块，不再和表格内容挤在一起。</p>
          </div>
          <el-button type="primary" @click="loadData">刷新列表</el-button>
        </div>
      </template>
      <div class="toolbar">
        <div class="filters">
          <el-select v-model="filters.auditStatus" placeholder="审核状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
          </el-select>
        </div>
      </div>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div>
          <h3 class="section-title">审核列表</h3>
          <p class="section-subtitle">店铺名、主体类型、状态和审核备注已按优先级重新组织。</p>
        </div>
      </template>
      <div class="table-scroll">
        <el-table :data="list" v-loading="loading" border>
          <el-table-column prop="id" label="ID" min-width="90" />
          <el-table-column prop="shopName" label="店铺名称" min-width="180" />
          <el-table-column prop="contactPhone" label="联系电话" min-width="150" />
          <el-table-column label="主体类型" min-width="110">
            <template #default="{ row }">{{ row.subjectType === 2 ? '企业' : '个人' }}</template>
          </el-table-column>
          <el-table-column label="审核状态" min-width="120">
            <template #default="{ row }">
              <el-tag :type="statusType(row.auditStatus)">{{ statusText(row.auditStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="auditRemark" label="审核备注" min-width="200" />
          <el-table-column prop="createdAt" label="申请时间" min-width="180" />
          <el-table-column label="操作" min-width="140" fixed="right">
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link type="primary" @click="goDetail(row.id)">查看详情</el-button>
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
import { getMerchantAuditList } from '@/api/admin'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const filters = reactive({ auditStatus: 0 })
const pager = reactive({ page: 1, size: 10, total: 0 })

function statusText(status) {
  return { 0: '待审核', 1: '已通过', 2: '已驳回', 3: '已冻结' }[status] || '未知'
}

function statusType(status) {
  return { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }[status] || 'info'
}

async function loadData() {
  loading.value = true
  try {
    const res = await getMerchantAuditList({
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

onMounted(loadData)
</script>
