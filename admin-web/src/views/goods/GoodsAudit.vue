<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Goods Review</div>
        <h1 class="hero-title">商品审核列表</h1>
        <p class="hero-subtitle">
          商品审核页统一强化了主图、标题、店铺、价格和审核状态的层次，减少纯表格带来的生硬感。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">审核重点</div>
        <h3 class="card-title">主图、标题和属性会影响前台展示质量</h3>
        <p class="card-desc">建议在查看详情时重点确认主图、商品描述与属性录入是否适合前台直接展示。</p>
      </div>
    </section>

    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <h3 class="section-title">筛选条件</h3>
            <p class="section-subtitle">通过审核状态快速筛出待处理商品。</p>
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
          <h3 class="section-title">商品审核列表</h3>
          <p class="section-subtitle">商品主图、价格和状态都使用更稳定的视觉优先级。</p>
        </div>
      </template>
      <div class="table-scroll">
        <el-table :data="list" v-loading="loading" border>
          <el-table-column prop="id" label="ID" min-width="90" />
          <el-table-column label="商品信息" min-width="280">
            <template #default="{ row }">
              <div class="goods-inline">
                <el-image :src="row.mainImage" class="thumb" fit="cover" />
                <div>
                  <div class="goods-inline-title">{{ row.title }}</div>
                  <div class="goods-inline-meta">{{ row.shopName }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="价格" min-width="110">
            <template #default="{ row }"><strong>¥ {{ row.price }}</strong></template>
          </el-table-column>
          <el-table-column prop="stock" label="库存" min-width="100" />
          <el-table-column label="审核状态" min-width="120">
            <template #default="{ row }">
              <el-tag :type="row.auditStatus === 1 ? 'success' : row.auditStatus === 2 ? 'danger' : 'warning'">
                {{ row.auditStatus === 1 ? '已通过' : row.auditStatus === 2 ? '已驳回' : '待审核' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="提交时间" min-width="180" />
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
import { getGoodsAuditList } from '@/api/admin'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const filters = reactive({ auditStatus: 0 })
const pager = reactive({ page: 1, size: 10, total: 0 })

async function loadData() {
  loading.value = true
  try {
    const res = await getGoodsAuditList({
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
  router.push(`/goods/audit/${id}`)
}

onMounted(loadData)
</script>
