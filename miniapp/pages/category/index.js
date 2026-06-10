/**
 * 分类页面（Category Page）
 * 功能概述：左侧分类导航 + 右侧商品列表，支持子分类筛选和排序
 * 布局模式：左右分栏（Sidebar + Content），类似电商App的经典分类页
 * 数据来源：分类树通过 /api/common/category/tree 获取，商品通过 /api/app/goods/list 分页加载
 */
const { get } = require('../../utils/request');

Page({
  // 页面数据定义
  data: {
    categories: [],        // 一级分类列表（左侧导航栏数据）
    activeCategory: 0,     // 当前选中的一级分类索引
    activeCategoryInfo: null, // 当前选中分类的完整信息
    subCategories: [],     // 当前一级分类下的子分类列表
    activeSubId: '',       // 当前选中的子分类ID（空字符串表示"全部"）
    goodsList: [],         // 商品列表数据
    page: 1,              // 当前分页页码
    pageSize: 20,         // 每页商品数量
    hasMore: true,        // 是否还有更多数据可加载
    loadingMore: false,   // 是否正在加载更多
    resultCount: 0,       // 当前筛选条件下的商品总数
    filters: {            // 筛选条件
      cityId: '',
      minPrice: '',
      maxPrice: '',
      sort: 'newest'      // 默认按最新上架排序
    },
    sortOptions: [        // 排序选项
      { label: '最新上架', value: 'newest' },
      { label: '销量优先', value: 'sales' },
      { label: '价格从低到高', value: 'price_asc' },
      { label: '价格从高到低', value: 'price_desc' }
    ]
  },

  // 页面加载：支持从首页传入分类ID直接定位到对应分类
  onLoad(options) {
    this._targetCategoryId = options.id || '';
    this.loadCategories();
  },

  // 页面每次显示时检查是否有从 tabBar 跳转传入的参数
  onShow() {
    const app = getApp();
    const navParams = app.globalData.tabNavParams;
    if (navParams && navParams.path === '/pages/category/index') {
      const newId = navParams.params.id || '';
      app.globalData.tabNavParams = null;
      if (newId && newId !== this._targetCategoryId) {
        this._targetCategoryId = newId;
        this.loadCategories();
      }
    }
  },

  // 加载分类树数据，并根据传入的ID定位到对应分类
  async loadCategories() {
    try {
      const res = await get('/api/common/category/tree');
      const categories = (res.data || []).map((item) => ({
        ...item,
        summary: item.children?.length
          ? `下含 ${item.children.length} 个细分类，帮你更快找到想逛的内容。`
          : '聚合同类好物与人气精选，适合慢慢挑。'
      }));

      // 如果从首页传入了分类ID，定位到该分类
      let activeIndex = 0;
      if (this._targetCategoryId) {
        const idx = categories.findIndex((c) => String(c.id) === String(this._targetCategoryId));
        if (idx >= 0) activeIndex = idx;
      }

      const currentCategory = categories[activeIndex] || null;
      this.setData({
        categories,
        activeCategory: activeIndex,
        activeCategoryInfo: currentCategory,
        subCategories: currentCategory?.children || []
      });
      this.resetAndLoadGoods();
    } catch (e) {
      console.error('loadCategories error', e);
    }
  },

  // 左侧一级分类切换事件（Vant Sidebar组件回调）
  onCategoryChange(e) {
    const index = e.detail;
    const currentCategory = this.data.categories[index] || null;
    this.setData({
      activeCategory: index,
      activeCategoryInfo: currentCategory,
      subCategories: currentCategory?.children || [],
      activeSubId: ''
    });
    this.resetAndLoadGoods();
  },

  // 子分类标签点击：再次点击同一个子分类则取消选中（回到"全部"）
  onSubCategoryChange(e) {
    const subId = e.currentTarget.dataset.id;
    this.setData({ activeSubId: subId === this.data.activeSubId ? '' : subId });
    this.resetAndLoadGoods();
  },

  // 排序方式切换
  onSortChange(e) {
    const sort = e.currentTarget.dataset.sort;
    this.setData({ 'filters.sort': sort });
    this.resetAndLoadGoods();
  },

  // 重置分页并重新加载商品（切换分类/子分类/排序时调用）
  resetAndLoadGoods() {
    this.setData({ goodsList: [], page: 1, hasMore: true, resultCount: 0 });
    this.loadGoods(true);
  },

  // 加载商品列表（支持分页加载更多）
  async loadGoods(reset = false) {
    const { categories, activeCategory, activeSubId, page, pageSize, filters, loadingMore } = this.data;
    if (loadingMore) return;

    const currentCat = categories[activeCategory];
    if (!currentCat) return;

    this.setData({ loadingMore: true });
    try {
      // 构建请求参数：优先使用子分类ID，否则使用一级分类ID
      const params = {
        categoryId: activeSubId || currentCat.id,
        page: reset ? 1 : page,
        size: pageSize,
        sort: filters.sort
      };

      if (filters.cityId) params.cityId = filters.cityId;
      if (filters.minPrice !== '') params.minPrice = filters.minPrice;
      if (filters.maxPrice !== '') params.maxPrice = filters.maxPrice;

      const res = await get('/api/app/goods/list', params);
      const payload = res.data || {};
      const list = this.normalizeGoodsList(payload.records || payload.list || []);
      const total = payload.total || list.length;
      const nextPage = reset ? 2 : page + 1;
      // 重置时替换列表，加载更多时追加到列表末尾
      const newList = reset ? list : [...this.data.goodsList, ...list];

      this.setData({
        goodsList: newList,
        page: nextPage,
        hasMore: newList.length < total,
        loadingMore: false,
        resultCount: total
      });
    } catch (e) {
      this.setData({ loadingMore: false });
    }
  },

  // 格式化商品数据：统一封面图、销量、价格等字段
  normalizeGoodsList(list) {
    return list.map((item) => ({
      ...item,
      coverImage: item.coverImage || item.mainImage || item.imageUrl || '',
      salesCount: item.salesCount !== undefined ? item.salesCount : (item.sales || 0),
      priceText: Number(item.price || 0).toFixed(2),
      cityText: item.cityName || item.city || '同城精选',
      shopText: item.shopName || item.merchantName || '品质店铺'
    }));
  },

  // 商品点击：跳转到商品详情页
  onGoodsTap(e) {
    wx.navigateTo({ url: `/pages/goods/detail?id=${e.currentTarget.dataset.id}` });
  },

  // 滚动到底部时触发加载更多
  onScrollToLower() {
    if (!this.data.hasMore || this.data.loadingMore) return;
    this.loadGoods(false);
  }
});
