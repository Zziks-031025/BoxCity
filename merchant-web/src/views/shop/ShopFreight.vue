<template>
  <div class="page-shell">
    <section class="list-heading">
      <div>
        <h1 class="list-heading-title">运费模板</h1>
        <p class="list-heading-subtitle">模板列表统一改为传统分页结构，新增、编辑、设为默认与删除操作保持不变。</p>
      </div>
      <el-button type="primary" @click="openDialog()">新增模板</el-button>
    </section>

    <el-card class="table-card" shadow="never">
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="name" label="模板名称" min-width="180" />
        <el-table-column label="计费方式" min-width="140">
          <template #default="{ row }">
            <el-tag :type="row.chargeType === 0 ? 'success' : row.chargeType === 1 ? 'warning' : 'primary'">
              {{ chargeTypeLabel(row.chargeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="规则摘要" min-width="340">
          <template #default="{ row }">
            <div class="rule-summary">
              <template v-if="row.chargeType === 0">全国包邮</template>
              <template v-else>
                首{{ row.rules?.[0]?.firstUnit || 1 }}件/重，¥ {{ money(row.rules?.[0]?.firstFee) }}；
                续{{ row.rules?.[0]?.additionalUnit || 1 }}件/重，¥ {{ money(row.rules?.[0]?.additionalFee) }}
              </template>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="row.isDefault === 1 ? 'success' : 'info'">
              {{ row.isDefault === 1 ? '默认模板' : '普通模板' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="240" fixed="right">
          <template #default="{ row }">
            <div class="row-actions">
              <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
              <el-button link type="success" @click="makeDefault(row.id)">设为默认</el-button>
              <el-button link type="danger" @click="removeItem(row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        layout="total, prev, pager, next"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑运费模板' : '新增运费模板'" width="620px">
      <el-form :model="form" label-position="top">
        <div class="form-grid">
          <el-form-item label="模板名称" class="span-2">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="计费方式" class="span-2">
            <el-radio-group v-model="form.chargeType" class="charge-group">
              <el-radio :value="0">全国包邮</el-radio>
              <el-radio :value="1">按件计费</el-radio>
              <el-radio :value="2">按重量计费</el-radio>
            </el-radio-group>
          </el-form-item>
          <template v-if="form.chargeType !== 0">
            <el-form-item label="首件 / 首重">
              <el-input-number v-model="form.firstUnit" :min="1" />
            </el-form-item>
            <el-form-item label="首费">
              <el-input-number v-model="form.firstFee" :min="0" :precision="2" />
            </el-form-item>
            <el-form-item label="续件 / 续重">
              <el-input-number v-model="form.additionalUnit" :min="1" />
            </el-form-item>
            <el-form-item label="续费">
              <el-input-number v-model="form.additionalFee" :min="0" :precision="2" />
            </el-form-item>
          </template>
          <el-form-item label="默认模板" class="span-2">
            <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存模板</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addFreight, deleteFreight, getFreightList, setDefaultFreight, updateFreight } from '@/api/merchant'

const loading = ref(false)
const allList = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const query = reactive({
  page: 1,
  size: 10
})
const total = computed(() => allList.value.length)
const list = computed(() => {
  const start = (query.page - 1) * query.size
  return allList.value.slice(start, start + query.size)
})
const form = reactive({
  name: '',
  chargeType: 0,
  firstUnit: 1,
  firstFee: 0,
  additionalUnit: 1,
  additionalFee: 0,
  isDefault: 0
})

function resetForm() {
  Object.assign(form, {
    name: '',
    chargeType: 0,
    firstUnit: 1,
    firstFee: 0,
    additionalUnit: 1,
    additionalFee: 0,
    isDefault: 0
  })
}

function money(value) {
  return Number(value || 0).toFixed(2)
}

function chargeTypeLabel(type) {
  return {
    0: '全国包邮',
    1: '按件计费',
    2: '按重量计费'
  }[type] || '未知'
}

async function loadList() {
  loading.value = true
  try {
    allList.value = await getFreightList()
    const maxPage = Math.max(1, Math.ceil(allList.value.length / query.size))
    if (query.page > maxPage) {
      query.page = maxPage
    }
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  dialogVisible.value = true
  if (!row) {
    editingId.value = null
    resetForm()
    return
  }

  editingId.value = row.id
  Object.assign(form, {
    name: row.name,
    chargeType: row.chargeType,
    firstUnit: row.rules?.[0]?.firstUnit || 1,
    firstFee: Number(row.rules?.[0]?.firstFee || 0),
    additionalUnit: row.rules?.[0]?.additionalUnit || 1,
    additionalFee: Number(row.rules?.[0]?.additionalFee || 0),
    isDefault: row.isDefault
  })
}

function toPayload() {
  return {
    name: form.name,
    chargeType: form.chargeType,
    isDefault: form.isDefault,
    rules: form.chargeType === 0
      ? []
      : [
          {
            firstUnit: form.firstUnit,
            firstFee: form.firstFee,
            additionalUnit: form.additionalUnit,
            additionalFee: form.additionalFee
          }
        ],
    excludes: []
  }
}

async function save() {
  if (editingId.value) {
    await updateFreight(editingId.value, toPayload())
  } else {
    await addFreight(toPayload())
  }

  ElMessage.success('运费模板已保存')
  dialogVisible.value = false
  query.page = 1
  await loadList()
}

async function makeDefault(id) {
  await setDefaultFreight(id)
  ElMessage.success('默认模板已更新')
  await loadList()
}

async function removeItem(id) {
  await ElMessageBox.confirm('确定删除该模板吗？删除后可能影响商品运费计算。', '删除确认', {
    type: 'warning'
  })
  await deleteFreight(id)
  ElMessage.success('运费模板已删除')
  await loadList()
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

.table-card :deep(.el-card__body) {
  display: grid;
  gap: 16px;
}

.rule-summary {
  color: var(--text-secondary);
  line-height: 1.7;
}

.row-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.span-2 {
  grid-column: 1 / -1;
}

.charge-group {
  min-height: 40px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

@media (max-width: 768px) {
  .list-heading {
    flex-direction: column;
  }
}
</style>
