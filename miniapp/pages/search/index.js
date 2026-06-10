const { get, del } = require('../../utils/request');

Page({
  data: {
    keyword: '',
    searchHistory: [],
    hotKeywords: [],
    focused: true,
    navPaddingTop: 0
  },

  onLoad() {
    const menuBtn = wx.getMenuButtonBoundingClientRect();
    this.setData({
      navPaddingTop: menuBtn.top
    });
    this.loadHistory();
    this.loadHotKeywords();
  },

  async loadHistory() {
    try {
      const res = await get('/api/app/search/history', {}, false);
      this.setData({ searchHistory: res.data || [] });
    } catch (e) {
      // 未登录时忽略搜索历史记录
    }
  },

  async loadHotKeywords() {
    try {
      const res = await get('/api/app/search/hot', {}, false);
      this.setData({ hotKeywords: res.data || [] });
    } catch (e) {}
  },

  onKeywordInput(e) {
    this.setData({ keyword: e.detail.value || '' });
  },

  clearKeyword() {
    this.setData({ keyword: '' });
  },

  onSearch(e) {
    const keyword = (e.detail.value || this.data.keyword || '').trim();
    if (!keyword) return;
    this.goToResult(keyword);
  },

  onHistoryTap(e) {
    this.goToResult(e.currentTarget.dataset.keyword);
  },

  onHotTap(e) {
    this.goToResult(e.currentTarget.dataset.keyword);
  },

  goToResult(keyword) {
    wx.navigateTo({ url: `/pages/search/result?keyword=${encodeURIComponent(keyword)}` });
  },

  async clearHistory() {
    wx.showModal({
      title: '清空搜索记录',
      content: '确认删除全部搜索历史吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await del('/api/app/search/history');
            this.setData({ searchHistory: [] });
          } catch (e) {}
        }
      }
    });
  },

  onCancel() {
    wx.navigateBack();
  },

  onBack() {
    wx.navigateBack();
  }
});
