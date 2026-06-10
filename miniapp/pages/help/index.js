const { helpTopics } = require('../../config/commercialization.js');

Page({
  data: {
    helpTopics,
    quickLinks: [
      { title: '客服中心', desc: '查看平台联系方式与建议处理方式', url: '/pages/service/index' },
      { title: '投诉举报', desc: '提交订单问题、服务争议与隐私问题', url: '/pages/complaint/create' },
      { title: '平台信息', desc: '查看主体公示、备案与经营说明', url: '/pages/platform/index' }
    ]
  },

  goLink(e) {
    const { url } = e.currentTarget.dataset;
    if (url) {
      wx.navigateTo({ url });
    }
  }
});
