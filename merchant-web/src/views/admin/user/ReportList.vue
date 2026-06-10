<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Report Center</div>
        <h1 class="hero-title">举报处理列表</h1>
        <p class="hero-subtitle">
          举报页统一了状态、处理结果和操作区结构，适合平台快速判断和填写处理结果。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">处理原则</div>
        <h3 class="card-title">尽量给出明确结论并保留处理说明</h3>
        <p class="card-desc">处理结果会作为后续风控和用户治理的重要依据。</p>
      </div>
    </section>

    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <h3 class="section-title">筛选条件</h3>
            <p class="section-subtitle">按处理状态快速筛出待处理举报。</p>
          </div>
          <el-button type="primary" @click="loadData">查询</el-button>
        </div>
      </template>
      <div class="toolbar">
        <div class="filters">
          <el-select v-model="filters.status" placeholder="处理状态" clearable>
            <el-option label="待处理" :value="0" />
            <el-option label="已处理" :value="1" />
          </el-select>
        </div>
      </div>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div>
          <h3 class="section-title">举报列表</h3>
          <p class="section-subtitle">状态、结果和操作区使用统一层次，不会出现按钮分布不均的问题。</p>
        </div>
      </template>
      <div class="table-scroll">
        <el-table :data="list" v-loading="loading" border>
          <el-table-column prop="id" label="ID" min-width="90" />
          <el-table-column prop="reporterName" label="举报人" min-width="140" />
          <el-table-column prop="targetType" label="目标类型" min-width="120" />
          <el-table-column prop="targetId" label="目标 ID" min-width="100" />
          <el-table-column prop="reason" label="举报原因" min-width="240" />
          <el-table-column label="状态" min-width="120">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'warning'">{{ row.status === 1 ? '已处理' : '待处理' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="handleResult" label="处理结果" min-width="240" />
          <el-table-column prop="createdAt" label="提交时间" min-width="180" />
          <el-table-column label="操作" min-width="120" fixed="right">
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link type="primary" :disabled="row.status === 1" @click="openDialog(row)">处理</el-button>
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

    <el-dialog v-model="dialogVisible" title="处理举报" width="560px">
      <el-input v-model="handleResult" type="textarea" :rows="4" placeholder="请输入处理结果" />
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">提交结果</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getReportList, handleReport } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const dialogVisible = ref(false)
const currentId = ref(null)
const handleResult = ref('')
const filters = reactive({ status: null })
const pager = reactive({ page: 1, size: 10, total: 0 })

async function loadData() {
  loading.value = true
  try {
    const res = await getReportList({
      status: filters.status,
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
  currentId.value = row.id
  handleResult.value = row.handleResult || ''
  dialogVisible.value = true
}

async function submitHandle() {
  if (!handleResult.value.trim()) {
    ElMessage.warning('请输入处理结果')
    return
  }
  await handleReport(currentId.value, handleResult.value.trim())
  ElMessage.success('处理成功')
  dialogVisible.value = false
  loadData()
}

onMounted(loadData)
</script>
