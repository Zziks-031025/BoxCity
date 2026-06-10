const { platformInfo } = require('../../config/commercialization.js');

Page({
  data: {
    platformInfo,
    infoRows: [
      { label: '平台名称', valueKey: 'name' },
      { label: '经营主体', valueKey: 'companyName' },
      { label: '客服电话', valueKey: 'servicePhone' },
      { label: '客服邮箱', valueKey: 'serviceEmail' },
      { label: '服务时间', valueKey: 'serviceTime' },
      { label: '备案信息', valueKey: 'recordNo' },
      { label: '联系地址', valueKey: 'address' }
    ]
  }
});
