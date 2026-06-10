const { get } = require('../../utils/request');

Page({
  data: {
    goodsId: null,
    list: [],
    page: 1,
    size: 10,
    hasMore: true,
    loading: false
  },

  onLoad(options) {
    this.setData({ goodsId: Number(options.goodsId) });
    this.loadData(true);
  },

  onReachBottom() {
    this.loadData(false);
  },

  async loadData(reset = false) {
    if (this.data.loading) return;
    if (!reset && !this.data.hasMore) return;
    const page = reset ? 1 : this.data.page;
    this.setData({ loading: true });
    try {
      const res = await get(`/api/app/review/goods/${this.data.goodsId}`, {
        page,
        size: this.data.size
      });
      const pageData = res.data || {};
      const records = (pageData.records || []).map((item) => ({
        ...item,
        userInitial: (item.userName || '匿').charAt(0)
      }));
      const list = reset ? records : this.data.list.concat(records);
      this.setData({
        list,
        page: page + 1,
        hasMore: list.length < (pageData.total || 0)
      });
    } catch (e) {
    } finally {
      this.setData({ loading: false });
    }
  }
});
