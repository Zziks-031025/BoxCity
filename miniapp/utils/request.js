const app = getApp();
const { clearSession, getValidToken } = require('./auth.js');

const getBaseUrlCandidates = () => {
  const candidates = [];

  if (app.globalData.baseUrl) {
    candidates.push(app.globalData.baseUrl);
  }

  if (Array.isArray(app.globalData.baseUrlCandidates)) {
    app.globalData.baseUrlCandidates.forEach((baseUrl) => {
      if (baseUrl && !candidates.includes(baseUrl)) {
        candidates.push(baseUrl);
      }
    });
  }

  return candidates;
};

const buildHeader = () => {
  const token = getValidToken(app);
  const header = {
    'Content-Type': 'application/json'
  };

  if (token) {
    header.Authorization = `Bearer ${token}`;
  }

  return header;
};

const requestWithBaseUrl = (baseUrl, url, method, data) => {
  return new Promise((resolve, reject) => {
    wx.request({
      url: baseUrl + url,
      method,
      data,
      header: buildHeader(),
      success(res) {
        resolve({ baseUrl, res });
      },
      fail(err) {
        reject({ baseUrl, err });
      }
    });
  });
};

const handleResponse = (res, resolve, reject) => {
  if (res.statusCode === 200) {
    if (res.data.code === 0 || res.data.code === 200) {
      resolve(res.data);
    } else {
      wx.showToast({ title: res.data.msg || '请求失败', icon: 'none' });
      reject(res.data);
    }
  } else if (res.statusCode === 401) {
    clearSession(app);
    wx.showToast({ title: '登录已过期，请重新登录', icon: 'none' });
    setTimeout(() => {
      wx.navigateTo({ url: '/pages/login/index' });
    }, 1500);
    reject(res.data);
  } else {
    wx.showToast({ title: '服务器错误', icon: 'none' });
    reject(res.data);
  }
};

const request = async (url, method, data, showLoading = true) => {
  const candidates = getBaseUrlCandidates();

  if (!candidates.length) {
    wx.showToast({ title: '未配置接口地址', icon: 'none' });
    return Promise.reject(new Error('No base URL configured'));
  }

  if (showLoading) {
    wx.showLoading({ title: '加载中...', mask: true });
  }

  let lastError = null;

  try {
    for (const baseUrl of candidates) {
      try {
        const { res } = await requestWithBaseUrl(baseUrl, url, method, data);

        app.globalData.baseUrl = baseUrl;
        wx.setStorageSync('debugBaseUrl', baseUrl);
        return new Promise((resolve, reject) => handleResponse(res, resolve, reject));
      } catch (error) {
        lastError = error.err || error;
      }
    }

    wx.showToast({ title: '网络异常，请检查电脑与手机网络', icon: 'none' });
    return Promise.reject(lastError);
  } finally {
    if (showLoading) {
      wx.hideLoading();
    }
  }
};

const get = (url, data, showLoading) => request(url, 'GET', data, showLoading);
const post = (url, data, showLoading) => request(url, 'POST', data, showLoading);
const put = (url, data, showLoading) => request(url, 'PUT', data, showLoading);
const del = (url, data, showLoading) => request(url, 'DELETE', data, showLoading);

module.exports = { get, post, put, del };
