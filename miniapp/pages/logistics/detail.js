const { get } = require('../../utils/request');

Page({
  data: {
    orderNo: '',
    logistics: null
  },

  onLoad(options) {
    this.setData({ orderNo: options.orderNo || '' });
    if (this.data.orderNo) {
      this.loadLogistics();
    }
  },

  async loadLogistics() {
    try {
      const res = await get(`/api/app/logistics/${this.data.orderNo}`);
      this.setData({ logistics: res.data });
    } catch (e) {}
  }
});
