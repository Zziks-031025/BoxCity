<template>
  <el-container class="app-shell unified-layout">
    <el-aside width="220px" class="shell-aside">
      <div class="shell-brand">
        <div class="shell-brand-mark">HJ</div>
        <div class="shell-brand-copy">
          <div class="shell-brand-title">盒集后台</div>
          <div class="shell-brand-subtitle">{{ roleLabel }}</div>
        </div>
      </div>

      <el-scrollbar class="shell-menu-scroll">
        <el-menu :default-active="activeMenu" router class="shell-menu">
          <template v-for="section in menuSections" :key="section.key">
            <el-sub-menu v-if="section.children?.length" :index="section.key">
              <template #title>
                <el-icon><component :is="section.icon" /></el-icon>
                <span>{{ section.label }}</span>
              </template>
              <el-menu-item v-for="item in section.children" :key="item.path" :index="item.path">
                {{ item.label }}
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item v-else :index="section.path">
              <el-icon><component :is="section.icon" /></el-icon>
              <span>{{ section.label }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container class="shell-main">
      <el-header class="shell-header">
        <div class="shell-header-main">
          <h1 class="shell-title">{{ pageTitle }}</h1>
        </div>
        <div class="shell-header-side">
          <div class="shell-account">
            <span class="shell-account-role">{{ roleLabel }}</span>
            <span class="shell-account-name">{{ displayName }}</span>
          </div>
          <el-button plain @click="logout">退出登录</el-button>
        </div>
      </el-header>

      <el-main class="shell-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Box, DataAnalysis, List, Monitor, Setting, Shop, User } from '@element-plus/icons-vue'
import { clearAuth, getDisplayName, getRoleLabel, getUserRole, USER_ROLE_ADMIN } from '@/utils/auth'

const route = useRoute()
const router = useRouter()

const merchantMenu = [
  { key: 'merchant-dashboard', label: '工作台', path: '/dashboard', icon: DataAnalysis },
  {
    key: 'merchant-goods',
    label: '商品管理',
    icon: Box,
    children: [
      { label: '商品列表', path: '/goods/list' },
      { label: '发布商品', path: '/goods/edit' },
      { label: '库存管理', path: '/goods/stock' }
    ]
  },
  {
    key: 'merchant-order',
    label: '订单管理',
    icon: List,
    children: [
      { label: '订单列表', path: '/order/list' },
      { label: '售后处理', path: '/aftersale/list' }
    ]
  },
  {
    key: 'merchant-shop',
    label: '店铺设置',
    icon: Shop,
    children: [
      { label: '店铺信息', path: '/shop/settings' },
      { label: '发货地址', path: '/shop/address' },
      { label: '运费模板', path: '/shop/freight' }
    ]
  }
]

const adminMenu = [
  { key: 'admin-dashboard', label: '平台概览', path: '/admin/dashboard', icon: DataAnalysis },
  {
    key: 'admin-user',
    label: '用户管理',
    icon: User,
    children: [
      { label: '用户列表', path: '/user/list' },
      { label: '举报处理', path: '/user/report' }
    ]
  },
  {
    key: 'admin-merchant',
    label: '商家管理',
    icon: Shop,
    children: [
      { label: '商家审核', path: '/merchant/audit' },
      { label: '商家列表', path: '/merchant/list' }
    ]
  },
  {
    key: 'admin-goods',
    label: '商品审核',
    icon: Box,
    children: [{ label: '商品审核列表', path: '/goods/audit' }]
  },
  {
    key: 'admin-order',
    label: '订单治理',
    icon: Monitor,
    children: [
      { label: '订单监控', path: '/order/monitor' },
      { label: '纠纷处理', path: '/order/dispute' },
      { label: '异常订单', path: '/order/abnormal' }
    ]
  },
  {
    key: 'admin-system',
    label: '系统配置',
    icon: Setting,
    children: [
      { label: '分类管理', path: '/system/category' },
      { label: '城市管理', path: '/system/city' },
      { label: '轮播图管理', path: '/system/banner' },
      { label: '支付配置', path: '/system/payment' },
      { label: '全局运费', path: '/system/freight' }
    ]
  }
]

const role = computed(() => getUserRole())
const roleLabel = computed(() => getRoleLabel())
const displayName = computed(() => getDisplayName() || '未命名账号')
const menuSections = computed(() => (role.value === USER_ROLE_ADMIN ? adminMenu : merchantMenu))
const pageTitle = computed(() => route.meta?.title || '盒集后台')
const activeMenu = computed(() => route.meta?.activeMenu || route.path)

function logout() {
  clearAuth()
  router.push('/login')
}
</script>

<style scoped>
.unified-layout {
  min-height: 100vh;
}

.shell-aside {
  display: flex;
  flex-direction: column;
  background: #31251e;
  color: #fff8f1;
  border-right: 1px solid rgba(255, 255, 255, 0.08);
}

.shell-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 18px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.shell-brand-mark {
  width: 38px;
  height: 38px;
  display: grid;
  place-items: center;
  background: var(--brand-primary);
  color: #fff;
  font-weight: 700;
}

.shell-brand-title {
  font-size: 18px;
  font-weight: 700;
}

.shell-brand-subtitle {
  margin-top: 4px;
  font-size: 12px;
  color: rgba(255, 248, 241, 0.68);
}

.shell-menu-scroll {
  flex: 1;
  min-height: 0;
}

.shell-menu {
  border-right: 0;
  background: transparent;
}

.shell-menu :deep(.el-menu) {
  background: transparent;
}

.shell-menu :deep(.el-menu-item),
.shell-menu :deep(.el-sub-menu__title) {
  height: 42px;
  margin: 0;
  color: rgba(255, 248, 241, 0.84);
  border-left: 3px solid transparent;
}

.shell-menu :deep(.el-menu-item:hover),
.shell-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
}

.shell-menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
  border-left-color: var(--brand-primary);
}

.shell-menu :deep(.el-sub-menu .el-menu-item) {
  padding-left: 52px !important;
}

.shell-main {
  min-width: 0;
}

.shell-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 20px;
  background: var(--bg-panel);
  border-bottom: 1px solid var(--border-color);
}

.shell-title {
  margin: 0;
  font-size: 22px;
  color: var(--text-primary);
}

.shell-header-side {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
}

.shell-account {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--text-secondary);
  font-size: 13px;
}

.shell-account-role {
  color: var(--text-tertiary);
}

.shell-account-name {
  color: var(--text-primary);
  font-weight: 700;
}

.shell-content {
  padding: 16px 20px 20px;
  min-width: 0;
}

@media (max-width: 1080px) {
  .unified-layout {
    flex-direction: column;
  }

  .shell-aside {
    width: 100% !important;
  }
}

@media (max-width: 640px) {
  .shell-header,
  .shell-content {
    padding-inline: 16px;
  }
}
</style>
