const { get, post } = require('../../utils/request');
const { uploadImages } = require('../../utils/upload');
const { ensurePrivacyForMedia } = require('../../utils/privacy.js');

Page({
  data: {
    orderNo: '',
    items: [],
    selectedItemId: null,
    rating: 5,
    content: '',
    images: [],
    submitting: false
  },

  onLoad(options) {
    this.setData({ orderNo: options.orderNo || '' });
    this.loadOrder();
  },

  async loadOrder() {
    if (!this.data.orderNo) return;
    try {
      const res = await get(`/api/app/order/${this.data.orderNo}`);
      const order = res.data || {};
      const items = (order.items || order.orderItems || []).map((item) => ({
        ...item,
        title: item.goodsTitle || item.goodsName,
        image: item.mainImage || item.imageUrl,
        reviewed: !!item.reviewed
      }));
      const firstPending = items.find((item) => !item.reviewed) || null;
      this.setData({
        items,
        selectedItemId: firstPending ? firstPending.id : null
      });
    } catch (e) {}
  },

  onSelectItem(e) {
    const id = Number(e.currentTarget.dataset.id);
    const item = this.data.items.find((entry) => entry.id === id);
    if (!item || item.reviewed) {
      return;
    }
    this.setData({ selectedItemId: id });
  },

  onRateChange(e) {
    this.setData({ rating: Number(e.detail) });
  },

  onContentInput(e) {
    this.setData({ content: e.detail });
  },

  chooseImages() {
    const remain = 3 - this.data.images.length;
    if (remain <= 0) {
      wx.showToast({ title: '最多选择 3 张图片', icon: 'none' });
      return;
    }

    const self = this;
    ensurePrivacyForMedia('上传评价图片').then((allowed) => {
      if (!allowed) return;
      wx.chooseMedia({
        count: remain,
        mediaType: ['image'],
        sourceType: ['album', 'camera'],
        sizeType: ['compressed'],
        success(res) {
          const paths = res.tempFiles.map((f) => f.tempFilePath);
          self.setData({ images: self.data.images.concat(paths) });
        },
        fail(err) {
          console.error('chooseMedia fail', err);
        }
      });
    });
  },

  removeImage(e) {
    const index = Number(e.currentTarget.dataset.index);
    const images = this.data.images.slice();
    images.splice(index, 1);
    this.setData({ images });
  },

  async submitReview() {
    if (!this.data.selectedItemId) {
      wx.showToast({ title: '该订单没有可评价商品', icon: 'none' });
      return;
    }
    if (this.data.submitting) return;

    this.setData({ submitting: true });
    try {
      let imageUrls = [];
      if (this.data.images.length > 0) {
        imageUrls = await uploadImages(this.data.images);
      }
      await post('/api/app/review', {
        orderNo: this.data.orderNo,
        orderItemId: this.data.selectedItemId,
        rating: this.data.rating,
        content: this.data.content.trim(),
        images: imageUrls,
        isAppend: 0
      });
      wx.showToast({ title: '评价成功', icon: 'success' });
      setTimeout(() => {
        wx.redirectTo({ url: `/pages/order/detail?orderNo=${this.data.orderNo}` });
      }, 800);
    } catch (e) {
    } finally {
      this.setData({ submitting: false });
    }
  }
});
