const { get, put, del } = require('../../utils/request');
const { getIOSRestriction } = require('../../utils/product.js');
const app = getApp();

const STATUS_TABS = [
  { label: '全部', status: -1 },
  { label: '待付款', status: 0 },
  { label: '待发货', status: 1 },
  { label: '待收货', status: 2 },
  { label: '已完成', status: 3 },
  { label: '售后中', status: 5 }
];

const STATUS_META = {
  0: { text: '待付款', className: 'status-0', desc: '等待你完成支付' },
  1: { text: '待发货', className: 'status-1', desc: '商家正在备货中' },
  2: { text: '待收货', className: 'status-2', desc: '商品已发出，请留意物流' },
  3: { text: '已完成', className: 'status-3', desc: '订单已完成，欢迎评价' },
  4: { text: '已取消', className: 'status-4', desc: '这笔订单已取消' },
  5: { text: '售后中', className: 'status-5', desc: '售后处理中，请耐心等待' }
};

Page({
  data: {
    tabs: STATUS_TABS,
    activeTab: 0,
    currentTabLabel: '全部',
    orderList: [],
    page: 1,
    size: 10,
    hasMore: true,
    loading: false
  },

  onLoad(options) {
    if (options.status !== undefined) {
      const idx = STATUS_TABS.findIndex((tab) => String(tab.status) === String(options.status));
      if (idx >= 0) {
        this.setData({ activeTab: idx, currentTabLabel: STATUS_TABS[idx].label });
      }
    }
    this.loadOrders(true);
  },

  onShow() {
    this.loadOrders(true);
  },

  onTabChange(e) {
    const activeTab = e.detail.index;
    this.setData({
      activeTab,
      currentTabLabel: STATUS_TABS[activeTab].label,
      orderList: [],
      page: 1,
      hasMore: true
    });
    this.loadOrders(true);
  },

  async loadOrders(reset = false) {
    if (this.data.loading) return;
    if (!reset && !this.data.hasMore) return;

    const currentPage = reset ? 1 : this.data.page;
    const status = STATUS_TABS[this.data.activeTab].status;
    this.setData({ loading: true });

    try {
      const params = { page: currentPage, size: this.data.size };
      if (status !== -1) params.status = status;
      const res = await get('/api/app/order/list', params);
      const payload = res.data || {};
      const list = (payload.records || []).map((item) => this.normalizeOrderListItem(item));
      const total = payload.total || list.length;
      const nextList = reset ? list : [...this.data.orderList, ...list];

      this.setData({
        orderList: nextList,
        page: currentPage + 1,
        hasMore: nextList.length < total
      });
    } catch (e) {
    } finally {
      this.setData({ loading: false });
    }
  },

  normalizeOrderListItem(order) {
    const firstItem = order.firstItem || {};
    const statusMeta = STATUS_META[order.status] || { text: '未知状态', className: '', desc: '' };
    const salePolicy = getIOSRestriction({
      goodsType: firstItem.goodsType || order.goodsType,
      type: firstItem.type || order.type,
      productType: firstItem.productType || order.productType,
      title: firstItem.goodsTitle,
      description: order.buyerMessage || ''
    }, (app.globalData && app.globalData.platformInfo) || {});

    const items = firstItem.id
      ? [{
          id: firstItem.id,
          goodsName: firstItem.goodsTitle,
          imageUrl: firstItem.mainImage,
          price: Number(firstItem.price || 0).toFixed(2),
          quantity: firstItem.quantity,
          reviewed: !!firstItem.reviewed,
          goodsPolicyText: `${salePolicy.goodsTypeLabel} · ${salePolicy.fulfillmentLabel}`
        }]
      : [];

    return {
      ...order,
      ...salePolicy,
      totalCount: order.itemCount || items.reduce((sum, item) => sum + Number(item.quantity || 0), 0),
      reviewed: !!firstItem.reviewed,
      orderItems: items,
      statusText: statusMeta.text,
      statusClass: statusMeta.className,
      statusDesc: statusMeta.desc,
      createTimeText: order.createdAt || order.createTime || ''
    };
  },

  onReachBottom() {
    this.loadOrders(false);
  },

  goDetail(e) {
    const orderNo = e.currentTarget.dataset.orderNo;
    wx.navigateTo({ url: `/pages/order/detail?orderNo=${orderNo}` });
  },

  goPay(e) {
    const orderNo = e.currentTarget.dataset.orderNo;
    const currentOrder = this.data.orderList.find((item) => item.orderNo === orderNo);
    if (currentOrder?.iosPurchaseBlocked) {
      wx.showToast({ title: 'iOS 端暂不支持该权益购买', icon: 'none' });
      return;
    }
    wx.navigateTo({ url: `/pages/order/pay-result?orderNos=${orderNo}` });
  },

  async cancelOrder(e) {
    const orderNo = e.currentTarget.dataset.orderNo;
    wx.showModal({
      title: '取消订单',
      content: '确认取消这笔订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await put(`/api/app/order/${orderNo}/cancel`);
            wx.showToast({ title: '已取消', icon: 'success' });
            this.loadOrders(true);
          } catch (e) {}
        }
      }
    });
  },

  async confirmReceive(e) {
    const orderNo = e.currentTarget.dataset.orderNo;
    wx.showModal({
      title: '确认收货',
      content: '确认已经收到这笔订单的商品吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await put(`/api/app/order/${orderNo}/confirm`);
            wx.showToast({ title: '确认收货成功', icon: 'success' });
            this.loadOrders(true);
          } catch (e) {}
        }
      }
    });
  },

  goReview(e) {
    const orderNo = e.currentTarget.dataset.orderNo;
    const order = this.data.orderList.find((item) => item.orderNo === orderNo);
    if (order?.reviewed) {
      wx.showToast({ title: '该订单已评价', icon: 'none' });
      return;
    }
    wx.navigateTo({ url: `/pages/review/create?orderNo=${orderNo}` });
  },

  async deleteOrder(e) {
    const orderNo = e.currentTarget.dataset.orderNo;
    wx.showModal({
      title: '删除订单',
      content: '确认删除这笔订单记录吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await del(`/api/app/order/${orderNo}`);
            wx.showToast({ title: '已删除', icon: 'success' });
            this.loadOrders(true);
          } catch (e) {}
        }
      }
    });
  },

  remindShip() {
    wx.showToast({ title: '已提醒商家发货', icon: 'success' });
  }
});
