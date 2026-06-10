const { get, put } = require('../../utils/request');

Page({
  data: {
    cityTree: [],
    filteredTree: [],
    currentCity: '',
    currentCityId: null,
    keyword: '',
    loading: true,
    searchResults: []
  },

  onLoad() {
    this.loadCurrentCity();
    this.loadCityTree();
  },

  loadCurrentCity() {
    const cityName = wx.getStorageSync('currentCity') || '';
    const cityId = wx.getStorageSync('currentCityId') || null;
    this.setData({ currentCity: cityName, currentCityId: cityId });
  },

  async loadCityTree() {
    this.setData({ loading: true });
    try {
      const res = await get('/api/common/city/tree');
      const tree = res.data || [];
      this.setData({
        cityTree: tree,
        filteredTree: tree,
        loading: false
      });
    } catch (e) {
      console.error('加载城市列表失败', e);
      this.setData({ loading: false });
    }
  },

  onSearchChange(e) {
    const keyword = (e.detail || '').trim().toLowerCase();
    this.setData({ keyword });
    if (!keyword) {
      this.setData({
        filteredTree: this.data.cityTree,
        searchResults: []
      });
      return;
    }
    // Flatten and search cities
    const results = [];
    this.data.cityTree.forEach(province => {
      const children = province.children || [];
      children.forEach(city => {
        if (city.name && city.name.toLowerCase().includes(keyword)) {
          results.push({
            id: city.id,
            name: city.name,
            provinceName: province.name
          });
        }
      });
    });
    this.setData({ searchResults: results });
  },

  onSearchClear() {
    this.setData({
      keyword: '',
      filteredTree: this.data.cityTree,
      searchResults: []
    });
  },

  async selectCity(e) {
    const { id, name } = e.currentTarget.dataset;
    wx.showLoading({ title: '设置中...' });
    try {
      await put('/api/app/user/profile', { cityId: id });
      wx.setStorageSync('currentCity', name);
      wx.setStorageSync('currentCityId', id);
      this.setData({ currentCity: name, currentCityId: id });
      wx.hideLoading();
      wx.showToast({ title: `已切换到${name}`, icon: 'success' });
      setTimeout(() => wx.navigateBack(), 1500);
    } catch (e) {
      wx.hideLoading();
      console.error('设置城市失败', e);
    }
  },

  toggleProvince(e) {
    const index = e.currentTarget.dataset.index;
    const key = `filteredTree[${index}].expanded`;
    this.setData({
      [key]: !this.data.filteredTree[index].expanded
    });
  }
});
