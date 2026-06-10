<template>
  <div class="page-shell" v-loading="loading">
    <section v-if="detail" class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Goods Review Detail</div>
        <h1 class="hero-title">{{ detail.title }}</h1>
        <p class="hero-subtitle">
          审核详情将商品基础信息、图片、属性和审核动作拆开展示，避免图文内容和处理动作挤在一起。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">审核动作</div>
        <h3 class="card-title">确认商品信息是否适合前台直接展示</h3>
        <p class="card-desc">待审核状态下可以直接通过或驳回，并填写审核备注用于追踪沟通结果。</p>
        <div class="panel-actions">
          <el-button @click="router.back()">返回</el-button>
          <el-button v-if="detail?.auditStatus === 0" type="success" @click="submitAudit(true)">审核通过</el-button>
          <el-button v-if="detail?.auditStatus === 0" type="danger" @click="submitAudit(false)">驳回商品</el-button>
        </div>
      </div>
    </section>

    <div v-if="detail" class="detail-grid">
      <el-card shadow="never">
        <template #header><h3 class="section-title">基础信息</h3></template>
        <div class="data-list">
          <div class="data-row"><strong>商品标题</strong><span>{{ detail.title }}</span></div>
          <div class="data-row"><strong>所属店铺</strong><span>{{ detail.shopName }}</span></div>
          <div class="data-row"><strong>价格</strong><span>¥ {{ detail.price }}</span></div>
          <div class="data-row"><strong>库存</strong><span>{{ detail.stock }}</span></div>
          <div class="data-row"><strong>销量</strong><span>{{ detail.sales || 0 }}</span></div>
        </div>
      </el-card>

      <el-card shadow="never">
        <template #header><h3 class="section-title">审核信息</h3></template>
        <div class="data-list">
          <div class="data-row"><strong>审核状态</strong><span>{{ auditStatusText }}</span></div>
          <div class="data-row"><strong>审核备注</strong><span>{{ detail.auditRemark || '-' }}</span></div>
        </div>
      </el-card>

      <el-card class="span-2" shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">商品图片</h3>
            <p class="section-subtitle">图片网格会自动适配宽度，不会压缩到覆盖其他内容。</p>
          </div>
        </template>
        <div class="image-grid">
          <el-image
            v-for="item in detail?.images || []"
            :key="item.id"
            :src="item.imageUrl"
            fit="cover"
            class="image-item"
          />
        </div>
      </el-card>

      <el-card class="span-2" shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">商品属性</h3>
            <p class="section-subtitle">属性信息是后台录入到前台展示映射的重要一环。</p>
          </div>
        </template>
        <div class="table-scroll">
          <el-table :data="detail?.attributes || []" border>
            <el-table-column prop="attrName" label="属性名" min-width="180" />
            <el-table-column prop="attrValue" label="属性值" min-width="240" />
          </el-table>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { auditGoods, getGoodsAuditDetail } from '@/api/admin'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref(null)

const auditStatusText = computed(() =>
  detail.value?.auditStatus === 1 ? '已通过' : detail.value?.auditStatus === 2 ? '已驳回' : '待审核'
)

async function loadData() {
  loading.value = true
  try {
    detail.value = await getGoodsAuditDetail(route.params.id)
  } finally {
    loading.value = false
  }
}

async function submitAudit(approved) {
  const { value } = await ElMessageBox.prompt(`请输入${approved ? '通过' : '驳回'}备注`, '审核操作', {
    inputValue: detail.value?.auditRemark || '',
    confirmButtonText: '提交'
  })
  await auditGoods(route.params.id, { approved, remark: value || '' })
  ElMessage.success('审核结果已提交')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.span-2 {
  grid-column: 1 / -1;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 14px;
}

.image-item {
  width: 100%;
  height: 180px;
  border-radius: 18px;
}
</style>
