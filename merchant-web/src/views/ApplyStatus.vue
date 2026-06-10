<template>
  <div class="status-shell">
    <el-card class="status-card" shadow="never">
      <el-result
        icon="success"
        title="申请已提交"
        sub-title="平台审核通过后，你就可以直接使用商家账号登录后台。如审核被驳回，可根据提示补充资料后重新提交。"
      >
        <template #extra>
          <div class="result-actions">
            <el-button type="primary" @click="router.push('/login')">返回登录</el-button>
            <el-button @click="router.push('/apply')">重新填写资料</el-button>
          </div>
        </template>
      </el-result>

      <div class="summary-table">
        <div class="summary-row">
          <div class="summary-label">审核流程</div>
          <div class="summary-value">平台校验主体信息、店铺信息与基础资质链接。</div>
        </div>
        <div class="summary-row">
          <div class="summary-label">结果通知</div>
          <div class="summary-value">审核通过后即可使用联系人手机号登录商家后台。</div>
        </div>
        <div class="summary-row">
          <div class="summary-label">后续准备</div>
          <div class="summary-value">建议提前准备店铺头像、商品图片、发货地址和运费模板。</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.status-shell {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
  background: #f3f5f7;
}

.status-card {
  width: min(860px, 100%);
}

.result-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px;
}

.summary-table {
  margin-top: 8px;
  border-top: 1px solid var(--border-color);
}

.summary-row {
  display: grid;
  grid-template-columns: 160px 1fr;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid var(--border-color);
}

.summary-label {
  font-weight: 700;
  color: var(--text-primary);
}

.summary-value {
  color: var(--text-secondary);
  line-height: 1.8;
}

@media (max-width: 640px) {
  .summary-row {
    grid-template-columns: 1fr;
    gap: 8px;
  }
}
</style>
