const { serviceCenter } = require('../../config/commercialization.js');

Page({
  data: {
    serviceCenter
  },

  copyValue(e) {
    const { value } = e.currentTarget.dataset;
    if (!value) return;
    wx.setClipboardData({
      data: value,
      success() {
        wx.showToast({ title: '已复制', icon: 'success' });
      }
    });
  }
});
