const { get, put } = require('../../utils/request');
const { ensurePrivacyForMedia } = require('../../utils/privacy.js');
const { uploadFile } = require('../../utils/upload.js');

Page({
  data: {
    avatar: '',
    nickname: '',
    gender: 0,
    birthday: '',
    genderOptions: ['未知', '男', '女'],
    showDatePicker: false,
    currentDate: new Date().getTime(),
    minDate: new Date(1950, 0, 1).getTime(),
    maxDate: new Date().getTime(),
    loading: false
  },

  onLoad() {
    this.loadProfile();
  },

  async loadProfile() {
    try {
      const res = await get('/api/app/user/profile');
      const data = res.data || {};
      this.setData({
        avatar: data.avatar || '',
        nickname: data.nickname || '',
        gender: data.gender || 0,
        birthday: data.birthday || ''
      });
      if (data.birthday) {
        this.setData({
          currentDate: new Date(data.birthday).getTime()
        });
      }
    } catch (e) {
      console.error('加载个人资料失败', e);
    }
  },

  async chooseAvatar() {
    const allowed = await ensurePrivacyForMedia('上传头像');
    if (!allowed) {
      return;
    }

    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      sizeType: ['compressed'],
      success: async (res) => {
        const tempFiles = res.tempFiles || [];
        if (!tempFiles.length) return;
        try {
          const url = await uploadFile(tempFiles[0].tempFilePath);
          this.setData({ avatar: url });
          wx.showToast({ title: '头像已上传', icon: 'success' });
        } catch (e) {
          this.setData({ avatar: tempFiles[0].tempFilePath });
        }
      }
    });
  },

  onNicknameInput(e) {
    this.setData({ nickname: e.detail.value || e.detail || '' });
  },

  onGenderChange(e) {
    this.setData({ gender: parseInt(e.detail, 10) });
  },

  openDatePicker() {
    this.setData({ showDatePicker: true });
  },

  closeDatePicker() {
    this.setData({ showDatePicker: false });
  },

  onDateConfirm(e) {
    const date = new Date(e.detail);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    this.setData({
      birthday: `${year}-${month}-${day}`,
      currentDate: e.detail,
      showDatePicker: false
    });
  },

  async saveProfile() {
    const { nickname, avatar, gender, birthday } = this.data;
    if (!nickname.trim()) {
      wx.showToast({ title: '请输入昵称', icon: 'none' });
      return;
    }
    this.setData({ loading: true });
    try {
      const params = { nickname: nickname.trim(), gender, birthday };
      if (avatar) {
        params.avatar = avatar;
      }
      await put('/api/app/user/profile', params);
      wx.showToast({ title: '保存成功', icon: 'success' });
      setTimeout(() => wx.navigateBack(), 1500);
    } catch (e) {
      console.error('保存失败', e);
    } finally {
      this.setData({ loading: false });
    }
  }
});
