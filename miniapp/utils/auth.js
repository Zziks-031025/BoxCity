var TOKEN_KEY = 'token';
var TOKEN_EXPIRE_AT_KEY = 'tokenExpireAt';
var USER_INFO_KEY = 'userInfo';
var SESSION_TTL_MS = 7 * 24 * 60 * 60 * 1000;

function syncAppState(app, token, userInfo, expireAt) {
  if (!app || !app.globalData) {
    return;
  }

  app.globalData.token = token || '';
  app.globalData.userInfo = userInfo || null;
  app.globalData.tokenExpireAt = expireAt || 0;
}

function clearSession(app) {
  wx.removeStorageSync(TOKEN_KEY);
  wx.removeStorageSync(TOKEN_EXPIRE_AT_KEY);
  wx.removeStorageSync(USER_INFO_KEY);
  syncAppState(app, '', null, 0);
}

function getStoredToken() {
  return wx.getStorageSync(TOKEN_KEY) || '';
}

function getStoredExpireAt() {
  return Number(wx.getStorageSync(TOKEN_EXPIRE_AT_KEY) || 0);
}

function isSessionExpired() {
  var token = getStoredToken();
  var expireAt = getStoredExpireAt();

  if (!token || !expireAt) {
    return true;
  }

  return Date.now() >= expireAt;
}

function getValidToken(app) {
  var token = getStoredToken();

  if (!token || isSessionExpired()) {
    clearSession(app);
    return '';
  }

  return token;
}

function hasValidSession(app) {
  return !!getValidToken(app);
}

function saveSession(token, app) {
  var expireAt = Date.now() + SESSION_TTL_MS;
  var userInfo = wx.getStorageSync(USER_INFO_KEY) || null;

  wx.setStorageSync(TOKEN_KEY, token);
  wx.setStorageSync(TOKEN_EXPIRE_AT_KEY, expireAt);
  syncAppState(app, token, userInfo, expireAt);

  return expireAt;
}

function restoreSession(app) {
  var token = getValidToken(app);
  var userInfo = wx.getStorageSync(USER_INFO_KEY) || null;
  var expireAt = getStoredExpireAt();

  syncAppState(app, token, userInfo, token ? expireAt : 0);

  return {
    token: token,
    expireAt: token ? expireAt : 0
  };
}

module.exports = {
  SESSION_TTL_MS: SESSION_TTL_MS,
  clearSession: clearSession,
  getValidToken: getValidToken,
  hasValidSession: hasValidSession,
  isSessionExpired: isSessionExpired,
  restoreSession: restoreSession,
  saveSession: saveSession
};
