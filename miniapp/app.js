const { getBaseUrlCandidates, getMiniProgramEnvVersion } = require('./config/env');
const { restoreSession } = require('./utils/auth.js');
const { getPlatformInfo } = require('./utils/platform.js');

App({
  globalData: {
    userInfo: null,
    token: '',
    tokenExpireAt: 0,
    baseUrl: '',
    baseUrlCandidates: [],
    envVersion: 'develop',
    platformInfo: {},
    tabNavParams: null
  },

  onLaunch() {
    this.globalData.baseUrlCandidates = getBaseUrlCandidates();
    this.globalData.baseUrl = this.globalData.baseUrlCandidates[0] || '';
    this.globalData.envVersion = getMiniProgramEnvVersion();
    this.globalData.platformInfo = getPlatformInfo();

    restoreSession(this);
  }
});
