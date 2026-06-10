<template>
  <div class="apply-shell">
    <section class="apply-panel">
      <div class="apply-panel-inner">
        <div class="apply-panel-tag">Merchant Onboarding</div>
        <h1 class="apply-panel-title">商家入驻申请</h1>
        <p class="apply-panel-subtitle">提交真实、可核验的资料后，平台会按现有流程完成审核。页面结构简化，但保留全部字段与提交逻辑。</p>

        <dl class="apply-panel-list">
          <div>
            <dt>店铺资料</dt>
            <dd>店铺名称、简介、公告会直接影响后续小程序展示。</dd>
          </div>
          <div>
            <dt>登录账号</dt>
            <dd>联系人手机号将作为商家后台登录账号。</dd>
          </div>
          <div>
            <dt>主体资质</dt>
            <dd>建议提前准备营业执照或身份证明图片链接。</dd>
          </div>
        </dl>
      </div>
    </section>

    <section class="apply-form-wrap">
      <el-card class="apply-form-card" shadow="never">
        <div class="apply-form-head">
          <h2>填写入驻资料</h2>
          <p>请按字段要求完整填写，平台会根据提交内容审核。</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <div class="form-grid">
            <el-form-item label="店铺名称" prop="shopName">
              <el-input v-model="form.shopName" placeholder="例如：金陵手作研究所" />
            </el-form-item>
            <el-form-item label="联系人手机号" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="将作为商家后台登录账号" />
            </el-form-item>
            <el-form-item label="联系邮箱">
              <el-input v-model="form.contactEmail" placeholder="用于接收审核通知和运营沟通" />
            </el-form-item>
            <el-form-item label="主体类型" prop="subjectType">
              <el-radio-group v-model="form.subjectType" class="subject-group">
                <el-radio :value="1">个人主体</el-radio>
                <el-radio :value="2">企业主体</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="登录密码" prop="password">
              <el-input v-model="form.password" type="password" show-password placeholder="请设置后台登录密码" />
            </el-form-item>
            <el-form-item label="营业执照 URL">
              <el-input v-model="form.licenseUrl" placeholder="企业主体请填写营业执照图片链接" />
            </el-form-item>
            <el-form-item label="店铺简介" class="span-2">
              <el-input
                v-model="form.shopIntro"
                type="textarea"
                :rows="4"
                placeholder="请简要介绍店铺定位、商品方向和经营特色"
              />
            </el-form-item>
            <el-form-item label="身份证正面 URL" class="span-2">
              <el-input v-model="form.idCardFront" placeholder="个人主体或法人身份证正面图片链接" />
            </el-form-item>
            <el-form-item label="身份证反面 URL" class="span-2">
              <el-input v-model="form.idCardBack" placeholder="个人主体或法人身份证反面图片链接" />
            </el-form-item>
          </div>

          <div class="panel-actions">
            <el-button @click="router.push('/login')">返回登录</el-button>
            <el-button type="primary" :loading="loading" @click="submit">提交申请</el-button>
          </div>
        </el-form>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { merchantApply } from '@/api/merchant'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({
  shopName: '',
  contactPhone: '',
  contactEmail: '',
  subjectType: 1,
  password: '',
  shopIntro: '',
  licenseUrl: '',
  idCardFront: '',
  idCardBack: '',
  legalPersonIdFront: '',
  legalPersonIdBack: ''
})

const rules = {
  shopName: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  subjectType: [{ required: true, message: '请选择主体类型', trigger: 'change' }],
  password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }]
}

async function submit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await merchantApply(form)
    ElMessage.success('申请已提交，请等待平台审核')
    router.push('/apply/status')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.apply-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(340px, 420px) minmax(0, 1fr);
  background: #f3f5f7;
}

.apply-panel {
  padding: 40px 32px;
  border-right: 1px solid var(--border-color);
  background: linear-gradient(180deg, #f9fbfc 0%, #f1f4f7 100%);
}

.apply-panel-inner {
  max-width: 320px;
}

.apply-panel-tag {
  display: inline-flex;
  padding: 4px 10px;
  border: 1px solid var(--border-color);
  color: var(--brand-primary-deep);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  background: #fff;
}

.apply-panel-title {
  margin: 18px 0 12px;
  font-size: 34px;
  line-height: 1.2;
  color: var(--text-primary);
}

.apply-panel-subtitle {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.9;
}

.apply-panel-list {
  margin: 24px 0 0;
  display: grid;
  gap: 18px;
}

.apply-panel-list dt {
  margin: 0 0 6px;
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
}

.apply-panel-list dd {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.8;
}

.apply-form-wrap {
  padding: 32px;
}

.apply-form-card {
  min-width: 0;
}

.apply-form-head h2 {
  margin: 0;
  font-size: 28px;
  color: var(--text-primary);
}

.apply-form-head p {
  margin: 10px 0 20px;
  color: var(--text-secondary);
  line-height: 1.8;
}

.subject-group {
  min-height: 40px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

.panel-actions {
  margin-top: 8px;
}

@media (max-width: 1100px) {
  .apply-shell {
    grid-template-columns: 1fr;
  }

  .apply-panel {
    border-right: 0;
    border-bottom: 1px solid var(--border-color);
  }
}
</style>
