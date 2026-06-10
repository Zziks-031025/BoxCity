<template>
  <div class="auth-shell">
    <section class="auth-panel">
      <div class="auth-panel-inner">
        <div class="auth-panel-tag">Platform Console</div>
        <h1 class="auth-panel-title">盒集平台后台</h1>
        <p class="auth-panel-subtitle">统一处理审核、订单治理与系统配置，沿用与商家端一致的传统后台框架。</p>

        <dl class="auth-panel-list">
          <div>
            <dt>审核工作</dt>
            <dd>处理商家入驻、商品审核与平台结论。</dd>
          </div>
          <div>
            <dt>订单治理</dt>
            <dd>监控订单、纠纷与异常订单问题。</dd>
          </div>
          <div>
            <dt>系统配置</dt>
            <dd>维护分类、城市、轮播图、支付和运费配置。</dd>
          </div>
        </dl>
      </div>
    </section>

    <section class="auth-form-wrap">
      <el-card class="auth-form-card" shadow="never">
        <div class="auth-form-head">
          <h2>管理员登录</h2>
          <p>请输入平台管理员账号和密码。</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="submit">
          <el-form-item label="账号" prop="username">
            <el-input v-model="form.username" placeholder="请输入管理员账号" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入登录密码" />
          </el-form-item>

          <div class="auth-tip">默认账号可参考初始化脚本中的平台管理员信息。</div>

          <el-button type="primary" class="submit-btn" :loading="loading" @click="submit">
            登录平台后台
          </el-button>
        </el-form>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminLogin } from '@/api/admin'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: 'admin',
  password: 'admin123'
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function submit() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await adminLogin(form)
      localStorage.setItem('admin_token', res.token)
      localStorage.setItem('admin_name', res.username)
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.auth-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 460px;
  background: #f3f5f7;
}

.auth-panel {
  display: flex;
  align-items: center;
  padding: 56px;
  border-right: 1px solid var(--border-color);
  background: linear-gradient(180deg, #f9fbfc 0%, #f1f4f7 100%);
}

.auth-panel-inner {
  max-width: 560px;
}

.auth-panel-tag {
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

.auth-panel-title {
  margin: 18px 0 12px;
  font-size: 36px;
  line-height: 1.2;
  color: var(--text-primary);
}

.auth-panel-subtitle {
  margin: 0;
  color: var(--text-secondary);
  font-size: 15px;
  line-height: 1.9;
}

.auth-panel-list {
  margin: 28px 0 0;
  display: grid;
  gap: 18px;
}

.auth-panel-list dt {
  margin: 0 0 6px;
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
}

.auth-panel-list dd {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.8;
}

.auth-form-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
}

.auth-form-card {
  width: 100%;
}

.auth-form-head h2 {
  margin: 0;
  font-size: 28px;
  color: var(--text-primary);
}

.auth-form-head p {
  margin: 10px 0 20px;
  color: var(--text-secondary);
  line-height: 1.8;
}

.auth-tip {
  margin-bottom: 16px;
  color: var(--text-secondary);
  font-size: 13px;
}

.submit-btn {
  width: 100%;
}

@media (max-width: 960px) {
  .auth-shell {
    grid-template-columns: 1fr;
  }

  .auth-panel {
    padding: 32px 24px 24px;
    border-right: 0;
    border-bottom: 1px solid var(--border-color);
  }

  .auth-form-wrap {
    padding: 24px;
  }
}
</style>
