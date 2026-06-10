const { get } = require('../../utils/request');
const { getIOSRestriction } = require('../../utils/product.js');
const app = getApp();

Page({
  data: {
    keyword: '',
    goodsList: [],
    page: 1,
    pageSize: 20,
    hasMore: true,
    loadingMore: false,
    initialLoading: true,
    total: 0,
    navPaddingTop: 0
  },

  onLoad(options) {
    const menuBtn = wx.getMenuButtonBoundingClientRect();
    this.setData({
      navPaddingTop: menuBtn.top
    });
    const keyword = decodeURIComponent(options.keyword || '');
    this.setData({ keyword });
    if (keyword) this.loadGoods(true);
  },

  onKeywordInput(e) {
    this.setData({ keyword: e.detail.value || '' });
  },

  onSearch(e) {
    const keyword = (e.detail.value || this.data.keyword || '').trim();
    if (!keyword) return;
    this.setData({
      keyword,
      goodsList: [],
      page: 1,
      hasMore: true,
      initialLoading: true,
      total: 0
    });
    this.loadGoods(true);
  },

  async loadGoods(reset = false) {
    const { keyword, page, pageSize, loadingMore } = this.data;
    if (!keyword || loadingMore) return;

    this.setData({ loadingMore: true });
    try {
      const res = await get('/api/app/search/goods', {
        keyword,
        page: reset ? 1 : page,
        size: pageSize
      });
      const payload = res.data || {};
      const list = this.normalizeGoodsList(payload.records || payload.list || []);
      const total = payload.total || list.length;
      const nextPage = reset ? 2 : page + 1;
      const newList = reset ? list : [...this.data.goodsList, ...list];

      this.setData({
        goodsList: newList,
        page: nextPage,
        hasMore: newList.length < total,
        loadingMore: false,
        initialLoading: false,
        total
      });
    } catch (e) {
      this.setData({ loadingMore: false, initialLoading: false });
    }
  },

  normalizeGoodsList(list) {
    const platformInfo = (app && app.globalData && app.globalData.platformInfo) || {};

    return list.map((item) => {
      const salePolicy = getIOSRestriction(item, platformInfo);
      return {
        ...item,
        coverImage: item.coverImage || item.mainImage || item.imageUrl || '',
        salesCount: item.salesCount !== undefined ? item.salesCount : (item.sales || 0),
        priceText: Number(item.price || 0).toFixed(2),
        cityText: item.cityName || item.city || '同城好物',
        shopText: item.shopName || item.merchantName || '品质店铺',
        goodsType: salePolicy.goodsType,
        goodsTypeLabel: salePolicy.goodsTypeLabel,
        fulfillmentLabel: salePolicy.fulfillmentLabel
      };
    });
  },

  onGoodsTap(e) {
    wx.navigateTo({ url: `/pages/goods/detail?id=${e.currentTarget.dataset.id}` });
  },

  onReachBottom() {
    if (!this.data.hasMore || this.data.loadingMore) return;
    this.loadGoods(false);
  },

  onBack() {
    wx.navigateBack();
  }
});
