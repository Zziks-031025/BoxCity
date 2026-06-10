<template>
  <div class="page-shell" v-loading="loading">
    <section v-if="detail" class="page-hero hero-grid">
      <div>
        <div class="hero-eyebrow">Order Detail</div>
        <h1 class="hero-title">订单 {{ detail.orderNo }}</h1>
        <p class="hero-subtitle">
          订单信息已经拆分成概览、收货、商品、金额和物流模块，减少关键信息堆叠在一块导致的阅读压力。
        </p>
        <div class="metric-inline">
          <div class="metric-chip">
            <div class="metric-chip-label">订单状态</div>
            <div class="metric-chip-value">{{ statusLabel(detail.status) }}</div>
          </div>
          <div class="metric-chip">
            <div class="metric-chip-label">实付金额</div>
            <div class="metric-chip-value">¥ {{ Number(detail.payAmount || 0).toFixed(2) }}</div>
          </div>
          <div class="metric-chip">
            <div class="metric-chip-label">商品件数</div>
            <div class="metric-chip-value">{{ detail.items?.length || 0 }}</div>
          </div>
        </div>
      </div>
      <div class="hero-side-card">
        <div class="card-eyebrow">订单操作</div>
        <h3 class="card-title">发货与物流会同步影响前台订单体验</h3>
        <p class="card-desc">
          确认发货后，小程序订单详情页会展示物流信息和状态变化。如果当前订单仍为待发货，建议尽快录入物流单号。
        </p>
        <div class="panel-actions">
          <el-button @click="router.back()">返回列表</el-button>
          <el-button v-if="detail.status === 1" type="primary" @click="router.push(`/order/ship/${detail.orderNo}`)">
            去发货
          </el-button>
        </div>
      </div>
    </section>

    <div v-if="detail" class="detail-grid">
      <el-card shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">订单概览</h3>
            <p class="section-subtitle">订单基础信息与用户备注。</p>
          </div>
        </template>
        <div class="data-list">
          <div class="data-row"><strong>订单号</strong><span>{{ detail.orderNo }}</span></div>
          <div class="data-row"><strong>店铺名称</strong><span>{{ detail.shopName || '-' }}</span></div>
          <div class="data-row"><strong>订单状态</strong><span>{{ statusLabel(detail.status) }}</span></div>
          <div class="data-row"><strong>下单时间</strong><span>{{ detail.createdAt || '-' }}</span></div>
          <div class="data-row"><strong>支付时间</strong><span>{{ detail.payTime || '-' }}</span></div>
          <div class="data-row"><strong>买家留言</strong><span>{{ detail.buyerMessage || '-' }}</span></div>
        </div>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">收货信息</h3>
            <p class="section-subtitle">用于确认收件人和地址是否完整。</p>
          </div>
        </template>
        <div class="data-list">
          <div class="data-row"><strong>收货人</strong><span>{{ address.name || '-' }}</span></div>
          <div class="data-row"><strong>联系电话</strong><span>{{ address.phone || '-' }}</span></div>
          <div class="data-row address-row">
            <strong>收货地址</strong>
            <span>{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail || '-' }}</span>
          </div>
        </div>
      </el-card>

      <el-card class="span-2" shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">商品信息</h3>
            <p class="section-subtitle">商品行使用更清晰的图文结构，不会在窄屏下压成一团。</p>
          </div>
        </template>
        <div class="goods-stack">
          <div v-for="item in detail.items || []" :key="item.id" class="goods-item">
            <div class="goods-inline">
              <el-image :src="item.mainImage" class="thumb" fit="cover" />
              <div>
                <div class="goods-inline-title">{{ item.goodsTitle }}</div>
                <div class="goods-inline-meta">单价 ¥ {{ Number(item.price || 0).toFixed(2) }} · 数量 {{ item.quantity }}</div>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">金额信息</h3>
            <p class="section-subtitle">强调商品总额、运费和实付金额。</p>
          </div>
        </template>
        <div class="amount-list">
          <div class="amount-row">
            <strong>商品总额</strong>
            <span>¥ {{ Number(detail.totalAmount || 0).toFixed(2) }}</span>
          </div>
          <div class="amount-row">
            <strong>运费</strong>
            <span>¥ {{ Number(detail.freightAmount || 0).toFixed(2) }}</span>
          </div>
          <div class="amount-row total">
            <strong>实付金额</strong>
            <span>¥ {{ Number(detail.payAmount || 0).toFixed(2) }}</span>
          </div>
        </div>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <div>
            <h3 class="section-title">物流信息</h3>
            <p class="section-subtitle">当前物流状态与单号。</p>
          </div>
        </template>
        <div class="data-list">
          <div class="data-row"><strong>物流公司</strong><span>{{ detail.logisticsCompany || '-' }}</span></div>
          <div class="data-row"><strong>物流单号</strong><span>{{ detail.logisticsNo || '-' }}</span></div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail } from '@/api/merchant'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref(null)
const address = ref({})

function statusLabel(status) {
  return { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消', 5: '售后中' }[status] || '未知'
}

async function loadDetail() {
  loading.value = true
  try {
    detail.value = await getOrderDetail(route.params.orderNo)
    address.value = detail.value.addressSnapshot ? JSON.parse(detail.value.addressSnapshot) : {}
  } finally {
    loading.value = false
  }
}

onMounted(loadDetail)
</script>

<style scoped>
.span-2 {
  grid-column: 1 / -1;
}

.goods-stack {
  display: grid;
  gap: 14px;
}

.goods-item {
  padding: 14px 0;
  border-bottom: 1px solid rgba(226, 207, 188, 0.82);
}

.goods-item:last-child {
  border-bottom: none;
}

.address-row span {
  max-width: 360px;
  text-align: right;
}

@media (max-width: 720px) {
  .address-row span {
    max-width: none;
    text-align: left;
  }
}
</style>
