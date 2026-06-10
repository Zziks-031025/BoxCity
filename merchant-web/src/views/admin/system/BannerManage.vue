<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Banner Manage</div>
        <h1 class="hero-title">轮播图配置支持前台效果感知</h1>
        <p class="hero-subtitle">
          在维护轮播图时，不仅能查看列表，还能理解图片、跳转链接和排序会如何影响小程序首页展示。
        </p>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">配置说明</div>
        <h3 class="card-title">轮播图直接影响首页首屏视觉</h3>
        <p class="card-desc">建议优先关注图片质量、跳转链接与排序顺序，避免后台已配好但前台效果不符合预期。</p>
      </div>
    </section>

    <div class="split-grid">
      <el-card class="table-card" shadow="never">
        <template #header>
          <div class="header-row">
            <div>
              <h3 class="section-title">轮播图列表</h3>
              <p class="section-subtitle">状态、排序和操作区已统一，不会出现按钮分布失衡。</p>
            </div>
            <el-button type="primary" @click="openDialog()">新增轮播图</el-button>
          </div>
        </template>
        <div class="table-scroll">
          <el-table :data="list" v-loading="loading" border>
            <el-table-column prop="id" label="ID" min-width="90" />
            <el-table-column label="图片" min-width="140">
              <template #default="{ row }">
                <el-image :src="row.imageUrl" fit="cover" class="banner-thumb" />
              </template>
            </el-table-column>
            <el-table-column prop="linkUrl" label="跳转链接" min-width="240" />
            <el-table-column prop="sort" label="排序" min-width="100" />
            <el-table-column label="状态" min-width="110">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
              </template>
            </el-table-column>
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

      <el-card class="preview-panel" shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">首页预览提示</h3>
            <p class="section-subtitle">帮助配置人员理解轮播图在小程序首页中的位置与作用。</p>
          </div>
        </template>
        <div class="preview-phone">
          <div class="preview-phone-top">
            <div>盒集首页</div>
            <div class="preview-title">轮播区域预览</div>
          </div>
          <div class="preview-phone-body">
            <div v-if="list[0]" class="mobile-preview-card">
              <el-image :src="list[0].imageUrl" class="preview-banner-image" fit="cover" />
              <div class="goods-inline-meta">当前首图会优先展示排序靠前且启用的轮播图。</div>
            </div>
            <div v-else class="empty-hint">还没有可预览的轮播图</div>
          </div>
        </div>
      </el-card>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑轮播图' : '新增轮播图'" width="560px">
      <el-form label-position="top">
        <el-form-item label="图片地址">
          <el-input v-model="form.imageUrl" />
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="form.linkUrl" />
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
import { createBanner, deleteBanner, getBannerList, updateBanner } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const dialogVisible = ref(false)
const pager = reactive({ page: 1, size: 10, total: 0 })
const form = reactive({
  id: null,
  imageUrl: '',
  linkUrl: '',
  sort: 0,
  status: 1
})
const enabled = computed({
  get: () => form.status === 1,
  set: (value) => {
    form.status = value ? 1 : 0
  }
})

async function loadData() {
  loading.value = true
  try {
    const res = await getBannerList({ page: pager.page, size: pager.size })
    list.value = res.records || []
    pager.total = res.total || 0
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, { id: null, imageUrl: '', linkUrl: '', sort: 0, status: 1 })
  }
  dialogVisible.value = true
}

async function submit() {
  const payload = {
    imageUrl: form.imageUrl,
    linkUrl: form.linkUrl,
    sort: form.sort,
    status: form.status
  }
  if (form.id) {
    await updateBanner(form.id, payload)
  } else {
    await createBanner(payload)
  }
  ElMessage.success('轮播图已保存')
  dialogVisible.value = false
  loadData()
}

async function remove(id) {
  await ElMessageBox.confirm('确认删除该轮播图吗？', '删除确认', { type: 'warning' })
  await deleteBanner(id)
  ElMessage.success('轮播图已删除')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.banner-thumb {
  width: 96px;
  height: 58px;
  border-radius: 14px;
}

.preview-title {
  margin-top: 8px;
  font-size: 20px;
  font-weight: 700;
}

.preview-banner-image {
  width: 100%;
  height: 160px;
  border-radius: 18px;
}
</style>
