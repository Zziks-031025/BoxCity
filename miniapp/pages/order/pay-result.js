const { post } = require('../../utils/request');
const { testEnvironmentNote } = require('../../config/commercialization.js');

Page({
  data: {
    status: 'processing',
    orderNos: [],
    errorMsg: '',
    successCount: 0,
    testEnvironmentNote
  },

  onLoad(options) {
    const orderNos = (options.orderNos || options.orderNo || '')
      .split(',')
      .map((item) => item.trim())
      .filter(Boolean);

    this.setData({ orderNos });

    if (orderNos.length) {
      this.startMockPay();
    }
  },

  async startMockPay() {
    wx.showLoading({ title: '模拟支付中...', mask: true });
    this.setData({ status: 'processing', errorMsg: '', successCount: 0 });

    let successCount = 0;
    try {
      for (const orderNo of this.data.orderNos) {
        await post(`/api/app/order/${orderNo}/pay/mock`, {});
        successCount += 1;
      }
      this.setData({ status: 'success', successCount });
    } catch (e) {
      this.setData({
        status: 'fail',
        successCount,
        errorMsg: e.msg || e.message || '模拟支付失败，请稍后重试'
      });
    } finally {
      wx.hideLoading();
    }
  },

  goOrderDetail() {
    if (this.data.orderNos.length === 1) {
      wx.navigateTo({ url: `/pages/order/detail?orderNo=${this.data.orderNos[0]}` });
      return;
    }
    wx.redirectTo({ url: '/pages/order/list?status=1' });
  },

  goShopping() {
    wx.switchTab({ url: '/pages/home/index' });
  },

  retryPay() {
    this.startMockPay();
  }
});
