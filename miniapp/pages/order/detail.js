const { get, put } = require('../../utils/request');
const { getIOSRestriction, getGoodsTypeLabel, getFulfillmentLabel } = require('../../utils/product.js');
const { orderRules, testEnvironmentNote } = require('../../config/commercialization.js');
const app = getApp();

const STATUS_META = {
  0: { text: '待付款', desc: '请尽快完成支付，商家会在支付成功后开始备货。', mark: '付', className: 'status-bg-0' },
  1: { text: '待发货', desc: '商家正在准备商品，发货后会第一时间同步物流信息。', mark: '发', className: 'status-bg-1' },
  2: { text: '待收货', desc: '商品已经发出，请及时关注物流动态并确认收货。', mark: '收', className: 'status-bg-2' },
  3: { text: '已完成', desc: '这笔订单已经完成，欢迎继续逛逛更多同城好物。', mark: '成', className: 'status-bg-3' },
  4: { text: '已取消', desc: '订单已经取消，如有需要可以重新下单。', mark: '关', className: 'status-bg-4' },
  5: { text: '售后中', desc: '售后申请正在处理中，你可以在售后详情中查看当前进度。', mark: '售', className: 'status-bg-5' }
};

Page({
  data: {
    orderNo: '',
    order: null,
    orderRules,
    testEnvironmentNote,
    supportLinks: []
  },

  onLoad(options) {
    const orderNo = options.orderNo || '';
    this.setData({ orderNo });
    this.setupSupportLinks(orderNo);
    this.loadDetail();
  },

  setupSupportLinks(orderNo) {
    this.setData({
      supportLinks: [
        { title: '客服中心', desc: '咨询物流、履约和售后问题', url: '/pages/service/index' },
        { title: '投诉举报', desc: '提交订单争议或服务问题', url: `/pages/complaint/create?orderNo=${orderNo}` },
        { title: '发票与凭证', desc: '提交测试环境开票申请', url: `/pages/invoice/index?orderNo=${orderNo}` },
        { title: '平台信息', desc: '查看主体公示与经营说明', url: '/pages/platform/index' }
      ]
    });
  },

  async loadDetail() {
    try {
      const res = await get(`/api/app/order/${this.data.orderNo}`);
      this.setData({ order: this.normalizeOrderDetail(res.data || {}) });
    } catch (e) {}
  },

  normalizeOrderDetail(order) {
    const address = this.parseAddress(order.addressSnapshot);
    const statusMeta = STATUS_META[order.status] || STATUS_META[0];
    const rawItems = order.items || [];
    const platformInfo = (app.globalData && app.globalData.platformInfo) || {};
    const normalizedItems = rawItems.map((item) => {
      const salePolicy = getIOSRestriction({
        goodsType: item.goodsType || order.goodsType,
        type: item.type || order.type,
        productType: item.productType || order.productType,
        title: item.goodsTitle,
        description: order.buyerMessage || ''
      }, platformInfo);

      return {
        id: item.id,
        goodsName: item.goodsTitle,
        imageUrl: item.mainImage,
        price: Number(item.price || 0).toFixed(2),
        quantity: item.quantity,
        goodsType: salePolicy.goodsType,
        iosPurchaseBlocked: salePolicy.iosPurchaseBlocked,
        goodsPolicyText: `${salePolicy.goodsTypeLabel} · ${salePolicy.fulfillmentLabel}`
      };
    });

    const orderGoodsType = this.resolveOrderGoodsType(normalizedItems);
    const summaryPolicy = {
      goodsType: orderGoodsType,
      goodsTypeLabel: getGoodsTypeLabel(orderGoodsType),
      fulfillmentLabel: getFulfillmentLabel(orderGoodsType),
      iosPurchaseBlocked: normalizedItems.some((item) => item.iosPurchaseBlocked),
      iosRestrictionText: normalizedItems.some((item) => item.iosPurchaseBlocked)
        ? '当前订单包含数字权益类内容。为满足 iOS 端审核要求，小程序暂不在 iOS 端展示支付入口。'
        : ''
    };

    return {
      ...order,
      ...summaryPolicy,
      createTime: order.createdAt,
      receiverName: address.name || '',
      receiverPhone: address.phone || '',
      receiverAddress: `${address.province || ''}${address.city || ''}${address.district || ''}${address.detail || ''}`,
      orderItems: normalizedItems,
      statusText: statusMeta.text,
      statusDesc: statusMeta.desc,
      statusMark: statusMeta.mark,
      statusClass: statusMeta.className,
      fulfillmentNotice: this.buildFulfillmentNotice(orderGoodsType)
    };
  },

  resolveOrderGoodsType(items) {
    if (items.some((item) => item.goodsType === 'digital')) {
      return 'digital';
    }

    if (items.some((item) => item.goodsType === 'offline_service')) {
      return 'offline_service';
    }

    return 'physical';
  },

  buildFulfillmentNotice(goodsType) {
    if (goodsType === 'digital') {
      return '该订单对应的是数字权益类内容。为满足 iOS 端审核要求，小程序不会在 iOS 端继续展示支付入口。';
    }

    if (goodsType === 'offline_service') {
      return '该订单对应线下履约服务，请按商品页和订单页提示进行预约或到店核销，必要时可联系平台客服协助处理。';
    }

    return '该订单对应实物商品，支付后会进入备货、发货与物流流程。你可以在订单页查看物流信息、申请售后或发起投诉。';
  },

  parseAddress(addressSnapshot) {
    if (!addressSnapshot) return {};
    try {
      return JSON.parse(addressSnapshot);
    } catch (e) {
      return {};
    }
  },

  goPay() {
    if (this.data.order?.iosPurchaseBlocked) {
      wx.showToast({ title: 'iOS 端暂不展示该权益购买入口', icon: 'none' });
      return;
    }

    wx.navigateTo({ url: `/pages/order/pay-result?orderNos=${this.data.orderNo}` });
  },

  async cancelOrder() {
    wx.showModal({
      title: '取消订单',
      content: '确认取消这笔订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await put(`/api/app/order/${this.data.orderNo}/cancel`);
            wx.showToast({ title: '订单已取消', icon: 'success' });
            this.loadDetail();
          } catch (e) {}
        }
      }
    });
  },

  async confirmReceive() {
    wx.showModal({
      title: '确认收货',
      content: '确认已经收到这笔订单的商品吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await put(`/api/app/order/${this.data.orderNo}/confirm`);
            wx.showToast({ title: '确认收货成功', icon: 'success' });
            this.loadDetail();
          } catch (e) {}
        }
      }
    });
  },

  goReview() {
    wx.navigateTo({ url: `/pages/review/create?orderNo=${this.data.orderNo}` });
  },

  goLogistics() {
    wx.navigateTo({ url: `/pages/logistics/detail?orderNo=${this.data.orderNo}` });
  },

  goAftersaleApply() {
    wx.navigateTo({ url: `/pages/aftersale/apply?orderNo=${this.data.orderNo}` });
  },

  goAftersaleDetail() {
    if (!this.data.order || !this.data.order.refundId) return;
    wx.navigateTo({ url: `/pages/aftersale/detail?refundId=${this.data.order.refundId}` });
  },

  copyOrderNo() {
    wx.setClipboardData({
      data: this.data.orderNo,
      success: () => wx.showToast({ title: '已复制', icon: 'success' })
    });
  },

  goSupport(e) {
    const { url } = e.currentTarget.dataset;
    if (url) {
      wx.navigateTo({ url });
    }
  }
});
