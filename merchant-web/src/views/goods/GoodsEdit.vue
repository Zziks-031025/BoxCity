<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Goods Editor</div>
        <h1 class="hero-title">{{ isEdit ? '编辑商品资料' : '发布新商品' }}</h1>
        <p class="hero-subtitle">
          商品标题、主图、详情图、价格、库存和属性会影响平台审核效率，也会直接影响小程序前台的商品展示效果。
        </p>
        <ul class="meta-list">
          <li>主图建议优先体现主题、材质或礼盒组合。</li>
          <li>属性会用于商品详情页展示，建议保持结构清晰。</li>
          <li>保存后可返回商品列表继续上架或修改状态。</li>
        </ul>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">前台展示提醒</div>
        <h3 class="card-title">后台录入越完整，前台展示越可信</h3>
        <p class="card-desc">
          本页的图片、简介、价格、库存和属性是商家端到小程序前台的重要映射内容，建议边录入边预览。
        </p>
      </div>
    </section>

    <div class="split-grid">
      <div class="card-stack">
        <el-card shadow="never">
          <template #header>
            <div>
              <h3 class="section-title">基础信息</h3>
              <p class="section-subtitle">先确定商品标题、分类、城市、价格和库存，再补充素材与说明。</p>
            </div>
          </template>
          <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
            <div class="form-grid">
              <el-form-item label="商品标题" prop="title" class="span-2">
                <el-input v-model="form.title" maxlength="100" show-word-limit placeholder="请输入商品标题" />
              </el-form-item>
              <el-form-item label="商品分类" prop="categoryId">
                <el-cascader
                  v-model="form.categoryId"
                  :options="categoryTree"
                  :props="{ value: 'id', label: 'name', children: 'children', emitPath: false, checkStrictly: true }"
                  clearable
                  style="width: 100%"
                />
              </el-form-item>
              <el-form-item label="发货城市" prop="cityId">
                <el-select v-model="form.cityId" filterable clearable style="width: 100%">
                  <el-option v-for="city in cityOptions" :key="city.id" :label="city.name" :value="city.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="售价" prop="price">
                <el-input-number v-model="form.price" :min="0" :precision="2" />
              </el-form-item>
              <el-form-item label="原价">
                <el-input-number v-model="form.originalPrice" :min="0" :precision="2" />
              </el-form-item>
              <el-form-item label="库存" prop="stock">
                <el-input-number v-model="form.stock" :min="0" />
              </el-form-item>
              <div class="guide-block">
                <div class="field-note">
                  <strong>录入建议：</strong>价格和库存会直接影响前台的购买决策与订单转化，避免出现空值和异常零库存。
                </div>
              </div>
            </div>
          </el-form>
        </el-card>

        <el-card shadow="never">
          <template #header>
            <div>
              <h3 class="section-title">展示素材</h3>
              <p class="section-subtitle">当前仍采用 URL 录入，但右侧会同步显示预览，避免只看文本难以判断内容。</p>
            </div>
          </template>
          <div class="form-grid">
            <el-form-item label="主图 URL" class="span-2">
              <el-input
                v-model="mainImageText"
                type="textarea"
                :rows="4"
                placeholder="每行一个主图 URL，用于商品卡片与详情页头图"
              />
            </el-form-item>
            <el-form-item label="详情图 URL" class="span-2">
              <el-input
                v-model="detailImageText"
                type="textarea"
                :rows="5"
                placeholder="每行一个详情图 URL，用于详情页内容展示"
              />
            </el-form-item>
          </div>
        </el-card>

        <el-card shadow="never">
          <template #header>
            <div>
              <h3 class="section-title">属性与描述</h3>
              <p class="section-subtitle">属性会进入前台商品详情页，建议使用简短、明确、方便展示的内容。</p>
            </div>
          </template>
          <div class="form-grid">
            <el-form-item label="商品属性" class="span-2">
              <el-input
                v-model="attributeText"
                type="textarea"
                :rows="3"
                placeholder="多个属性请用中文逗号或英文逗号分隔，例如：手工拼木、南京限定、礼盒包装"
              />
            </el-form-item>
            <el-form-item label="商品描述" class="span-2">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="5"
                placeholder="补充商品亮点、材质、适用场景或发货说明"
              />
            </el-form-item>
          </div>
          <div class="panel-actions">
            <el-button @click="router.push('/goods/list')">取消</el-button>
            <el-button type="primary" :loading="saving" @click="submit">保存商品</el-button>
          </div>
        </el-card>
      </div>

      <div class="preview-stack">
        <el-card class="preview-panel" shadow="never">
          <template #header>
            <div>
              <h3 class="section-title">前台卡片预览</h3>
              <p class="section-subtitle">模拟商品在小程序中的展示密度，帮助判断图文是否完整。</p>
            </div>
          </template>
          <div class="mobile-preview">
            <div class="mobile-preview-head">
              <div class="preview-head-label">店铺商品卡</div>
              <div class="preview-head-title">{{ form.title || '商品标题预览' }}</div>
            </div>
            <div class="mobile-preview-body">
              <div class="mobile-preview-card">
                <el-image v-if="mainPreviewImages[0]" :src="mainPreviewImages[0]" class="preview-cover" fit="cover" />
                <div v-else class="empty-visual">主图预览区</div>
                <div class="preview-price-row">
                  <strong>¥ {{ Number(form.price || 0).toFixed(2) }}</strong>
                  <span v-if="form.originalPrice">¥ {{ Number(form.originalPrice || 0).toFixed(2) }}</span>
                </div>
                <div class="preview-shop">{{ shopName }}</div>
                <div class="preview-city">{{ selectedCityName || '发货城市待填写' }}</div>
              </div>
            </div>
          </div>
        </el-card>

        <el-card shadow="never">
          <template #header>
            <div>
              <h3 class="section-title">图片与属性预览</h3>
              <p class="section-subtitle">用于提前检查详情图数量和属性是否适合前台展示。</p>
            </div>
          </template>
          <div class="preview-section">
            <div class="preview-label">主图预览</div>
            <div class="image-grid">
              <el-image
                v-for="(url, index) in mainPreviewImages"
                :key="`main-${index}`"
                :src="url"
                class="image-card"
                fit="cover"
              />
              <div v-if="!mainPreviewImages.length" class="empty-hint">还没有录入主图 URL</div>
            </div>
          </div>
          <div class="preview-section">
            <div class="preview-label">详情图预览</div>
            <div class="image-grid">
              <el-image
                v-for="(url, index) in detailPreviewImages"
                :key="`detail-${index}`"
                :src="url"
                class="image-card"
                fit="cover"
              />
              <div v-if="!detailPreviewImages.length" class="empty-hint">还没有录入详情图 URL</div>
            </div>
          </div>
          <div class="preview-section">
            <div class="preview-label">属性展示</div>
            <div class="attribute-wrap">
              <span v-for="item in attributeList" :key="item" class="attribute-chip">{{ item }}</span>
              <div v-if="!attributeList.length" class="empty-hint">还没有录入商品属性</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCategoryTree, getCityTree, getMerchantGoodsDetail, publishGoods, updateGoods } from '@/api/merchant'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const saving = ref(false)
