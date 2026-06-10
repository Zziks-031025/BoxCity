<template>
  <div class="page-shell">
    <section class="list-heading">
      <div>
        <h1 class="list-heading-title">售后管理</h1>
        <p class="list-heading-subtitle">按状态筛选并分页查看售后申请，保留原有处理流程与详情入口。</p>
      </div>
      <div class="list-heading-meta">共 {{ total }} 条记录</div>
    </section>

    <el-card shadow="never">
      <div class="toolbar">
        <div class="filters">
          <el-select v-model="query.status" clearable placeholder="售后状态" @change="handleFilterChange">
            <el-option label="待商家处理" :value="0" />
            <el-option label="待退货" :value="1" />
            <el-option label="退货中" :value="2" />
            <el-option label="退款成功" :value="3" />
            <el-option label="商家拒绝" :value="4" />
            <el-option label="平台介入中" :value="5" />
          </el-select>
        </div>
        <div class="toolbar-note">列表页统一为传统表格 + 分页结构，状态与退款金额优先展示。</div>
      </div>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="售后单号" min-width="120" />
        <el-table-column prop="orderNo" label="订单号" min-width="220" />
        <el-table-column label="商品信息" min-width="280">
          <template #default="{ row }">
            <div class="goods-inline">
              <el-image :src="row.goodsImage" class="thumb" fit="cover" />
              <div>
                <div class="goods-inline-title">{{ row.goodsTitle }}</div>
                <div class="goods-inline-meta">申请时间：{{ row.createdAt || '-' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="退款金额" min-width="120">
          <template #default="{ row }">
            <strong>¥ {{ Number(row.refundAmount || 0).toFixed(2) }}</strong>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="140">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="router.push(`/aftersale/detail/${row.id}`)">查看详情</el-button>
            <template v-if="row.status === 0">
              <el-button link type="success" @click="handleAgree(row)">同意</el-button>
              <el-button link type="danger" @click="openReject(row)">拒绝</el-button>
            </template>
            <el-button v-if="row.status === 2" link type="primary" @click="handleConfirm(row)">确认收货</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadList"
      />
    </el-card>

    <el-dialog v-model="rejectDialog" title="填写拒绝原因" width="420px">
      <el-input v-model="rejectReason" type="textarea" :rows="4" placeholder="请说明拒绝原因" />
      <template #footer>
        <el-button @click="rejectDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReject">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAftersaleList, handleAftersale, confirmAftersaleReturn } from '@/api/merchant'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({
  status: undefined,
  page: 1,
  size: 10
})
const rejectDialog = ref(false)
const rejectReason = ref('')
const currentRow = ref(null)

function statusLabel(status) {
  return {
    0: '待商家处理',
    1: '待退货',
    2: '退货中',
    3: '退款成功',
    4: '商家拒绝',
    5: '平台介入中',
    6: '已关闭'
  }[status] || '未知'
}

function statusType(status) {
  return {
    0: 'warning',
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'danger',
    5: 'danger',
    6: 'info'
  }[status] || 'info'
}

async function loadList() {
  loading.value = true
  try {
    const data = await getAftersaleList(query)
    list.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function handleFilterChange() {
  query.page = 1
  loadList()
}

async function handleAgree(row) {
  await ElMessageBox.confirm('确认同意该售后申请？', '提示', { type: 'warning' })
  await handleAftersale(row.id, { agree: true, rejectReason: '' })
  ElMessage.success('已同意售后申请')
  loadList()
}

function openReject(row) {
  currentRow.value = row
  rejectReason.value = ''
  rejectDialog.value = true
}

async function submitReject() {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请填写拒绝原因')
    return
  }
  await handleAftersale(currentRow.value.id, { agree: false, rejectReason: rejectReason.value })
  ElMessage.success('已拒绝售后申请')
  rejectDialog.value = false
  loadList()
}

async function handleConfirm(row) {
  await ElMessageBox.confirm('确认已收到退货并同意退款？', '提示', { type: 'warning' })
  await confirmAftersaleReturn(row.id)
  ElMessage.success('退款已完成')
  loadList()
}

onMounted(loadList)
</script>

<style scoped>
.list-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.list-heading-title {
  margin: 0;
  font-size: 24px;
  line-height: 1.3;
  color: var(--text-primary);
}

.list-heading-subtitle {
  margin: 8px 0 0;
  color: var(--text-secondary);
  line-height: 1.7;
  font-size: 14px;
}

.list-heading-meta {
  padding-top: 6px;
  color: var(--text-secondary);
  white-space: nowrap;
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-note {
  color: var(--text-secondary);
  font-size: 13px;
}

.table-card :deep(.el-card__body) {
  display: grid;
  gap: 16px;
}

.goods-inline {
  display: flex;
  align-items: center;
  gap: 12px;
}

.thumb {
  width: 56px;
  height: 56px;
  border: 1px solid var(--border-color);
  flex: 0 0 auto;
}

.goods-inline-title {
  font-weight: 600;
  color: var(--text-primary);
}

.goods-inline-meta {
  margin-top: 4px;
  color: var(--text-secondary);
  font-size: 13px;
}

@media (max-width: 768px) {
  .list-heading {
    flex-direction: column;
  }
}
</style>
