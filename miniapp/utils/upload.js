const app = getApp();
const { getValidToken } = require('./auth.js');

const uploadFile = (filePath) => {
  return new Promise((resolve, reject) => {
    const token = getValidToken(app);
    const baseUrl = app.globalData.baseUrl || 'http://localhost:8080';

    wx.uploadFile({
      url: baseUrl + '/api/app/upload',
      filePath,
      name: 'file',
      header: token ? { Authorization: `Bearer ${token}` } : {},
      success(res) {
        try {
          const data = JSON.parse(res.data);
          if (data.code === 200 || data.code === 0) {
            resolve(data.data);
          } else {
            wx.showToast({ title: data.msg || '上传失败', icon: 'none' });
            reject(data);
          }
        } catch (e) {
          reject(e);
        }
      },
      fail(err) {
        wx.showToast({ title: '上传失败', icon: 'none' });
        reject(err);
      }
    });
  });
};

const uploadImages = async (filePaths) => {
  const urls = [];
  for (const path of filePaths) {
    const url = await uploadFile(path);
    urls.push(url);
  }
  return urls;
};

module.exports = { uploadFile, uploadImages };
