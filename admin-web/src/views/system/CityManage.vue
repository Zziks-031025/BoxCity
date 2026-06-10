<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">City Manage</div>
        <h1 class="hero-title">城市配置统一成“说明区 + 列表区 + 编辑区”结构</h1>
        <p class="hero-subtitle">
          城市层级会影响商家发货城市和用户地址的选择结果，因此需要更稳定的层级与排序管理页面。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">配置说明</div>
        <h3 class="card-title">城市层级与排序会影响前台选择顺序</h3>
        <p class="card-desc">建议在维护城市时同步关注层级、父级关系和排序值。</p>
      </div>
    </section>

    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <h3 class="section-title">筛选条件</h3>
            <p class="section-subtitle">支持按城市名称和层级快速定位。</p>
          </div>
          <div class="panel-actions">
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button @click="openDialog()">新增城市</el-button>
          </div>
        </div>
      </template>
      <div class="toolbar">
        <div class="filters">
          <el-input v-model="filters.keyword" placeholder="城市名称" clearable @keyup.enter="loadData" />
          <el-select v-model="filters.level" placeholder="级别" clearable>
            <el-option label="省" :value="1" />
            <el-option label="市" :value="2" />
            <el-option label="区县" :value="3" />
          </el-select>
        </div>
      </div>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div>
          <h3 class="section-title">城市列表</h3>
          <p class="section-subtitle">保留后台信息密度，但通过统一布局降低拥挤感。</p>
        </div>
      </template>
      <div class="table-scroll">
        <el-table :data="list" v-loading="loading" border>
          <el-table-column prop="id" label="ID" min-width="90" />
          <el-table-column prop="name" label="城市名称" min-width="180" />
          <el-table-column prop="parentId" label="父级 ID" min-width="110" />
          <el-table-column label="级别" min-width="100">
            <template #default="{ row }">{{ { 1: '省', 2: '市', 3: '区县' }[row.level] || '-' }}</template>
          </el-table-column>
          <el-table-column prop="sort" label="排序" min-width="100" />
          <el-table-column prop="createdAt" label="创建时间" min-width="180" />
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
      <el-pagination
        v-model:current-page="pager.page"
        v-model:page-size="pager.size"
        layout="total, prev, pager, next"
        :total="pager.total"
        @current-change="loadData"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑城市' : '新增城市'" width="520px">
      <el-form label-position="top">
        <div class="form-grid">
          <el-form-item label="城市名称" class="span-2">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="父级 ID">
            <el-input-number v-model="form.parentId" :min="0" />
          </el-form-item>
          <el-form-item label="级别">
            <el-select v-model="form.level">
              <el-option label="省" :value="1" />
              <el-option label="市" :value="2" />
              <el-option label="区县" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="排序" class="span-2">
            <el-input-number v-model="form.sort" :min="0" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createCity, deleteCity, getCityList, updateCity } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const dialogVisible = ref(false)
const filters = reactive({ keyword: '', level: null })
const pager = reactive({ page: 1, size: 20, total: 0 })
const form = reactive({
  id: null,
  parentId: 0,
  name: '',
  level: 1,
  sort: 0
})

async function loadData() {
  loading.value = true
  try {
    const res = await getCityList({
      keyword: filters.keyword || undefined,
      level: filters.level,
      page: pager.page,
      size: pager.size
    })
    list.value = res.records || []
    pager.total = res.total || 0
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  if (row) {
    form.id = row.id
    form.parentId = row.parentId || 0
    form.name = row.name
    form.level = row.level
    form.sort = row.sort || 0
  } else {
    form.id = null
    form.parentId = 0
    form.name = ''
    form.level = 1
    form.sort = 0
  }
  dialogVisible.value = true
}

async function submit() {
  const payload = {
    parentId: form.parentId,
    name: form.name,
    level: form.level,
    sort: form.sort
  }
  if (form.id) {
    await updateCity(form.id, payload)
  } else {
    await createCity(payload)
  }
  ElMessage.success('城市已保存')
  dialogVisible.value = false
  loadData()
}

async function remove(id) {
  await ElMessageBox.confirm('确认删除该城市吗？', '删除确认', { type: 'warning' })
  await deleteCity(id)
  ElMessage.success('城市已删除')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.span-2 {
  grid-column: 1 / -1;
}
</style>
