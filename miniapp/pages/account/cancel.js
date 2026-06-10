const { cancellationTips } = require('../../config/commercialization.js');
const { clearSession } = require('../../utils/auth.js');

Page({
  data: {
    cancellationTips,
    agreed: false
  },

  onAgreeChange(e) {
    const values = e.detail.value || [];
    this.setData({ agreed: values.includes('agree') });
  },

  submitCancelRequest() {
    if (!this.data.agreed) {
      wx.showToast({ title: '请先确认注销提示', icon: 'none' });
      return;
    }

    const app = getApp();
    const records = wx.getStorageSync('accountCancelRequests') || [];
    records.unshift({
      id: Date.now(),
      createdAt: new Date().toISOString()
    });
    wx.setStorageSync('accountCancelRequests', records);
    clearSession(app);

    wx.showToast({ title: '已提交注销申请', icon: 'success' });
    setTimeout(() => {
      wx.switchTab({ url: '/pages/home/index' });
    }, 1200);
  }
});
