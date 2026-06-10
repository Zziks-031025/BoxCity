const { complaintTypes } = require('../../config/commercialization.js');
const { ensurePrivacyForMedia } = require('../../utils/privacy.js');
const { uploadImages } = require('../../utils/upload.js');

Page({
  data: {
    complaintTypes,
    complaintTypeIndex: 0,
    description: '',
    orderNo: '',
    refundId: '',
    images: []
  },

  onLoad(options) {
    this.setData({
      orderNo: options.orderNo || '',
      refundId: options.refundId || ''
    });
  },

  onTypeChange(e) {
    this.setData({ complaintTypeIndex: Number(e.detail.value || 0) });
  },

  onDescriptionInput(e) {
    this.setData({ description: e.detail.value || '' });
  },

  onOrderNoInput(e) {
    this.setData({ orderNo: e.detail.value || '' });
  },

  async chooseImages() {
    if (this.data.images.length >= 3) {
      wx.showToast({ title: '最多上传 3 张图片', icon: 'none' });
      return;
    }

    const allowed = await ensurePrivacyForMedia('上传投诉举证图片');
    if (!allowed) {
      return;
    }

    wx.chooseMedia({
      count: 3 - this.data.images.length,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      sizeType: ['compressed'],
      success: (res) => {
        const files = (res.tempFiles || []).map((item) => item.tempFilePath).filter(Boolean);
        this.setData({
          images: this.data.images.concat(files).slice(0, 3)
        });
      }
    });
  },

  async submitComplaint() {
    const description = this.data.description.trim();
    if (!description) {
      wx.showToast({ title: '请填写问题描述', icon: 'none' });
      return;
    }

    let imageUrls = [];
    if (this.data.images.length > 0) {
      try {
        imageUrls = await uploadImages(this.data.images);
      } catch (e) {
        return;
      }
    }

    const record = {
      id: Date.now(),
      type: this.data.complaintTypes[this.data.complaintTypeIndex],
      description,
      orderNo: this.data.orderNo.trim(),
      refundId: this.data.refundId,
      images: imageUrls,
      createdAt: new Date().toISOString()
    };
    const records = wx.getStorageSync('complaintRecords') || [];
    records.unshift(record);
    wx.setStorageSync('complaintRecords', records);

    wx.showToast({ title: '投诉已提交', icon: 'success' });
    setTimeout(() => {
      wx.navigateBack({
        fail() {
          wx.switchTab({ url: '/pages/user/index' });
        }
      });
    }, 1200);
  }
});
