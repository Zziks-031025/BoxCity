function openPrivacyGuide(fallbackUrl) {
  if (wx.openPrivacyContract) {
    wx.openPrivacyContract({
      success() {},
      fail() {
        if (fallbackUrl) {
          wx.navigateTo({ url: fallbackUrl });
        }
      }
    });
    return;
  }

  if (fallbackUrl) {
    wx.navigateTo({ url: fallbackUrl });
  }
}

function ensurePrivacyForMedia(sceneText) {
  return new Promise((resolve) => {
    if (!wx.getPrivacySetting || !wx.requirePrivacyAuthorize) {
      resolve(true);
      return;
    }

    wx.getPrivacySetting({
      success(res) {
        if (res.needAuthorization) {
          wx.showModal({
            title: '隐私授权提示',
            content: `${sceneText || '当前操作'}需要你授权相关隐私能力，仅用于完成本次服务。`,
            confirmText: '继续',
            cancelText: '取消',
            success(modalRes) {
              if (!modalRes.confirm) {
                resolve(false);
                return;
              }
              wx.requirePrivacyAuthorize({
                success() {
                  resolve(true);
                },
                fail() {
                  wx.showToast({
                    title: '未完成授权，当前操作已取消',
                    icon: 'none'
                  });
                  resolve(false);
                }
              });
            }
          });
          return;
        }

        resolve(true);
      },
      fail() {
        resolve(true);
      }
    });
  });
}

module.exports = {
  openPrivacyGuide,
  ensurePrivacyForMedia
};
