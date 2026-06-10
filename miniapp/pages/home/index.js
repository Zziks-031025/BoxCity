/**
 * 首页（Home Page）
 * 功能概述：展示轮播图、分类入口、推荐商品和热门商品列表
 * 数据来源：通过 /api/app/goods/home 接口一次性获取首页所有数据
 */
const { get } = require('../../utils/request');
const { getIOSRestriction } = require('../../utils/product.js');
const app = getApp();

Page({
  // 页面数据定义
  data: {
    banners: [],       // 轮播图列表
    categories: [],    // 分类列表（最多展示8个）
    recommended: [],   // 推荐商品列表
    hotGoods: [],      // 热门商品列表
    heroStats: [],     // 顶部统计数据（店铺数、推荐数、热门数）
    loading: true,     // 页面加载状态
    currentBannerIndex: 0  // 当前轮播图索引
  },

  // 页面加载时触发，调用首页数据加载方法
  onLoad() {
    this.loadHomeData();
  },

  // 加载首页数据：调用后端接口获取轮播图、分类、推荐和热门商品
  async loadHomeData() {
    try {
      const res = await get('/api/app/goods/home');
      const payload = res.data || {};
      const banners = payload.banners || [];
      // 对分类和商品数据进行格式化处理
      const categories = this.normalizeCategories(payload.categories || []);
      const recommended = this.normalizeGoodsList(payload.recommended || []);
      const hotGoods = this.normalizeGoodsList(payload.hotGoods || []);

      this.setData({
        banners,
        categories,
        recommended,
        hotGoods,
        // 构建顶部统计卡片数据
        heroStats: [
          { label: '精选店铺', value: String(categories.length || 8) },
          { label: '推荐好物', value: String(recommended.length || 0) },
          { label: '热门清单', value: String(hotGoods.length || 0) }
        ],
        loading: false
      });
    } catch (e) {
      this.setData({ loading: false });
    }
  },

  // 格式化分类数据：截取前8个，生成首字母和描述文字
  normalizeCategories(list) {
    return list.slice(0, 8).map((item) => ({
      ...item,
      initial: (item.name || '类').charAt(0),
      desc: item.children?.length ? `${item.children.length} 个子类` : '城市精选'
    }));
  },

  // 格式化商品列表数据：统一字段名、处理iOS展示策略
  normalizeGoodsList(list) {
    const platformInfo = (app && app.globalData && app.globalData.platformInfo) || {};

    return list.map((item) => {
      // 获取iOS端展示限制策略（数字权益类商品在iOS端不展示购买入口）
      const salePolicy = getIOSRestriction(item, platformInfo);
      return {
        ...item,
        coverImage: item.coverImage || item.mainImage || item.imageUrl || '',
        salesCount: item.salesCount !== undefined ? item.salesCount : (item.sales || 0),
        cityText: item.cityName || item.city || '同城好物',
        shopText: item.shopName || item.merchantName || '品质店铺',
        priceText: Number(item.price || 0).toFixed(2),
        goodsType: salePolicy.goodsType,
        goodsTypeLabel: salePolicy.goodsTypeLabel,
        fulfillmentLabel: salePolicy.fulfillmentLabel
      };
    });
  },

  // 轮播图切换回调：记录当前索引
  onBannerChange(e) {
    this.setData({ currentBannerIndex: e.detail.current });
  },

  // 轮播图点击事件：通过当前索引获取对应banner的链接并跳转
  onBannerTap() {
    const banner = this.data.banners[this.data.currentBannerIndex];
    if (!banner || !banner.linkUrl) return;
    const url = banner.linkUrl;
    if (!url.startsWith('/pages')) return;

    // tabBar 页面无法使用 navigateTo，需要用 switchTab + globalData 传参
    const tabBarPages = ['/pages/home/index', '/pages/category/index', '/pages/cart/index', '/pages/user/index'];
    const pagePath = url.split('?')[0];
    if (tabBarPages.includes(pagePath)) {
      // 将参数存入 globalData，目标页面在 onShow 中读取
      const params = {};
      const queryStr = url.split('?')[1];
      if (queryStr) {
        queryStr.split('&').forEach(pair => {
          const [key, val] = pair.split('=');
          params[key] = decodeURIComponent(val || '');
        });
      }
      app.globalData.tabNavParams = { path: pagePath, params };
      wx.switchTab({ url: pagePath });
    } else {
      wx.navigateTo({ url });
    }
  },

  // 分类点击事件：跳转到分类页并传递分类ID
  onCategoryTap(e) {
    wx.navigateTo({ url: `/pages/category/index?id=${e.currentTarget.dataset.id}` });
  },

  // 商品点击事件：跳转到商品详情页
  onGoodsTap(e) {
    wx.navigateTo({ url: `/pages/goods/detail?id=${e.currentTarget.dataset.id}` });
  },

  // 搜索栏点击事件：跳转到搜索页
  onSearchTap() {
    wx.navigateTo({ url: '/pages/search/index' });
  },

  // 下拉刷新：重新加载首页数据
  onPullDownRefresh() {
    this.loadHomeData().finally(() => {
      wx.stopPullDownRefresh();
    });
  }
});
