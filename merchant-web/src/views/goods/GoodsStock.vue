<template>
  <div class="page-shell">
    <section class="list-heading">
      <div>
        <h1 class="list-heading-title">库存管理</h1>
        <p class="list-heading-subtitle">通过分页表格维护库存，低库存商品优先高亮，保留原有即时保存能力。</p>
      </div>
      <div class="list-heading-meta">共 {{ total }} 件商品</div>
    </section>

    <el-card class="table-card" shadow="never">
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="title" label="商品标题" min-width="320" />
        <el-table-column label="当前库存" min-width="120">
          <template #default="{ row }">
            <el-tag :type="row.stock <= 0 ? 'danger' : row.stock < 10 ? 'warning' : 'success'">
              {{ row.stock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="调整库存" min-width="220">
          <template #default="{ row }">
            <el-input-number v-model="row._stock" :min="0" />
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="save(row)">保存</el-button>
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
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getMerchantGoodsList, updateGoodsStock } from '@/api/merchant'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({
  page: 1,
  size: 10
})

async function loadList() {
  loading.value = true
  try {
    const data = await getMerchantGoodsList(query)
    list.value = (data.records || []).map((item) => ({
      ...item,
      _stock: item.stock
    }))
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

async function save(row) {
  await updateGoodsStock(row.id, row._stock)
  ElMessage.success('库存已更新')
  row.stock = row._stock
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

.table-card :deep(.el-card__body) {
  display: grid;
  gap: 16px;
}

@media (max-width: 768px) {
  .list-heading {
    flex-direction: column;
  }
}
</style>
