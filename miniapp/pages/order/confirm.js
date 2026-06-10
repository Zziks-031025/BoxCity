const { get, post } = require('../../utils/request');
const { getIOSRestriction } = require('../../utils/product.js');
const { orderRules, testEnvironmentNote } = require('../../config/commercialization.js');
const app = getApp();

Page({
  data: {
    cartIds: [],
    goodsId: null,
    quantity: 1,
    address: null,
    merchantOrders: [],
    totalAmount: '0.00',
    freightAmount: '0.00',
    payAmount: '0.00',
    buyerMessage: '',
    hasIOSRestrictedItems: false,
    iosRestrictionNote: '',
    orderRules,
    testEnvironmentNote,
    supportLinks: [
      { title: '帮助中心', url: '/pages/help/index' },
      { title: '平台信息', url: '/pages/platform/index' }
    ]
  },

  onLoad(options) {
    if (options.cartIds) {
      this.setData({ cartIds: options.cartIds.split(',').filter(Boolean).map(Number) });
    } else if (options.goodsId) {
      this.setData({
        goodsId: Number(options.goodsId),
        quantity: parseInt(options.quantity || '1', 10)
      });
    }
    this.loadDefaultAddress();
  },

  onShow() {
    if (app.globalData.selectedAddress) {
      this.setData({ address: this.normalizeAddress(app.globalData.selectedAddress) });
      app.globalData.selectedAddress = null;
      this.loadPreview();
    }
  },

  async loadDefaultAddress() {
    try {
      const res = await get('/api/app/address/list');
      const list = res.data || [];
      const defaultAddr = list.find((item) => item.isDefault === 1) || list[0] || null;
      this.setData({ address: this.normalizeAddress(defaultAddr) });
      if (defaultAddr) {
        this.loadPreview();
      }
    } catch (e) {}
  },

  normalizeAddress(address) {
    if (!address) return null;
    return {
      ...address,
      receiverName: address.name || address.receiverName,
      receiverPhone: address.phone || address.receiverPhone,
      detailAddress: address.detail || address.detailAddress
    };
  },

  buildPayload() {
    return {
      addressId: this.data.address ? this.data.address.id : undefined,
      buyerMessage: this.data.buyerMessage,
      cartIds: this.data.cartIds.length ? this.data.cartIds : undefined,
      goodsId: this.data.goodsId || undefined,
      quantity: this.data.quantity
    };
  },

  async loadPreview() {
    if (!this.data.address || !this.data.address.id) return;

    try {
      const res = await post('/api/app/order/preview', this.buildPayload());
      const preview = res.data || {};
      const platformInfo = (app.globalData && app.globalData.platformInfo) || {};
      const merchantOrders = (preview.merchantOrders || []).map((merchant) => {
        const items = (merchant.items || []).map((item) => {
          const salePolicy = getIOSRestriction({
            goodsType: item.goodsType || merchant.goodsType,
            type: item.type || merchant.type,
            productType: item.productType || merchant.productType,
            title: item.goodsName,
            description: item.goodsSubtitle || merchant.shopName
          }, platformInfo);

          return {
            ...item,
            ...salePolicy,
            price: this.formatMoney(item.price),
            goodsPolicyText: `${salePolicy.goodsTypeLabel} · ${salePolicy.fulfillmentLabel}`
          };
        });

        return {
          ...merchant,
          totalAmount: this.formatMoney(merchant.totalAmount),
          freightAmount: this.formatMoney(merchant.freightAmount),
          payAmount: this.formatMoney(merchant.payAmount),
          items,
          hasIOSRestrictedItems: items.some((item) => item.iosPurchaseBlocked)
        };
      });

      const hasIOSRestrictedItems = merchantOrders.some((merchant) => merchant.hasIOSRestrictedItems);

      this.setData({
        merchantOrders,
        totalAmount: this.formatMoney(preview.totalAmount),
        freightAmount: this.formatMoney(preview.freightAmount),
        payAmount: this.formatMoney(preview.payAmount),
        hasIOSRestrictedItems,
        iosRestrictionNote: hasIOSRestrictedItems
          ? '当前订单中包含数字权益类内容。为满足 iOS 端审核要求，小程序暂不在 iOS 端提供下单与支付入口。'
          : ''
      });
    } catch (e) {}
  },

  formatMoney(value) {
    return Number(value || 0).toFixed(2);
  },

  selectAddress() {
    wx.navigateTo({ url: '/pages/address/list?select=1' });
  },

  onMessageInput(e) {
    this.setData({ buyerMessage: e.detail.value || e.detail || '' });
  },

  async submitOrder() {
    if (!this.data.address || !this.data.address.id) {
      wx.showToast({ title: '请选择收货地址', icon: 'none' });
      return;
    }

    if (this.data.hasIOSRestrictedItems) {
      wx.showToast({ title: '当前 iOS 端暂不支持该权益下单', icon: 'none' });
      return;
    }

    try {
      const res = await post('/api/app/order/create', this.buildPayload());
      const orderNos = res.data || '';
      wx.navigateTo({ url: `/pages/order/pay-result?orderNos=${orderNos}` });
    } catch (e) {}
  },

  goSupport(e) {
    const { url } = e.currentTarget.dataset;
    if (url) {
      wx.navigateTo({ url });
    }
  }
});
