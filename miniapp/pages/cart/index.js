/**
 * 购物车页面（Cart Page）
 * 功能概述：展示用户购物车商品列表，支持选择、修改数量、删除和结算
 * 数据来源：通过 /api/app/cart 接口获取购物车数据，按商家分组展示
 * 核心交互：单选/全选商品、修改数量（Stepper）、编辑模式删除、跳转结算
 */
const { get, put, del } = require('../../utils/request');

Page({
  // 页面数据定义
  data: {
    cartList: [],          // 购物车列表（按商家分组后的数据）
    selectedIds: [],       // 当前选中的购物车项ID数组
    selectedLineCount: 0,  // 已选条目数
    totalPrice: '0.00',    // 选中商品合计金额
    totalCount: 0,         // 选中商品总件数
    itemCount: 0,          // 购物车商品总条目数
    isAllSelected: false,  // 是否全选
    editing: false         // 是否处于编辑模式（编辑模式下显示删除按钮）
  },

  // 每次页面显示时重新加载购物车（从详情页返回时能刷新数据）
  onShow() {
    this.loadCart();
  },

  // 从后端加载购物车数据
  async loadCart() {
    try {
      const res = await get('/api/app/cart');
      // 将扁平列表按商家分组，再计算选中状态
      this.applyCartState(this.groupByMerchant(res.data || []));
    } catch (e) {}
  },

  // 将购物车列表按 merchantId 分组，每组包含商家名和商品列表
  groupByMerchant(list) {
    const map = {};
    list.forEach((item) => {
      const merchantId = item.merchantId;
      if (!map[merchantId]) {
        map[merchantId] = {
          merchantId,
          shopName: item.shopName || '品质店铺',
          items: []
        };
      }
      // 每个商品默认未选中
      map[merchantId].items.push({ ...item, selected: false });
    });
    return this.normalizeCartList(Object.values(map));
  },

  // 计算每个商家分组的全选状态
  normalizeCartList(cartList) {
    return cartList.map((shop) => ({
      ...shop,
      allSelected: shop.items.length > 0 && shop.items.every((item) => item.selected)
    }));
  },

  // 根据当前选中状态，重新计算合计金额、数量等汇总数据
  applyCartState(cartList) {
    const normalized = this.normalizeCartList(cartList);
    const selectedIds = [];
    let totalPrice = 0;
    let totalCount = 0;
    let itemCount = 0;
    let selectedLineCount = 0;

    normalized.forEach((shop) => {
      shop.items.forEach((item) => {
        itemCount += 1;
        if (item.selected) {
          selectedLineCount += 1;
          selectedIds.push(item.id);
          totalPrice += Number(item.price || 0) * Number(item.quantity || 0);
          totalCount += Number(item.quantity || 0);
        }
      });
    });

    const isAllSelected = itemCount > 0 && normalized.every((shop) => shop.allSelected);

    this.setData({
      cartList: normalized,
      selectedIds,
      selectedLineCount,
      totalPrice: totalPrice.toFixed(2),
      totalCount,
      itemCount,
      isAllSelected
    });
  },

  // 单个商品选中/取消选中
  toggleSelect(e) {
    const { merchantIndex, itemIndex } = e.currentTarget.dataset;
    const current = this.data.cartList[merchantIndex].items[itemIndex].selected;
    this.setData({
      [`cartList[${merchantIndex}].items[${itemIndex}].selected`]: !current
    }, () => {
      this.applyCartState(this.data.cartList);
    });
  },

  // 商家级别全选/取消全选
  toggleShopSelect(e) {
    const { merchantIndex } = e.currentTarget.dataset;
    const shop = this.data.cartList[merchantIndex];
    const next = !shop.allSelected;
    const updates = {};
    shop.items.forEach((_, idx) => {
      updates[`cartList[${merchantIndex}].items[${idx}].selected`] = next;
    });
    this.setData(updates, () => {
      this.applyCartState(this.data.cartList);
    });
  },

  // 全选/取消全选所有商品
  toggleAllSelect() {
    const next = !this.data.isAllSelected;
    const updates = {};
    this.data.cartList.forEach((shop, shopIndex) => {
      shop.items.forEach((_, itemIndex) => {
        updates[`cartList[${shopIndex}].items[${itemIndex}].selected`] = next;
      });
    });
    this.setData(updates, () => {
      this.applyCartState(this.data.cartList);
    });
  },

  // 调用后端接口更新商品数量
  async updateQuantity(e) {
    const { cartId, quantity } = e.currentTarget.dataset;
    if (quantity < 1) return;
    try {
      await put(`/api/app/cart/${cartId}`, { quantity });
      this.loadCart();
    } catch (e) {}
  },

  // Stepper组件数量变化回调
  onStepperChange(e) {
    const { cartId } = e.currentTarget.dataset;
    const quantity = e.detail;
    this.updateQuantity({ currentTarget: { dataset: { cartId, quantity } } });
  },

  // 删除单个购物车商品
  async removeItem(e) {
    const cartId = e.currentTarget.dataset.cartId;
    try {
      await del(`/api/app/cart/${cartId}`);
      this.loadCart();
    } catch (e) {}
  },

  // 批量删除选中的商品（弹出确认对话框）
  async removeSelected() {
    if (this.data.selectedIds.length === 0) {
      wx.showToast({ title: '请选择要删除的商品', icon: 'none' });
      return;
    }

    wx.showModal({
      title: '删除商品',
      content: `确认删除已选中的 ${this.data.selectedIds.length} 件商品吗？`,
      success: async (res) => {
        if (res.confirm) {
          try {
            for (const id of this.data.selectedIds) {
              await del(`/api/app/cart/${id}`);
            }
            this.loadCart();
          } catch (e) {}
        }
      }
    });
  },

  // 切换编辑模式（编辑模式下显示删除按钮，隐藏数量步进器）
  toggleEditing() {
    this.setData({ editing: !this.data.editing });
  },

  // 购物车为空时，点击"去逛逛"跳转到首页
  goShopping() {
    wx.switchTab({ url: '/pages/home/index' });
  },

  // 点击商品图片或信息区域，跳转到商品详情页
  goGoodsDetail(e) {
    const { goodsId } = e.currentTarget.dataset;
    if (goodsId) {
      wx.navigateTo({ url: `/pages/goods/detail?id=${goodsId}` });
    }
  },

  // 去结算：将选中的购物车ID传递给订单确认页
  goCheckout() {
    if (this.data.selectedIds.length === 0) {
      wx.showToast({ title: '请选择商品', icon: 'none' });
      return;
    }
    const cartIds = this.data.selectedIds.join(',');
    wx.navigateTo({ url: `/pages/order/confirm?cartIds=${cartIds}` });
  }
});
