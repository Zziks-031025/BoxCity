const { get, post, put } = require('../../utils/request');
const { getAreaData, getCodeByName } = require('../../utils/area-helper');

Page({
  data: {
    id: null,
    name: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: false,
    areaValue: '',
    showAreaPicker: false,
    areaText: '',
    areaList: {},
    loading: false
  },

  onLoad(options) {
    this.loadAreaData();
    if (options.id) {
      this.setData({ id: options.id });
      wx.setNavigationBarTitle({ title: '编辑地址' });
      this.loadAddress(options.id);
    } else {
      wx.setNavigationBarTitle({ title: '新增地址' });
    }
  },

  async loadAreaData() {
    try {
      const { areaList, nameToCode } = await getAreaData();
      this._nameToCode = nameToCode;
      this.setData({ areaList });
      this.updateAreaValue();
    } catch (e) {
      console.error('加载地区数据失败', e);
    }
  },

  updateAreaValue() {
    const { province, city, district } = this.data;
    if (province && this._nameToCode) {
      const code = getCodeByName(this._nameToCode, province, city, district);
      if (code) {
        this.setData({ areaValue: code });
      }
    }
  },

  async loadAddress(id) {
    try {
      const res = await get('/api/app/address/list');
      const list = res.data || [];
      const addr = list.find(item => String(item.id) === String(id));
      if (addr) {
        this.setData({
          name: addr.name || '',
          phone: addr.phone || '',
          province: addr.province || '',
          city: addr.city || '',
          district: addr.district || '',
          detail: addr.detail || '',
          isDefault: addr.isDefault === 1 || addr.isDefault === true,
          areaText: `${addr.province || ''}${addr.city || ''}${addr.district || ''}`
        });
        this.updateAreaValue();
      }
    } catch (e) {
      console.error('加载地址失败', e);
    }
  },

  onNameInput(e) {
    this.setData({ name: e.detail });
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail });
  },

  onDetailInput(e) {
    this.setData({ detail: e.detail });
  },

  onDefaultChange(e) {
    this.setData({ isDefault: e.detail });
  },

  openAreaPicker() {
    this.setData({ showAreaPicker: true });
  },

  closeAreaPicker() {
    this.setData({ showAreaPicker: false });
  },

  onAreaConfirm(e) {
    const values = e.detail.values || [];
    if (values.length >= 3 && values[0] && values[1] && values[2]) {
      this.setData({
        province: values[0].name,
        city: values[1].name,
        district: values[2].name,
        areaText: `${values[0].name}${values[1].name}${values[2].name}`,
        areaValue: values[2].code || '',
        showAreaPicker: false
      });
    }
  },

  validate() {
    const { name, phone, province, detail } = this.data;
    if (!name.trim()) {
      wx.showToast({ title: '请输入收货人姓名', icon: 'none' });
      return false;
    }
    if (!phone.trim() || !/^1\d{10}$/.test(phone.trim())) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return false;
    }
    if (!province) {
      wx.showToast({ title: '请选择所在地区', icon: 'none' });
      return false;
    }
    if (!detail.trim()) {
      wx.showToast({ title: '请输入详细地址', icon: 'none' });
      return false;
    }
    return true;
  },

  async saveAddress() {
    if (!this.validate()) return;

    const { id, name, phone, province, city, district, detail, isDefault } = this.data;
    const params = {
      name: name.trim(),
      phone: phone.trim(),
      province,
      city,
      district,
      detail: detail.trim(),
      isDefault: isDefault ? 1 : 0
    };

    this.setData({ loading: true });
    try {
      if (id) {
        await put(`/api/app/address/${id}`, params);
      } else {
        await post('/api/app/address', params);
      }
      wx.showToast({ title: '保存成功', icon: 'success' });
      setTimeout(() => wx.navigateBack(), 1500);
    } catch (e) {
      console.error('保存地址失败', e);
    } finally {
      this.setData({ loading: false });
    }
  }
});
