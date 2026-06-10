<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Goods Center</div>
        <h1 class="hero-title">商品列表与审核状态一屏掌握</h1>
        <p class="hero-subtitle">
          页面保留了后台表格效率，同时强化了商品主图、价格、销量和状态的阅读顺序，适合高频巡检和快速操作。
        </p>
        <div class="metric-inline">
          <div class="metric-chip">
            <div class="metric-chip-label">当前分页</div>
            <div class="metric-chip-value">{{ query.page }}</div>
          </div>
          <div class="metric-chip">
            <div class="metric-chip-label">商品总数</div>
            <div class="metric-chip-value">{{ total }}</div>
          </div>
          <div class="metric-chip">
            <div class="metric-chip-label">筛选状态</div>
            <div class="metric-chip-value">{{ activeFilterLabel }}</div>
          </div>
        </div>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">前台映射</div>
        <h3 class="card-title">商品图、标题和状态会同步影响前台体验</h3>
        <p class="card-desc">
          审核通过且已上架的商品会在小程序搜索、分类和店铺页中展示。建议优先检查主图质量与属性录入完整度。
        </p>
      </div>
    </section>

    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <h3 class="section-title">筛选与管理</h3>
            <p class="section-subtitle">筛选区单独成块，减少和表格内容混在一起造成的拥挤感。</p>
          </div>
          <el-button type="primary" @click="router.push('/goods/edit')">发布商品</el-button>
        </div>
      </template>

      <div class="toolbar">
        <div class="filters">
          <el-select v-model="query.auditStatus" clearable placeholder="审核状态" @change="loadList">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="未通过" :value="2" />
          </el-select>
          <el-select v-model="query.status" clearable placeholder="上架状态" @change="loadList">
            <el-option label="已下架" :value="0" />
            <el-option label="已上架" :value="1" />
          </el-select>
        </div>
        <div class="toolbar-note">
          当前列表支持上架、下架、编辑和删除操作，所有按钮会自动换行，避免窄屏时内容重叠。
        </div>
      </div>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <h3 class="section-title">商品清单</h3>
            <p class="section-subtitle">统一强调主图、价格、销量、审核状态和上架状态。</p>
          </div>
          <div class="status-pill">共 {{ total }} 件商品</div>
        </div>
      </template>
      <div class="table-scroll">
        <el-table :data="list" v-loading="loading" border>
          <el-table-column label="商品" min-width="280">
            <template #default="{ row }">
              <div class="goods-inline">
                <el-image :src="row.mainImage" class="thumb" fit="cover" />
                <div>
                  <div class="goods-inline-title">{{ row.title }}</div>
                  <div class="goods-inline-meta">创建时间：{{ row.createdAt || '-' }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="价格" min-width="130">
            <template #default="{ row }">
              <strong>¥ {{ money(row.price) }}</strong>
            </template>
          </el-table-column>
          <el-table-column prop="stock" label="库存" min-width="100" />
          <el-table-column prop="sales" label="销量" min-width="100" />
          <el-table-column label="审核状态" min-width="130">
            <template #default="{ row }">
              <el-tag :type="auditTag(row.auditStatus)">{{ auditLabel(row.auditStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="上架状态" min-width="120">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">
                {{ row.status === 1 ? '已上架' : '已下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="220" fixed="right">
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link type="primary" @click="router.push(`/goods/edit/${row.id}`)">编辑</el-button>
                <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
                  {{ row.status === 1 ? '下架' : '上架' }}
                </el-button>
                <el-button link type="danger" @click="removeItem(row.id)">删除</el-button>
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
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteGoods, getMerchantGoodsList, updateGoodsStatus } from '@/api/merchant'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({
  auditStatus: undefined,
  status: undefined,
  page: 1,
  size: 10
})

const activeFilterLabel = computed(() => {
  if (query.auditStatus != null) return auditLabel(query.auditStatus)
  if (query.status != null) return query.status === 1 ? '已上架' : '已下架'
  return '全部'
})

function money(value) {
  return Number(value || 0).toFixed(2)
}

function auditLabel(status) {
  return { 0: '待审核', 1: '已通过', 2: '未通过' }[status] || '未知'
}

function auditTag(status) {
  return { 0: 'warning', 1: 'success', 2: 'danger' }[status] || 'info'
}

async function loadList() {
  loading.value = true
  try {
    const data = await getMerchantGoodsList(query)
    list.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

async function toggleStatus(row) {
  await updateGoodsStatus(row.id, row.status === 1 ? 0 : 1)
  ElMessage.success('商品状态已更新')
  loadList()
}

async function removeItem(id) {
  await ElMessageBox.confirm('确定删除这件商品吗？删除后前台对应展示也会同步消失。', '删除确认', { type: 'warning' })
  await deleteGoods(id)
  ElMessage.success('商品已删除')
  loadList()
}

onMounted(loadList)
</script>
