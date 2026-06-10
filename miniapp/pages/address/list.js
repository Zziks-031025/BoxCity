const { get, del, put } = require('../../utils/request');

Page({
  data: {
    addressList: [],
    loading: true,
    selectMode: false
  },

  onLoad(options) {
    this.setData({ selectMode: options.select === '1' });
  },

  onShow() {
    this.loadAddressList();
  },

  async loadAddressList() {
    this.setData({ loading: true });
    try {
      const res = await get('/api/app/address/list');
      this.setData({
        addressList: res.data || [],
        loading: false
      });
    } catch (e) {
      console.error('加载地址列表失败', e);
      this.setData({ loading: false });
    }
  },

  addAddress() {
    wx.navigateTo({ url: '/pages/address/edit' });
  },

  handleAddressTap(e) {
    const id = e.currentTarget.dataset.id;
    if (!this.data.selectMode) {
      this.editAddress(e);
      return;
    }

    const selected = this.data.addressList.find(item => String(item.id) === String(id));
    if (!selected) return;
    const app = getApp();
    app.globalData.selectedAddress = selected;
    wx.navigateBack();
  },

  editAddress(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/address/edit?id=${id}` });
  },

  deleteAddress(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '提示',
      content: '确定要删除该地址吗？',
      confirmColor: '#FF6B35',
      success: async (res) => {
        if (res.confirm) {
          try {
            await del(`/api/app/address/${id}`);
            wx.showToast({ title: '删除成功', icon: 'success' });
            this.loadAddressList();
          } catch (e) {
            console.error('删除地址失败', e);
          }
        }
      }
    });
  },

  async setDefault(e) {
    const id = e.currentTarget.dataset.id;
    try {
      await put(`/api/app/address/${id}/default`);
      wx.showToast({ title: '设置成功', icon: 'success' });
      this.loadAddressList();
    } catch (e) {
      console.error('设置默认地址失败', e);
    }
  },

  onSwipeClose(e) {
    const { position, instance } = e.detail;
    switch (position) {
      case 'right':
        instance.close();
        this.deleteAddress(e);
        break;
    }
  }
});
