function getPlatformInfo() {
  let rawInfo = {};

  try {
    if (wx.getDeviceInfo) {
      rawInfo = wx.getDeviceInfo() || {};
    } else if (wx.getSystemInfoSync) {
      rawInfo = wx.getSystemInfoSync() || {};
    }
  } catch (e) {
    rawInfo = {};
  }

  const platformText = `${rawInfo.platform || ''} ${rawInfo.system || ''}`.toLowerCase();

  return {
    platform: rawInfo.platform || '',
    system: rawInfo.system || '',
    brand: rawInfo.brand || '',
    model: rawInfo.model || '',
    isIOS: platformText.includes('ios') || platformText.includes('iphone') || platformText.includes('ipad'),
    isAndroid: platformText.includes('android')
  };
}

module.exports = {
  getPlatformInfo
};
