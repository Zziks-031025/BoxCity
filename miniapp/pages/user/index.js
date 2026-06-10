const { get } = require('../../utils/request');
const { hasValidSession } = require('../../utils/auth.js');
const app = getApp();

Page({
  data: {
    userInfo: {
      nickname: '',
      avatar: '',
      phone: ''
    },
    orderCounts: {
      unpaid: 0,
      unshipped: 0,
      unreceived: 0,
      completed: 0
    },
    isLogin: false,
    welcomeText: '登录后可同步订单、地址、发票和售后进度',
    menuSections: []
  },

  onLoad() {
    this.setupMenuSections();
  },

  onShow() {
    this.setupMenuSections();

    if (hasValidSession(app)) {
      this.setData({
        isLogin: true,
        welcomeText: '欢迎回来，今天也去逛逛新的同城好物吧'
      });
      this.loadUserProfile();
    } else {
      this.setData({
        isLogin: false,
        userInfo: { nickname: '', avatar: '', phone: '' },
        orderCounts: { unpaid: 0, unshipped: 0, unreceived: 0, completed: 0 },
        welcomeText: '登录后可同步订单、地址、发票和售后进度'
      });
    }
  },

  setupMenuSections() {
    this.setData({
      menuSections: [
        {
          title: '交易与保障',
          subtitle: '订单履约、地址与票据相关能力',
          items: [
            { title: '收货地址', desc: '管理常用收货地址', icon: 'location-o', url: '/pages/address/list', needLogin: true },
            { title: '发票与凭证', desc: '提交测试环境的开票申请', icon: 'description', url: '/pages/invoice/index', needLogin: true },
            { title: '城市选择', desc: '切换当前浏览城市', icon: 'map-marked', url: '/pages/user/city', needLogin: true }
          ]
        },
        {
          title: '客户支持',
          subtitle: '帮助、客服、投诉与隐私咨询入口',
          items: [
            { title: '帮助中心', desc: '查看支付、发货、售后常见问题', icon: 'question-o', url: '/pages/help/index', needLogin: false },
            { title: '客服中心', desc: '查看平台客服热线与邮箱', icon: 'service-o', url: '/pages/service/index', needLogin: false },
            { title: '投诉举报', desc: '提交订单争议、服务或隐私问题', icon: 'warning-o', url: '/pages/complaint/create', needLogin: false }
          ]
        },
        {
          title: '平台与账号',
          subtitle: '查看公示信息、协议与账号处理说明',
          items: [
            { title: '平台信息', desc: '查看主体公示、备案与经营说明', icon: 'info-o', url: '/pages/platform/index', needLogin: false },
            { title: '用户协议', desc: '阅读服务协议正文', icon: 'notes-o', url: '/pages/agreement/index', needLogin: false },
            { title: '隐私政策', desc: '查看信息使用与授权说明', icon: 'shield-o', url: '/pages/privacy/index', needLogin: false },
            { title: '账号注销', desc: '提交账号注销测试申请', icon: 'delete-o', url: '/pages/account/cancel', needLogin: true }
          ]
        }
      ]
    });
  },

  async loadUserProfile() {
    try {
      const res = await get('/api/app/user/profile', {}, false);
      const data = res.data || {};
      this.setData({
        userInfo: {
          nickname: data.nickname || '盒集用户',
          avatar: data.avatar || '',
          phone: data.phone ? data.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : ''
        },
        orderCounts: {
          unpaid: data.unpaidCount || 0,
          unshipped: data.unshippedCount || 0,
          unreceived: data.unreceivedCount || 0,
          completed: data.completedCount || 0
        }
      });
    } catch (e) {
      console.error('加载用户信息失败', e);
    }
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/index' });
  },

  onProfileTap() {
    if (this.data.isLogin) {
      this.goProfile();
    } else {
      this.goLogin();
    }
  },

  goProfile() {
    if (!this.data.isLogin) return this.goLogin();
    wx.navigateTo({ url: '/pages/user/profile' });
  },

  goOrderList(e) {
    if (!this.data.isLogin) return this.goLogin();
    const status = e.currentTarget.dataset.status || '';
    wx.navigateTo({ url: `/pages/order/list?status=${status}` });
  },

  goAllOrders() {
    if (!this.data.isLogin) return this.goLogin();
    wx.navigateTo({ url: '/pages/order/list' });
  },

  onMenuTap(e) {
    const { url, needLogin } = e.currentTarget.dataset;
    if (needLogin && !this.data.isLogin) {
      this.goLogin();
      return;
    }

    if (url) {
      wx.navigateTo({ url });
    }
  }
});
