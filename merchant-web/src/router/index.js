import { createRouter, createWebHistory } from 'vue-router'
import MerchantLayout from '@/layout/MerchantLayout.vue'
import { clearAuth, getRoleHomePath, getUserRole, USER_ROLE_ADMIN, USER_ROLE_MERCHANT } from '@/utils/auth'

const publicRoutes = [
  {
    path: '/login',
    component: () => import('@/views/Login.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/apply',
    component: () => import('@/views/Apply.vue'),
    meta: { public: true, title: '商家入驻申请' }
  },
  {
    path: '/apply/status',
    component: () => import('@/views/ApplyStatus.vue'),
    meta: { public: true, title: '申请状态' }
  }
]

const merchantRoutes = [
  { path: '/dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '工作台', role: USER_ROLE_MERCHANT } },
  { path: '/goods/list', component: () => import('@/views/goods/GoodsList.vue'), meta: { title: '商品列表', role: USER_ROLE_MERCHANT } },
  { path: '/goods/edit', component: () => import('@/views/goods/GoodsEdit.vue'), meta: { title: '发布商品', role: USER_ROLE_MERCHANT, activeMenu: '/goods/edit' } },
  { path: '/goods/edit/:id', component: () => import('@/views/goods/GoodsEdit.vue'), meta: { title: '编辑商品', role: USER_ROLE_MERCHANT, activeMenu: '/goods/list' } },
  { path: '/goods/stock', component: () => import('@/views/goods/GoodsStock.vue'), meta: { title: '库存管理', role: USER_ROLE_MERCHANT } },
  { path: '/order/list', component: () => import('@/views/order/OrderList.vue'), meta: { title: '订单列表', role: USER_ROLE_MERCHANT } },
  { path: '/order/detail/:orderNo', component: () => import('@/views/order/OrderDetail.vue'), meta: { title: '订单详情', role: USER_ROLE_MERCHANT, activeMenu: '/order/list' } },
  { path: '/order/ship/:orderNo', component: () => import('@/views/order/OrderShip.vue'), meta: { title: '订单发货', role: USER_ROLE_MERCHANT, activeMenu: '/order/list' } },
  { path: '/aftersale/list', component: () => import('@/views/aftersale/AftersaleList.vue'), meta: { title: '售后处理', role: USER_ROLE_MERCHANT } },
  { path: '/aftersale/detail/:id', component: () => import('@/views/aftersale/AftersaleDetail.vue'), meta: { title: '售后详情', role: USER_ROLE_MERCHANT, activeMenu: '/aftersale/list' } },
  { path: '/shop/settings', component: () => import('@/views/shop/ShopSettings.vue'), meta: { title: '店铺信息', role: USER_ROLE_MERCHANT } },
  { path: '/shop/address', component: () => import('@/views/shop/ShopAddress.vue'), meta: { title: '发货地址', role: USER_ROLE_MERCHANT } },
  { path: '/shop/freight', component: () => import('@/views/shop/ShopFreight.vue'), meta: { title: '运费模板', role: USER_ROLE_MERCHANT } }
]

const adminRoutes = [
  { path: '/admin/dashboard', component: () => import('@/views/admin/Dashboard.vue'), meta: { title: '平台概览', role: USER_ROLE_ADMIN } },
  { path: '/user/list', component: () => import('@/views/admin/user/UserList.vue'), meta: { title: '用户列表', role: USER_ROLE_ADMIN } },
  { path: '/user/detail/:id', component: () => import('@/views/admin/user/UserDetail.vue'), meta: { title: '用户详情', role: USER_ROLE_ADMIN, activeMenu: '/user/list' } },
  { path: '/user/report', component: () => import('@/views/admin/user/ReportList.vue'), meta: { title: '举报处理', role: USER_ROLE_ADMIN } },
  { path: '/merchant/audit', component: () => import('@/views/admin/merchant/MerchantAudit.vue'), meta: { title: '商家审核', role: USER_ROLE_ADMIN } },
  { path: '/merchant/audit/:id', component: () => import('@/views/admin/merchant/MerchantAuditDetail.vue'), meta: { title: '审核详情', role: USER_ROLE_ADMIN, activeMenu: '/merchant/audit' } },
  { path: '/merchant/list', component: () => import('@/views/admin/merchant/MerchantList.vue'), meta: { title: '商家列表', role: USER_ROLE_ADMIN } },
  { path: '/goods/audit', component: () => import('@/views/admin/goods/GoodsAudit.vue'), meta: { title: '商品审核', role: USER_ROLE_ADMIN } },
  { path: '/goods/audit/:id', component: () => import('@/views/admin/goods/GoodsAuditDetail.vue'), meta: { title: '商品审核详情', role: USER_ROLE_ADMIN, activeMenu: '/goods/audit' } },
  { path: '/order/monitor', component: () => import('@/views/admin/order/OrderMonitor.vue'), meta: { title: '订单监控', role: USER_ROLE_ADMIN } },
  { path: '/order/dispute', component: () => import('@/views/admin/order/DisputeList.vue'), meta: { title: '纠纷处理', role: USER_ROLE_ADMIN } },
  { path: '/order/abnormal', component: () => import('@/views/admin/order/AbnormalOrder.vue'), meta: { title: '异常订单', role: USER_ROLE_ADMIN } },
  { path: '/system/category', component: () => import('@/views/admin/system/CategoryManage.vue'), meta: { title: '分类管理', role: USER_ROLE_ADMIN } },
  { path: '/system/city', component: () => import('@/views/admin/system/CityManage.vue'), meta: { title: '城市管理', role: USER_ROLE_ADMIN } },
  { path: '/system/banner', component: () => import('@/views/admin/system/BannerManage.vue'), meta: { title: '轮播图管理', role: USER_ROLE_ADMIN } },
  { path: '/system/payment', component: () => import('@/views/admin/system/PaymentConfig.vue'), meta: { title: '支付配置', role: USER_ROLE_ADMIN } },
  { path: '/system/freight', component: () => import('@/views/admin/system/FreightGlobal.vue'), meta: { title: '全局运费', role: USER_ROLE_ADMIN } }
]

const routes = [
  ...publicRoutes,
  {
    path: '/',
    component: MerchantLayout,
    children: [...merchantRoutes, ...adminRoutes]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

function getTokenByRole(role) {
  return role === USER_ROLE_ADMIN ? localStorage.getItem('admin_token') : localStorage.getItem('merchant_token')
}

router.beforeEach((to, from, next) => {
  const role = getUserRole()
  const isPublic = Boolean(to.meta?.public)

  if (to.path === '/') {
    next(getRoleHomePath(role || USER_ROLE_MERCHANT))
    return
  }

  if (isPublic) {
    if (to.path === '/login' && role && getTokenByRole(role)) {
      next(getRoleHomePath(role))
      return
    }
    document.title = `${to.meta?.title || '盒集后台'} - 盒集后台`
    next()
    return
  }

  const routeRole = to.meta?.role
  const token = getTokenByRole(role)

  if (!role || !token) {
    clearAuth()
    next('/login')
    return
  }

  if (routeRole && routeRole !== role) {
    next(getRoleHomePath(role))
    return
  }

  document.title = `${to.meta?.title || '盒集后台'} - 盒集后台`
  next()
})

export default router