const categoryTree = ref([])
const cityOptions = ref([])
const mainImageText = ref('')
const detailImageText = ref('')
const attributeText = ref('')
const shopName = ref(localStorage.getItem('merchant_shop_name') || '我的店铺')

const form = reactive({
  categoryId: undefined,
  title: '',
  description: '',
  price: 0,
  originalPrice: undefined,
  stock: 0,
  cityId: undefined
})

const isEdit = computed(() => Boolean(route.params.id))
const mainPreviewImages = computed(() => urlsFromText(mainImageText.value))
const detailPreviewImages = computed(() => urlsFromText(detailImageText.value))
const attributeList = computed(() =>
  attributeText.value
    .split(/[，,]/)
    .map((item) => item.trim())
    .filter(Boolean)
)
const selectedCityName = computed(() => cityOptions.value.find((item) => item.id === form.cityId)?.name || '')

const rules = {
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

function flattenCities(nodes, result = []) {
  for (const node of nodes || []) {
    if (node.level === 2) {
      result.push(node)
    }
    if (node.children?.length) {
      flattenCities(node.children, result)
    }
  }
  return result
}

function urlsFromText(text) {
  return text
    .split('\n')
    .map((item) => item.trim())
    .filter(Boolean)
}

function urlsToImages(text, type, sortOffset = 0) {
  return urlsFromText(text).map((imageUrl, index) => ({ imageUrl, type, sort: sortOffset + index }))
}

async function loadBaseData() {
  categoryTree.value = await getCategoryTree()
  cityOptions.value = flattenCities(await getCityTree())
}

async function loadDetail() {
  if (!isEdit.value) return
  const data = await getMerchantGoodsDetail(route.params.id)
  form.categoryId = data.categoryId
  form.title = data.title
  form.description = data.description
  form.price = Number(data.price || 0)
  form.originalPrice = data.originalPrice == null ? undefined : Number(data.originalPrice)
  form.stock = data.stock || 0
  form.cityId = data.cityId
  mainImageText.value = (data.images || []).filter((item) => item.type === 1).map((item) => item.imageUrl).join('\n')
  detailImageText.value = (data.images || []).filter((item) => item.type !== 1).map((item) => item.imageUrl).join('\n')
  attributeText.value = (data.attributes || [])
    .map((item) => item.attributeName || item.attrName || item.attributeValue || item.attrValue)
    .filter(Boolean)
    .join('，')
}

async function submit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    const images = [...urlsToImages(mainImageText.value, 1, 0), ...urlsToImages(detailImageText.value, 2, 100)]
    const payload = {
      ...form,
      images,
      attributes: attributeList.value
    }
    if (isEdit.value) {
      await updateGoods(route.params.id, payload)
      ElMessage.success('商品已更新')
    } else {
      await publishGoods(payload)
      ElMessage.success('商品已提交')
    }
    router.push('/goods/list')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await loadBaseData()
  await loadDetail()
})
</script>

