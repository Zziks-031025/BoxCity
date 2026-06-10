const DEV_BASE_URLS = [
  // Real-device debugging should use the current LAN IP of the computer running the backend.
  'http://10.248.82.250:8080',
  'http://localhost:8080'
];

const EMPTY_BASE_URLS = [];

const getMiniProgramEnvVersion = () => {
  try {
    return wx.getAccountInfoSync().miniProgram.envVersion || 'develop';
  } catch (error) {
    return 'develop';
  }
};

const getStoredDebugBaseUrl = () => {
  try {
    return wx.getStorageSync('debugBaseUrl') || '';
  } catch (error) {
    return '';
  }
};

const getBaseUrlCandidates = () => {
  const envVersion = getMiniProgramEnvVersion();
  const storedBaseUrl = getStoredDebugBaseUrl();
  const envBaseUrls = envVersion === 'develop' ? DEV_BASE_URLS : EMPTY_BASE_URLS;

  return [storedBaseUrl, ...envBaseUrls].filter((url, index, list) => url && list.indexOf(url) === index);
};

module.exports = {
  getBaseUrlCandidates,
  getMiniProgramEnvVersion
};
