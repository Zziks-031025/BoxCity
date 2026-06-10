const { get } = require('../../utils/request');

Page({
  data: {
    merchantId: null,
    shop: null,
    goodsList: [],
    page: 1,
    size: 10,
    hasMore: true,
    loading: false
  },

  onLoad(options) {
    const merchantId = Number(options.id || options.merchantId);
    this.setData({ merchantId });
    this.loadShop();
    this.loadGoods(true);
  },

  async loadShop() {
    try {
      const res = await get(`/api/app/shop/${this.data.merchantId}`);
      const payload = res.data || {};
      const shop = {
        ...payload,
        avatarText: (payload.shopName || '店').charAt(0),
        creditScore: payload.creditScore || 0,
        onSaleCount: payload.onSaleCount || payload.goodsCount || 0,
        shopIntro: payload.shopIntro || '店铺暂未填写介绍',
        shopNotice: payload.shopNotice || '',
        contactPhone: payload.contactPhone || '',
        contactEmail: payload.contactEmail || ''
      };
      this.setData({ shop });
      wx.setNavigationBarTitle({ title: shop.shopName || '店铺' });
    } catch (e) {}
  },

  async loadGoods(reset = false) {
    if (this.data.loading) return;
    if (!reset && !this.data.hasMore) return;

    const page = reset ? 1 : this.data.page;
    this.setData({ loading: true });
    try {
      const res = await get(`/api/app/shop/${this.data.merchantId}/goods`, {
        page,
        size: this.data.size
      });
      const payload = res.data || {};
      const records = (payload.records || []).map((item) => ({
        ...item,
        mainImage: item.mainImage || item.coverImage || item.imageUrl || '',
        priceText: Number(item.price || 0).toFixed(2),
        salesText: item.sales || item.salesCount || 0
      }));
      const goodsList = reset ? records : this.data.goodsList.concat(records);

      this.setData({
        goodsList,
        page: page + 1,
        hasMore: goodsList.length < (payload.total || 0)
      });
    } catch (e) {
    } finally {
      this.setData({ loading: false });
    }
  },

  onReachBottom() {
    this.loadGoods(false);
  },

  goGoodsDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/goods/detail?id=${id}` });
  },

  callPhone() {
    if (!this.data.shop?.contactPhone) {
      wx.showToast({ title: '商家暂未留下电话', icon: 'none' });
      return;
    }
    wx.makePhoneCall({ phoneNumber: this.data.shop.contactPhone });
  }
});