<style scoped>
.guide-block {
  display: flex;
  align-items: flex-end;
}

.preview-cover {
  width: 100%;
  height: 180px;
  border-radius: 20px;
  background: #f6e1cc;
}

.empty-visual {
  display: grid;
  place-items: center;
  height: 180px;
  border-radius: 20px;
  background: linear-gradient(135deg, #f7c692 0%, #eb8452 100%);
  color: #fff8f1;
  font-weight: 700;
}

.preview-head-label {
  font-size: 12px;
  opacity: 0.82;
}

.preview-head-title {
  margin-top: 10px;
  font-size: 24px;
  font-weight: 700;
}

.preview-price-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: baseline;
  margin-top: 14px;
}

.preview-price-row strong {
  font-size: 28px;
  color: #cb582d;
}

.preview-price-row span {
  color: #a18d7e;
  text-decoration: line-through;
}

.preview-shop,
.preview-city {
  margin-top: 10px;
  color: #7f6858;
}

.preview-section {
  display: grid;
  gap: 12px;
}

.preview-section + .preview-section {
  margin-top: 18px;
}

.preview-label {
  font-size: 13px;
  color: #7f6858;
  font-weight: 600;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.image-card {
  width: 100%;
  height: 140px;
  border-radius: 18px;
}

.attribute-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.attribute-chip {
  display: inline-flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 999px;
  background: #fff1e6;
  color: #cb582d;
  font-size: 13px;
  font-weight: 600;
}

@media (max-width: 640px) {
  .image-grid {
    grid-template-columns: 1fr;
  }
}
</style>
