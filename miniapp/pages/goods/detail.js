/**
 * 商品详情页（Goods Detail Page）
 * 功能概述：展示商品完整信息，支持加入购物车和立即购买
 * 数据来源：通过 /api/app/goods/{id} 获取商品详情（含图片、商家、评价等）
 * 核心交互：图片轮播预览、数量选择、加入购物车、立即购买、进入店铺、查看评价
 * 特殊逻辑：iOS端对数字权益类商品屏蔽购买入口（符合App Store审核要求）
 */
const { get, post } = require('../../utils/request');
const { hasValidSession } = require('../../utils/auth.js');
const { getIOSRestriction } = require('../../utils/product.js');
const { goodsRules, testEnvironmentNote } = require('../../config/commercialization.js');
const app = getApp();

Page({
  // 页面数据定义
  data: {
    goodsId: null,          // 当前商品ID
    goods: null,            // 商品详情数据（格式化后）
    quantity: 1,            // 用户选择的购买数量
    activeImageIndex: 0,    // 当前轮播图索引
    reviews: [],            // 买家评价列表（最多展示3条）
    loading: true,          // 页面加载状态
    goodsRules,             // 购买规则说明（来自配置文件）
    testEnvironmentNote,    // 测试环境提示信息
    supportLinks: [         // 服务与支持链接列表
      { title: '客服中心', desc: '咨询订单、发货、核销与售后问题', url: '/pages/service/index' },
      { title: '帮助中心', desc: '查看支付、配送、核销和售后常见问题', url: '/pages/help/index' },
      { title: '平台信息', desc: '查看经营主体公示、备案与服务说明', url: '/pages/platform/index' }
    ]
  },

  // 页面加载：从URL参数获取商品ID并加载详情
  onLoad(options) {
    this.setData({ goodsId: Number(options.id) });
    this.loadGoodsDetail();
  },

  // 调用后端接口加载商品详情数据
  async loadGoodsDetail() {
    try {
      const res = await get(`/api/app/goods/${this.data.goodsId}`);
      const goods = this.normalizeGoods(res.data || {});
      this.setData({
        goods,
        reviews: goods.reviews.slice(0, 3),  // 详情页只展示前3条评价
        loading: false
      });
      // 动态设置导航栏标题为商品名称
      wx.setNavigationBarTitle({ title: goods.title || '商品详情' });
    } catch (e) {
      this.setData({ loading: false });
      wx.showToast({ title: '加载失败', icon: 'none' });
    }
  },

  // 格式化商品数据：处理图片列表、商家信息、评价、iOS策略等
  normalizeGoods(goods) {
    // 处理商品图片：优先使用图片列表，兜底使用主图
    const imageUrls = (goods.images || []).map((item) => item.imageUrl || item).filter(Boolean);
    const safeImages = imageUrls.length ? imageUrls : [goods.mainImage || goods.coverImage || ''];
    // 处理评价数据：生成用户名首字母头像
    const reviews = (goods.reviews || []).map((item) => ({
      ...item,
      userInitial: (item.userName || '匿').charAt(0)
    }));
    // 获取iOS展示策略（数字权益类商品在iOS端不展示购买入口）
    const salePolicy = getIOSRestriction(goods, (app.globalData && app.globalData.platformInfo) || {});
    const cityName = goods.cityName || goods.city || '';

    return {
      ...goods,
      ...salePolicy,
      images: safeImages,
      merchantName: goods.shopName || '品质店铺',
      merchantInitial: (goods.shopName || '店').charAt(0),
      merchantRating: Number(goods.avgRating || 5),
      merchantRatingText: Number(goods.avgRating || 5).toFixed(1),
      reviewCount: goods.reviewCount || reviews.length || 0,
      salesCount: goods.sales || goods.salesCount || 0,
      city: cityName,
      reviews,
      priceText: Number(goods.price || 0).toFixed(2),
      originalPriceText: goods.originalPrice ? Number(goods.originalPrice).toFixed(2) : '',
      fulfillmentNotice: this.buildFulfillmentNotice(salePolicy.goodsType),
      serviceTags: [salePolicy.goodsTypeLabel, salePolicy.fulfillmentLabel, cityName || '城市好物']
    };
  },

  // 根据商品类型生成履约方式说明文案
  buildFulfillmentNotice(goodsType) {
    if (goodsType === 'digital') {
      return '该商品属于数字权益，权益发放和使用规则以页面说明为准。为满足 iOS 端审核要求，iOS 端暂不展示购买入口。';
    }

    if (goodsType === 'offline_service') {
      return '该商品属于线下履约服务，下单后请按页面说明预约或到店核销，核销规则以商品页和订单页提示为准。';
    }

    return '该商品属于实物商品，下单后将按收货地址完成发货，可在订单页查看物流、申请售后和联系平台客服。';
  },

  // 轮播图切换回调：更新当前图片索引
  onImageChange(e) {
    this.setData({ activeImageIndex: e.detail.current });
  },

  // 购买数量变化回调
  onQuantityChange(e) {
    this.setData({ quantity: Number(e.detail) });
  },

  // 加入购物车：先检查iOS限制和登录状态，再调用后端接口
  async addToCart() {
    if (this.data.goods?.iosPurchaseBlocked) {
      wx.showToast({ title: 'iOS 端暂不展示该权益购买入口', icon: 'none' });
      return;
    }

    // 未登录则跳转登录页
    if (!hasValidSession(app)) {
      wx.navigateTo({ url: '/pages/login/index' });
      return;
    }

    try {
      await post('/api/app/cart', {
        goodsId: this.data.goodsId,
        quantity: this.data.quantity
      });
      wx.showToast({ title: '已加入购物车', icon: 'success' });
    } catch (e) {}
  },

  // 立即购买：跳转到订单确认页（绕过购物车直接下单）
  buyNow() {
    if (this.data.goods?.iosPurchaseBlocked) {
      wx.showToast({ title: 'iOS 端暂不展示该权益购买入口', icon: 'none' });
      return;
    }

    if (!hasValidSession(app)) {
      wx.navigateTo({ url: '/pages/login/index' });
      return;
    }

    wx.navigateTo({
      url: `/pages/order/confirm?goodsId=${this.data.goodsId}&quantity=${this.data.quantity}`
    });
  },

  // 点击商家卡片：跳转到店铺页面
  onShopTap() {
    const { goods } = this.data;
    if (!goods || !goods.merchantId) return;
    wx.navigateTo({ url: `/pages/shop/index?id=${goods.merchantId}` });
  },

  // 查看全部评价：跳转到评价列表页
  onViewAllReviews() {
    wx.navigateTo({ url: `/pages/review/list?goodsId=${this.data.goodsId}` });
  },

  // 点击商品图片：调用微信图片预览（支持左右滑动查看大图）
  onPreviewImage(e) {
    const { index } = e.currentTarget.dataset;
    const urls = this.data.goods ? this.data.goods.images : [];
    wx.previewImage({ current: urls[index], urls });
  },

  // 服务与支持链接点击：跳转到对应页面
  goSupport(e) {
    const { url } = e.currentTarget.dataset;
    if (url) {
      wx.navigateTo({ url });
    }
  }
});
