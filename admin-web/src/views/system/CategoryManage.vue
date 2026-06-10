<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Category Manage</div>
        <h1 class="hero-title">分类配置更强调层级与前台影响</h1>
        <p class="hero-subtitle">
          分类管理不仅是维护名称与排序，也会直接影响小程序分类页、搜索筛选和商品挂载效果。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">配置提醒</div>
        <h3 class="card-title">分类层级和状态会影响前台可见性</h3>
        <p class="card-desc">编辑分类时建议同步关注父级关系、排序值和启停状态，减少前后台表现偏差。</p>
      </div>
    </section>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <h3 class="section-title">分类列表</h3>
            <p class="section-subtitle">通过层级缩进、状态标签和操作区统一结构，让管理更清晰。</p>
          </div>
          <el-button type="primary" @click="openDialog()">新增分类</el-button>
        </div>
      </template>
      <div class="table-scroll">
        <el-table :data="rows" v-loading="loading" border row-key="id">
          <el-table-column prop="id" label="ID" min-width="90" />
          <el-table-column prop="displayName" label="分类名称" min-width="260" />
          <el-table-column prop="sort" label="排序" min-width="100" />
          <el-table-column label="状态" min-width="110">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="180" fixed="right">
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
                <el-button link type="danger" @click="remove(row.id)">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="500px">
      <el-form label-position="top">
        <el-form-item label="父级分类">
          <el-select v-model="form.parentId" placeholder="请选择父级分类">
            <el-option label="顶级分类" :value="0" />
            <el-option v-for="item in parentOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类名称">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="启用状态">
          <el-switch v-model="enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createCategory, deleteCategory, getCategoryTree, updateCategory } from '@/api/admin'

const loading = ref(false)
const dialogVisible = ref(false)
const tree = ref([])
const form = reactive({
  id: null,
  parentId: 0,
  name: '',
  sort: 0,
  status: 1
})
const enabled = computed({
  get: () => form.status === 1,
  set: (value) => {
    form.status = value ? 1 : 0
  }
})

const rows = computed(() => {
  const result = []
  const walk = (items = [], level = 0) => {
    items.forEach((item) => {
      result.push({ ...item, displayName: `${'　'.repeat(level)}${item.name}` })
      walk(item.children || [], level + 1)
    })
  }
  walk(tree.value)
  return result
})

const parentOptions = computed(() => tree.value || [])

async function loadData() {
  loading.value = true
  try {
    tree.value = await getCategoryTree()
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.id = null
  form.parentId = 0
  form.name = ''
  form.sort = 0
  form.status = 1
}

function openDialog(row) {
  if (!row) {
    resetForm()
  } else {
    form.id = row.id
    form.parentId = row.parentId || 0
    form.name = row.name
    form.sort = row.sort || 0
    form.status = row.status ?? 1
  }
  dialogVisible.value = true
}

async function submit() {
  const payload = {
    parentId: form.parentId,
    name: form.name,
    sort: form.sort,
    status: form.status
  }
  if (form.id) {
    await updateCategory(form.id, payload)
  } else {
    await createCategory(payload)
  }
  ElMessage.success('分类已保存')
  dialogVisible.value = false
  loadData()
}

async function remove(id) {
  await ElMessageBox.confirm('删除后将同步删除子分类，确认继续吗？', '删除确认', { type: 'warning' })
  await deleteCategory(id)
  ElMessage.success('分类已删除')
  loadData()
}

onMounted(loadData)
</script>
