<template>
  <div class="page-shell">
    <section class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Shop Branding</div>
        <h1 class="hero-title">店铺资料将直接影响小程序店铺页展示</h1>
        <p class="hero-subtitle">
          头像、简介、公告和联系方式是用户进入店铺后最先看到的信息。这里的修改会同步影响前台品牌感和信任感。
        </p>
        <ul class="meta-list">
          <li>店铺头像优先用于小程序店铺页和商品详情页的店铺信息区。</li>
          <li>店铺简介更适合承接品牌定位、经营方向和主打特色。</li>
          <li>公告适合展示发货节奏、节假日安排或售后提醒。</li>
        </ul>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">同步提醒</div>
        <h3 class="card-title">保存后将作为前台店铺资料源</h3>
        <p class="card-desc">
          如果头像为空，前台会退回到首字母占位；因此建议优先补齐真实头像与公告，让店铺更完整。
        </p>
      </div>
    </section>

    <div class="split-grid">
      <el-card shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">品牌资料编辑</h3>
            <p class="section-subtitle">只读字段和可编辑字段已分层处理，避免整页看起来像默认表单堆叠。</p>
          </div>
        </template>
        <el-form ref="formRef" :model="form" label-position="top">
          <div class="form-grid">
            <el-form-item label="店铺名称" class="span-2">
              <el-input v-model="form.shopName" placeholder="请输入店铺名称" />
            </el-form-item>
            <el-form-item label="店铺头像 URL" class="span-2">
              <el-input v-model="form.shopAvatar" placeholder="建议填写清晰的方形头像图片链接" />
            </el-form-item>
            <el-form-item label="店铺简介" class="span-2">
              <el-input
                v-model="form.shopIntro"
                type="textarea"
                :rows="4"
                placeholder="用于介绍店铺定位、商品风格和运营理念"
              />
            </el-form-item>
            <el-form-item label="店铺公告" class="span-2">
              <el-input
                v-model="form.shopNotice"
                type="textarea"
                :rows="4"
                placeholder="可填写节假日安排、发货说明和售后提醒"
              />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="form.contactPhone" disabled />
            </el-form-item>
            <el-form-item label="联系邮箱">
              <el-input v-model="form.contactEmail" placeholder="用于接收平台通知和合作沟通" />
            </el-form-item>
          </div>
          <div class="panel-actions">
            <el-button type="primary" :loading="saving" @click="submit">保存店铺资料</el-button>
          </div>
        </el-form>
      </el-card>

      <div class="preview-stack">
        <el-card class="preview-panel" shadow="never">
          <template #header>
            <div>
              <h3 class="section-title">小程序展示预览</h3>
              <p class="section-subtitle">帮助你直观看到本页配置对前台店铺页的影响。</p>
            </div>
          </template>
          <div class="preview-phone">
            <div class="preview-phone-top">
              <div class="preview-phone-title">店铺首页头部</div>
              <div class="preview-phone-subtitle">{{ form.shopName || '店铺名称预览' }}</div>
            </div>
            <div class="preview-phone-body">
              <div class="shop-preview-header">
                <el-image v-if="form.shopAvatar" :src="form.shopAvatar" class="preview-avatar" fit="cover" />
                <div v-else class="preview-avatar preview-placeholder">
                  {{ avatarInitial }}
                </div>
                <div class="shop-preview-copy">
                  <strong>{{ form.shopName || '店铺名称预览' }}</strong>
                  <span>{{ form.contactPhone || '联系电话' }}</span>
                </div>
              </div>
              <div class="mobile-preview-card">
                <div class="preview-label">店铺简介</div>
                <div class="muted-text">{{ form.shopIntro || '这里会显示店铺简介内容。' }}</div>
              </div>
              <div class="mobile-preview-card">
                <div class="preview-label">店铺公告</div>
                <div class="muted-text">{{ form.shopNotice || '这里会显示店铺公告内容。' }}</div>
              </div>
            </div>
          </div>
        </el-card>

        <el-card shadow="never">
          <template #header>
            <div>
              <h3 class="section-title">维护建议</h3>
              <p class="section-subtitle">这些建议对应清单中“后台配置到前台展示”的重点映射要求。</p>
            </div>
          </template>
          <ul class="card-list">
            <li>头像建议使用稳定的图片地址，避免前台读取失败。</li>
            <li>简介适合写店铺定位，公告适合写短期动态或提醒。</li>
            <li>保存后可回到小程序店铺页检查真实展示效果。</li>
          </ul>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getShopInfo, updateShopInfo } from '@/api/merchant'

const formRef = ref()
const saving = ref(false)
const form = reactive({
  shopName: '',
  shopAvatar: '',
  shopIntro: '',
  shopNotice: '',
  contactPhone: '',
  contactEmail: ''
})

const avatarInitial = computed(() => (form.shopName || '盒').slice(0, 1))

async function loadInfo() {
  Object.assign(form, await getShopInfo())
}

async function submit() {
  saving.value = true
  try {
    await updateShopInfo(form)
    localStorage.setItem('merchant_shop_name', form.shopName || '我的店铺')
    ElMessage.success('店铺资料已保存')
  } finally {
    saving.value = false
  }
}

onMounted(loadInfo)
</script>

<style scoped>
.preview-phone-title {
  font-size: 12px;
  opacity: 0.82;
}

.preview-phone-subtitle {
  margin-top: 8px;
  font-size: 22px;
  font-weight: 700;
}

.shop-preview-header {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 14px;
  align-items: center;
}

.shop-preview-copy {
  display: grid;
  gap: 6px;
}

.shop-preview-copy strong {
  font-size: 18px;
}

.shop-preview-copy span {
  color: #7f6858;
}

.preview-label {
  margin-bottom: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #7f6858;
}
</style>
