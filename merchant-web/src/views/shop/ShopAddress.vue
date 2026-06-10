<template>
  <div class="page-shell">
    <section class="list-heading">
      <div>
        <h1 class="list-heading-title">发货地址</h1>
        <p class="list-heading-subtitle">地址管理改为传统分页列表，默认地址、编辑与删除操作保持不变。</p>
      </div>
      <el-button type="primary" @click="openDialog()">新增地址</el-button>
    </section>

    <el-card class="table-card" shadow="never">
      <el-table :data="list" v-loading="loading" border>
        <el-table-column label="地址信息" min-width="420">
          <template #default="{ row }">
            <div class="address-block">
              <div class="address-main">{{ row.province }}{{ row.city }}{{ row.district }}{{ row.detail }}</div>
              <div class="address-meta">邮编：{{ row.zipcode || '-' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="row.isDefault === 1 ? 'success' : 'info'">
              {{ row.isDefault === 1 ? '默认地址' : '普通地址' }}
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

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑发货地址' : '新增发货地址'" width="560px">
      <el-form :model="form" label-position="top">
        <div class="form-grid">
          <el-form-item label="省份"><el-input v-model="form.province" /></el-form-item>
          <el-form-item label="城市"><el-input v-model="form.city" /></el-form-item>
          <el-form-item label="区县"><el-input v-model="form.district" /></el-form-item>
          <el-form-item label="邮编"><el-input v-model="form.zipcode" /></el-form-item>
          <el-form-item label="详细地址" class="span-2"><el-input v-model="form.detail" /></el-form-item>
          <el-form-item label="默认地址" class="span-2">
            <el-switch v-model="form.isDefault" :active-value="true" :inactive-value="false" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存地址</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createAddress, deleteAddress, getAddressList, setDefaultAddress, updateAddress } from '@/api/merchant'

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
  province: '',
  city: '',
  district: '',
  detail: '',
  zipcode: '',
  isDefault: false
})

function resetForm() {
  Object.assign(form, {
    province: '',
    city: '',
    district: '',
    detail: '',
    zipcode: '',
    isDefault: false
  })
}

async function loadList() {
  loading.value = true
  try {
    allList.value = await getAddressList()
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
    province: row.province,
    city: row.city,
    district: row.district,
    detail: row.detail,
    zipcode: row.zipcode,
    isDefault: row.isDefault === 1
  })
}

async function save() {
  if (editingId.value) {
    await updateAddress(editingId.value, form)
  } else {
    await createAddress(form)
  }

  ElMessage.success('地址已保存')
  dialogVisible.value = false
  query.page = 1
  await loadList()
}

async function makeDefault(id) {
  await setDefaultAddress(id)
  ElMessage.success('默认地址已更新')
  await loadList()
}

async function removeItem(id) {
  await ElMessageBox.confirm('确定删除该地址吗？删除后发货页将无法继续使用它作为默认地址。', '删除确认', {
    type: 'warning'
  })
  await deleteAddress(id)
  ElMessage.success('地址已删除')
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

.address-block {
  display: grid;
  gap: 6px;
}

.address-main {
  font-weight: 600;
  color: var(--text-primary);
}

.address-meta {
  color: var(--text-secondary);
  font-size: 13px;
}

.row-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.span-2 {
  grid-column: 1 / -1;
}

@media (max-width: 768px) {
  .list-heading {
    flex-direction: column;
  }
}
</style>
