const { invoiceTips } = require('../../config/commercialization.js');

Page({
  data: {
    invoiceTips,
    orderNo: '',
    invoiceTypeIndex: 0,
    invoiceTypes: ['个人发票', '企业发票'],
    title: '',
    taxNo: '',
    email: ''
  },

  onLoad(options) {
    this.setData({
      orderNo: options.orderNo || ''
    });
  },

  onTypeChange(e) {
    this.setData({ invoiceTypeIndex: Number(e.detail.value || 0) });
  },

  onInput(e) {
    const { field } = e.currentTarget.dataset;
    this.setData({ [field]: e.detail.value || e.detail || '' });
  },

  submitRequest() {
    if (!this.data.title.trim()) {
      wx.showToast({ title: '请填写发票抬头', icon: 'none' });
      return;
    }

    const record = {
      id: Date.now(),
      orderNo: this.data.orderNo.trim(),
      invoiceType: this.data.invoiceTypes[this.data.invoiceTypeIndex],
      title: this.data.title.trim(),
      taxNo: this.data.taxNo.trim(),
      email: this.data.email.trim(),
      createdAt: new Date().toISOString()
    };
    const records = wx.getStorageSync('invoiceRequests') || [];
    records.unshift(record);
    wx.setStorageSync('invoiceRequests', records);

    wx.showToast({ title: '申请已提交', icon: 'success' });
    setTimeout(() => wx.navigateBack({ fail() {} }), 1200);
  }
});
