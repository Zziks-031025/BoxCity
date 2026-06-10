<template>
  <div class="login-page">
    <div class="login-side">
      <div class="login-side-content">
        <div class="login-side-tag">Heji Console</div>
        <h1 class="login-side-title">盒集统一后台</h1>
        <p class="login-side-text">使用账号密码登录，系统会自动识别身份并进入对应界面。</p>
      </div>
    </div>

    <div class="login-main">
      <div class="login-box">
        <div class="login-head">
          <h2>登录</h2>
          <p>商家使用手机号，管理员使用账号。</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="submit">
          <el-form-item label="账号" prop="account">
            <el-input v-model="form.account" placeholder="请输入手机号或管理员账号" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
          </el-form-item>

          <el-button type="primary" class="submit-btn" :loading="loading" @click="submit">登录系统</el-button>
        </el-form>

        <div class="login-footer">
          <span>还没有商家账号？</span>
          <el-button link type="primary" @click="router.push('/apply')">申请入驻</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminLogin } from '@/api/admin'
import { merchantLogin } from '@/api/merchant'
import { getRoleHomePath, storeAdminSession, storeMerchantSession, USER_ROLE_ADMIN, USER_ROLE_MERCHANT } from '@/utils/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({
  account: '',
  password: ''
})

const rules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const ADMIN_CREDENTIAL_ERROR = '账号或密码错误'
const MERCHANT_CREDENTIAL_ERROR = '手机号或密码错误'

function looksLikePhone(value) {
  return /^1\d{10}$/.test(value)
}

async function tryMerchantLogin() {
  const data = await merchantLogin(
    {
      contactPhone: form.account,
      password: form.password
    },
    { silent: true }
  )
  storeMerchantSession(data)
  return USER_ROLE_MERCHANT
}

async function tryAdminLogin() {
  const data = await adminLogin(
    {
      username: form.account,
      password: form.password
    },
    { silent: true }
  )
  storeAdminSession(data, form.account)
  return USER_ROLE_ADMIN
}

async function loginByRole() {
  if (looksLikePhone(form.account)) {
    try {
      return await tryMerchantLogin()
    } catch (error) {
      if (error?.message !== MERCHANT_CREDENTIAL_ERROR) {
        throw error
      }
      try {
        return await tryAdminLogin()
      } catch (fallbackError) {
        if (fallbackError?.message !== ADMIN_CREDENTIAL_ERROR) {
          throw fallbackError
        }
        throw error
      }
    }
  }

  try {
    return await tryAdminLogin()
  } catch (error) {
    if (error?.message !== ADMIN_CREDENTIAL_ERROR) {
      throw error
    }
    try {
      return await tryMerchantLogin()
    } catch (fallbackError) {
      if (fallbackError?.message !== MERCHANT_CREDENTIAL_ERROR) {
        throw fallbackError
      }
      throw error
    }
  }
}

async function submit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  loading.value = true
  try {
    const role = await loginByRole()
    ElMessage.success('登录成功')
    router.push(getRoleHomePath(role))
  } catch (error) {
    ElMessage.error(error.message || '账号或密码错误')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(320px, 1fr) 420px;
  background: var(--bg-page);
}

.login-side {
  display: flex;
  align-items: center;
  padding: 56px;
  background: linear-gradient(180deg, #fff8ee 0%, #f6efe6 100%);
  border-right: 1px solid var(--border-color);
}

.login-side-content {
  max-width: 520px;
}

.login-side-tag {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 10px;
  border: 1px solid var(--border-color);
  color: var(--brand-primary-deep);
  background: #fff;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

.login-side-title {
  margin: 18px 0 12px;
  font-size: 34px;
  color: var(--text-primary);
}

.login-side-text {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.8;
}

.login-main {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.login-box {
  width: 100%;
  padding: 28px;
  border: 1px solid var(--border-color);
  background: var(--bg-panel);
}

.login-head h2 {
  margin: 0;
  font-size: 28px;
  color: var(--text-primary);
}

.login-head p {
  margin: 8px 0 20px;
  color: var(--text-secondary);
}

.submit-btn {
  width: 100%;
}

.login-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 18px;
  color: var(--text-secondary);
  font-size: 13px;
}

@media (max-width: 960px) {
  .login-page {
    grid-template-columns: 1fr;
  }

  .login-side {
    padding: 32px 24px 20px;
    border-right: 0;
    border-bottom: 1px solid var(--border-color);
  }

  .login-main {
    padding: 20px;
  }
}
</style>
