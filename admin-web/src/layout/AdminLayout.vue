<template>
  <el-container class="admin-layout app-shell">
    <el-aside width="272px" class="admin-aside">
      <div class="admin-brand">
        <div class="admin-brand-mark">BC</div>
        <div class="admin-brand-copy">
          <div class="admin-brand-title">盒集平台后台</div>
          <div class="admin-brand-subtitle">审核、监控与系统配置统一管理</div>
        </div>
      </div>

      <div class="admin-side-summary">
        <div class="admin-side-summary-label">当前账号</div>
        <div class="admin-side-summary-name">{{ adminName }}</div>
        <div class="admin-side-summary-tip">
          与商家端使用同一套传统后台框架，只在业务内容和管理权限上区分。
        </div>
      </div>

      <el-scrollbar class="admin-menu-scroll">
        <el-menu :default-active="route.path" router class="admin-menu">
          <el-menu-item index="/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>工作台</span>
          </el-menu-item>

          <el-sub-menu index="user">
            <template #title>
              <el-icon><UserFilled /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/user/list">用户列表</el-menu-item>
            <el-menu-item index="/user/report">举报处理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="merchant">
            <template #title>
              <el-icon><Shop /></el-icon>
              <span>商家与商品</span>
            </template>
            <el-menu-item index="/merchant/audit">商家审核</el-menu-item>
            <el-menu-item index="/merchant/list">商家列表</el-menu-item>
            <el-menu-item index="/goods/audit">商品审核</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="order">
            <template #title>
              <el-icon><List /></el-icon>
              <span>订单治理</span>
            </template>
            <el-menu-item index="/order/monitor">订单监控</el-menu-item>
            <el-menu-item index="/order/dispute">纠纷处理</el-menu-item>
            <el-menu-item index="/order/abnormal">异常订单</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统配置</span>
            </template>
            <el-menu-item index="/system/category">分类管理</el-menu-item>
            <el-menu-item index="/system/city">城市管理</el-menu-item>
            <el-menu-item index="/system/payment">支付配置</el-menu-item>
            <el-menu-item index="/system/freight">全局运费</el-menu-item>
            <el-menu-item index="/system/banner">轮播图管理</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container class="admin-main-shell">
      <el-header class="admin-topbar">
        <div class="admin-topbar-copy">
          <div class="admin-topbar-title">{{ pageTitle }}</div>
          <p class="admin-topbar-subtitle">{{ pageSubtitle }}</p>
        </div>

        <div class="admin-topbar-actions">
          <div class="admin-account">
            <span class="admin-account-label">当前账号</span>
            <span class="admin-account-name">{{ adminName }}</span>
          </div>
          <el-button plain @click="logout">退出登录</el-button>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { DataAnalysis, List, Setting, Shop, UserFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const adminName = computed(() => localStorage.getItem('admin_name') || '平台管理员')
const pageTitle = computed(() => route.meta?.title || '平台后台')
const pageSubtitle = computed(
  () => route.meta?.subtitle || '统一使用传统后台布局，强化审核、监控与配置页面的可读性。'
)

function logout() {
  localStorage.removeItem('admin_token')
  localStorage.removeItem('admin_name')
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.admin-aside {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px 0;
  background: var(--sidebar-gradient);
  color: #fff8f2;
}

.admin-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 16px 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.admin-brand-mark {
  width: 40px;
  height: 40px;
  display: grid;
  place-items: center;
  background: #d06b34;
  color: #fff9f4;
  font-size: 16px;
  font-weight: 800;
}

.admin-brand-copy {
  min-width: 0;
}

.admin-brand-title {
  font-size: 18px;
  font-weight: 700;
}

.admin-brand-subtitle {
  margin-top: 4px;
  font-size: 12px;
  color: rgba(255, 248, 240, 0.68);
}

.admin-side-summary {
  padding: 0 16px 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.admin-side-summary-label {
  font-size: 12px;
  color: rgba(255, 248, 240, 0.72);
}

.admin-side-summary-name {
  margin-top: 8px;
  font-size: 16px;
  font-weight: 700;
}

.admin-side-summary-tip {
  margin-top: 8px;
  font-size: 12px;
  line-height: 1.7;
  color: rgba(255, 248, 240, 0.72);
}

.admin-menu-scroll {
  flex: 1;
  min-height: 0;
}

.admin-menu {
  padding-left: 1px;
  border-right: none;
  background: transparent;
}

.admin-menu :deep(.el-menu) {
  background: transparent;
}

.admin-menu :deep(.el-menu-item),
.admin-menu :deep(.el-sub-menu__title) {
  margin: 0;
  min-height: 42px;
  color: rgba(255, 249, 242, 0.84);
  border-left: 3px solid transparent;
}

.admin-menu :deep(.el-menu-item:hover),
.admin-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  border-left-color: #d06b34;
}

.admin-menu :deep(.el-sub-menu .el-menu-item) {
  padding-left: 52px !important;
}

.admin-main-shell {
  min-width: 0;
}

.admin-topbar {
  display: flex;
  align-items: flex-start;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 12px;
  min-height: auto;
  padding: 18px 20px 10px;
  background: var(--bg-panel);
  border-bottom: 1px solid var(--border-color);
}

.admin-topbar-copy {
  min-width: 0;
}

.admin-topbar-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
}

.admin-topbar-subtitle {
  margin: 6px 0 0;
  max-width: 720px;
  color: var(--text-secondary);
  line-height: 1.6;
  font-size: 13px;
}

.admin-topbar-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
}

.admin-account {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary);
  font-size: 13px;
}

.admin-account-label {
  color: var(--text-tertiary);
}

.admin-account-name {
  font-weight: 700;
  color: var(--text-primary);
}

.admin-main {
  min-width: 0;
  padding: 16px 20px 20px;
}

@media (max-width: 1080px) {
  .admin-layout {
    flex-direction: column;
  }

  .admin-aside {
    width: 100% !important;
  }
}

@media (max-width: 640px) {
  .admin-topbar,
  .admin-main {
    padding-inline: 16px;
  }
}
</style>
