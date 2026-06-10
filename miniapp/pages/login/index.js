const app = getApp();
const { post } = require('../../utils/request');
const { hasValidSession, saveSession } = require('../../utils/auth.js');
const { openPrivacyGuide } = require('../../utils/privacy.js');

Page({
  data: {
    step: 'login',
    phone: '',
    smsCode: '',
    countdown: 0,
    loading: false
  },

  onLoad() {
    if (hasValidSession(app)) {
      this.navigateToHome();
    }
  },

  handleWxLogin() {
    this.setData({ loading: true });
    wx.login({
      success: (res) => {
        if (!res.code) {
          this.setData({ loading: false });
          wx.showToast({ title: '未获取到登录凭证', icon: 'none' });
          return;
        }

        post('/api/app/auth/wxLogin', { code: res.code }, false)
          .then((result) => {
            const data = result.data || {};
            saveSession(data.token, app);

            if (!data.phoneBound) {
              this.setData({ step: 'bindPhone', loading: false });
            } else {
              this.navigateToHome();
            }
          })
          .catch(() => {
            this.setData({ loading: false });
          });
      },
      fail: () => {
        this.setData({ loading: false });
        wx.showToast({ title: '微信登录失败', icon: 'none' });
      }
    });
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value || e.detail || '' });
  },

  onCodeInput(e) {
    this.setData({ smsCode: e.detail.value || e.detail || '' });
  },

  sendSmsCode() {
    const { phone, countdown } = this.data;
    if (countdown > 0) return;

    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return;
    }

    post('/api/app/auth/sendSms', { phone })
      .then(() => {
        wx.showToast({ title: '验证码已发送', icon: 'success' });
        this.startCountdown();
      });
  },

  startCountdown() {
    this.setData({ countdown: 60 });
    const timer = setInterval(() => {
      if (this.data.countdown <= 1) {
        clearInterval(timer);
        this.setData({ countdown: 0 });
      } else {
        this.setData({ countdown: this.data.countdown - 1 });
      }
    }, 1000);
  },

  handleBindPhone() {
    const { phone, smsCode } = this.data;

    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return;
    }

    if (!smsCode || smsCode.length < 4) {
      wx.showToast({ title: '请输入验证码', icon: 'none' });
      return;
    }

    this.setData({ loading: true });
    post('/api/app/auth/bindPhone', { phone, code: smsCode })
      .then(() => {
        wx.showToast({ title: '绑定成功', icon: 'success' });
        setTimeout(() => this.navigateToHome(), 1000);
      })
      .catch(() => {
        this.setData({ loading: false });
      });
  },

  goAgreement() {
    wx.navigateTo({ url: '/pages/agreement/index' });
  },

  goPrivacy() {
    wx.navigateTo({ url: '/pages/privacy/index' });
  },

  goPlatformInfo() {
    wx.navigateTo({ url: '/pages/platform/index' });
  },

  openPrivacyGuide() {
    openPrivacyGuide('/pages/privacy/index');
  },

  navigateToHome() {
    wx.switchTab({ url: '/pages/home/index' });
  }
});
