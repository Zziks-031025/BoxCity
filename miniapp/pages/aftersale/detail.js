const { get, put } = require('../../utils/request');
const { aftersaleRules, testEnvironmentNote } = require('../../config/commercialization.js');

const STATUS_TEXT_MAP = {
  0: '等待商家处理',
  1: '商家已同意，等待退货',
  2: '退货途中',
  3: '退款成功',
  4: '商家已拒绝',
  5: '平台介入处理中',
  6: '售后已关闭'
};

Page({
  data: {
    refundId: '',
    refund: null,
    logisticsCompany: '',
    logisticsNo: '',
    aftersaleRules,
    testEnvironmentNote,
    supportLinks: []
  },

  onLoad(options) {
    const refundId = options.refundId || '';
    this.setData({ refundId });
    this.setupSupportLinks(refundId);
    if (this.data.refundId) {
      this.loadRefundDetail();
    }
  },

  setupSupportLinks(refundId) {
    this.setData({
      supportLinks: [
        { title: '客服中心', desc: '咨询售后时效、退货物流与平台处理', url: '/pages/service/index' },
        { title: '投诉举报', desc: '对售后处理结果存在争议时可提交', url: `/pages/complaint/create?refundId=${refundId}` },
        { title: '帮助中心', desc: '查看售后常见问题与说明', url: '/pages/help/index' }
      ]
    });
  },

  async loadRefundDetail() {
    try {
      const res = await get(`/api/app/refund/${this.data.refundId}`);
      const refund = this.normalizeRefund(res.data || {});
      this.setData({ refund });
    } catch (e) {}
  },

  normalizeRefund(refund) {
    return {
      ...refund,
      statusText: STATUS_TEXT_MAP[refund.status] || '未知状态'
    };
  },

  onLogisticsCompanyInput(e) {
    this.setData({ logisticsCompany: e.detail.value || e.detail || '' });
  },

  onLogisticsNoInput(e) {
    this.setData({ logisticsNo: e.detail.value || e.detail || '' });
  },

  async cancelRefund() {
    try {
      await put(`/api/app/refund/${this.data.refundId}/cancel`, {});
      wx.showToast({ title: '已撤销', icon: 'success' });
      this.loadRefundDetail();
    } catch (e) {}
  },

  async submitReturnLogistics() {
    if (!this.data.logisticsCompany.trim() || !this.data.logisticsNo.trim()) {
      wx.showToast({ title: '请填写完整物流信息', icon: 'none' });
      return;
    }
    try {
      await put(`/api/app/refund/${this.data.refundId}/logistics`, {
        logisticsCompany: this.data.logisticsCompany.trim(),
        logisticsNo: this.data.logisticsNo.trim()
      });
      wx.showToast({ title: '已提交', icon: 'success' });
      this.loadRefundDetail();
    } catch (e) {}
  },

  async requestIntervene() {
    wx.showModal({
      title: '申请平台介入',
      content: '商家已拒绝当前售后申请，是否提交平台进一步核验处理？',
      success: async (res) => {
        if (!res.confirm) return;
        try {
          await put(`/api/app/refund/${this.data.refundId}/intervene`, {
            reason: '用户在小程序端发起平台介入'
          });
          wx.showToast({ title: '已提交平台介入', icon: 'success' });
          this.loadRefundDetail();
        } catch (e) {}
      }
    });
  },

  goSupport(e) {
    const { url } = e.currentTarget.dataset;
    if (url) {
      wx.navigateTo({ url });
    }
  }
});
