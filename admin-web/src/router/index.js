import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '@/layout/AdminLayout.vue'

const routes = [
  { path: '/login', component: () => import('@/views/Login.vue'), meta: { title: '管理员登录', subtitle: '统一处理审核、治理、配置与运营监控。' } },
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '数据概览', subtitle: '查看平台整体经营、审核和风险处理情况。' } },
      { path: 'user/list', component: () => import('@/views/user/UserList.vue'), meta: { title: '用户管理', subtitle: '查看用户资料、账号状态与风险记录。' } },
      { path: 'user/detail/:id', component: () => import('@/views/user/UserDetail.vue'), meta: { title: '用户详情', subtitle: '查看用户订单、举报和账号行为记录。' } },
      { path: 'user/report', component: () => import('@/views/user/ReportList.vue'), meta: { title: '举报处理', subtitle: '处理用户举报、违规内容与平台反馈。' } },
      { path: 'merchant/audit', component: () => import('@/views/merchant/MerchantAudit.vue'), meta: { title: '商家审核', subtitle: '审核商家入驻申请与主体资质。' } },
      { path: 'merchant/audit/:id', component: () => import('@/views/merchant/MerchantAuditDetail.vue'), meta: { title: '商家审核详情', subtitle: '查看资质材料并做出审核结论。' } },
      { path: 'merchant/list', component: () => import('@/views/merchant/MerchantList.vue'), meta: { title: '商家列表', subtitle: '查看商家状态、经营信息与平台备注。' } },
      { path: 'goods/audit', component: () => import('@/views/goods/GoodsAudit.vue'), meta: { title: '商品审核', subtitle: '审核商品资料、属性和上架内容。' } },
      { path: 'goods/audit/:id', component: () => import('@/views/goods/GoodsAuditDetail.vue'), meta: { title: '商品审核详情', subtitle: '查看商品图片、描述与审核意见。' } },
      { path: 'order/monitor', component: () => import('@/views/order/OrderMonitor.vue'), meta: { title: '订单监控', subtitle: '查看全平台订单状态、支付与履约异常。' } },
      { path: 'order/dispute', component: () => import('@/views/order/DisputeList.vue'), meta: { title: '纠纷仲裁', subtitle: '处理平台介入、售后争议与风险订单。' } },
      { path: 'order/abnormal', component: () => import('@/views/order/AbnormalOrder.vue'), meta: { title: '异常订单', subtitle: '聚合异常订单并辅助排查风险原因。' } },
      { path: 'system/category', component: () => import('@/views/system/CategoryManage.vue'), meta: { title: '分类管理', subtitle: '维护前台分类结构、排序与展示层级。' } },
      { path: 'system/city', component: () => import('@/views/system/CityManage.vue'), meta: { title: '城市管理', subtitle: '维护城市覆盖范围与前台城市数据。' } },
      { path: 'system/payment', component: () => import('@/views/system/PaymentConfig.vue'), meta: { title: '支付配置', subtitle: '维护支付环境、测试说明与后续接入点。' } },
      { path: 'system/freight', component: () => import('@/views/system/FreightGlobal.vue'), meta: { title: '全局运费', subtitle: '统一配置平台级运费与默认履约策略。' } },
      { path: 'system/banner', component: () => import('@/views/system/BannerManage.vue'), meta: { title: '轮播图管理', subtitle: '维护首页轮播图并明确前台映射效果。' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('admin_token')
  if (to.path !== '/login' && !token) {
    next('/login')
    return
  }

  document.title = to.meta?.title ? `${to.meta.title} - 盒集平台端` : '盒集平台端'
  next()
})

export default router
