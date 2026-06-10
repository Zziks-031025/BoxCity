<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Dispute Center</div>
        <h1 class="hero-title">平台纠纷处理台</h1>
        <p class="hero-subtitle">
          纠纷页重点展示订单、店铺、商品、退款金额与申请原因，方便平台快速进入仲裁处理。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">处理建议</div>
        <h3 class="card-title">处理结果应明确站在买家或商家一侧</h3>
        <p class="card-desc">仲裁备注会保留在平台治理链路中，用于后续追踪和复盘。</p>
      </div>
    </section>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <h3 class="section-title">纠纷列表</h3>
            <p class="section-subtitle">高风险金额和原因已经优先展示，便于平台快速判断。</p>
          </div>
          <el-button type="primary" @click="loadData">刷新列表</el-button>
        </div>
      </template>
      <div class="table-scroll">
        <el-table :data="list" v-loading="loading" border>
          <el-table-column prop="id" label="售后 ID" min-width="100" />
          <el-table-column prop="orderNo" label="订单号" min-width="200" />
          <el-table-column prop="shopName" label="店铺" min-width="180" />
          <el-table-column prop="goodsTitle" label="商品" min-width="220" />
          <el-table-column prop="refundReason" label="售后原因" min-width="220" />
          <el-table-column label="退款金额" min-width="120">
            <template #default="{ row }"><strong>¥ {{ Number(row.refundAmount || 0).toFixed(2) }}</strong></template>
          </el-table-column>
          <el-table-column prop="createdAt" label="申请时间" min-width="180" />
          <el-table-column label="操作" min-width="140" fixed="right">
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link type="primary" @click="openDialog(row)">仲裁处理</el-button>
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

    <el-dialog v-model="dialogVisible" title="平台纠纷处理" width="560px">
      <el-form label-position="top">
        <el-form-item label="仲裁结果">
          <el-radio-group v-model="form.approve">
            <el-radio :label="true">支持买家，执行退款</el-radio>
            <el-radio :label="false">驳回介入，关闭售后</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input v-model="form.remark" type="textarea" :rows="4" placeholder="请填写仲裁说明" />
        </el-form-item>
      </el-form>
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
import { getDisputeList, handleDispute } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const dialogVisible = ref(false)
const currentId = ref(null)
const pager = reactive({ page: 1, size: 10, total: 0 })
const form = reactive({
  approve: true,
  remark: ''
})

async function loadData() {
  loading.value = true
  try {
    const res = await getDisputeList({ page: pager.page, size: pager.size })
    list.value = res.records || []
    pager.total = res.total || 0
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  currentId.value = row.id
  form.approve = true
  form.remark = row.rejectReason || ''
  dialogVisible.value = true
}

async function submitHandle() {
  await handleDispute(currentId.value, {
    approve: form.approve,
    remark: form.remark || ''
  })
  ElMessage.success('仲裁结果已提交')
  dialogVisible.value = false
  loadData()
}

onMounted(loadData)
</script>
