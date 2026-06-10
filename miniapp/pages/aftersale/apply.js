const { get, post } = require('../../utils/request');

Page({
  data: {
    orderNo: '',
    order: null,
    refundType: 1,
    refundReason: '',
    refundAmount: '0.00',
    refundTypeOptions: []
  },

  onLoad(options) {
    this.setData({ orderNo: options.orderNo || '' });
    if (this.data.orderNo) {
      this.loadOrderDetail();
    }
  },

  async loadOrderDetail() {
    try {
      const res = await get(`/api/app/order/${this.data.orderNo}`);
      const order = res.data || {};
      const refundTypeOptions = [];
      if (order.status === 1 || order.status === 2) {
        refundTypeOptions.push({ value: 1, label: '仅退款' });
      }
      if (order.status === 3) {
        refundTypeOptions.push({ value: 2, label: '退货退款' });
      }
      const defaultType = refundTypeOptions[0]?.value || 1;
      this.setData({
        order,
        refundTypeOptions,
        refundType: defaultType,
        refundAmount: order.payAmount || '0.00'
      });
    } catch (e) {}
  },

  onRefundTypeChange(e) {
    this.setData({ refundType: Number(e.detail) });
  },

  onReasonInput(e) {
    this.setData({ refundReason: e.detail });
  },

  async submitRefund() {
    if (!this.data.refundReason.trim()) {
      wx.showToast({ title: '请填写退款原因', icon: 'none' });
      return;
    }

    const firstItem = this.data.order?.items?.[0];
    try {
      const res = await post('/api/app/refund/apply', {
        orderId: this.data.order.id,
        orderItemId: firstItem?.id,
        refundType: this.data.refundType,
        refundReason: this.data.refundReason.trim(),
        evidenceImages: '[]',
        refundAmount: Number(this.data.refundAmount)
      });
      wx.redirectTo({ url: `/pages/aftersale/detail?refundId=${res.data}` });
    } catch (e) {}
  }
});
