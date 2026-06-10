const DIGITAL_KEYWORDS = [
  '会员',
  'vip',
  '充值',
  '余额',
  '积分',
  '点数',
  '虚拟',
  '权益',
  '解锁',
  '订阅',
  '在线课程',
  '音频',
  '视频',
  '电子',
  '卡密',
  '兑换码',
  '在线使用',
  '立即生效'
];

const OFFLINE_SERVICE_KEYWORDS = [
  '到店',
  '线下',
  '核销',
  '预约',
  '体验券',
  '门店',
  '现场',
  '到场',
  '服务票',
  '门票'
];

const PHYSICAL_KEYWORDS = [
  '礼盒',
  '饰品',
  '手作',
  '周边',
  '特产',
  '食品',
  '工艺',
  '发货',
  '物流',
  '收货',
  '包邮',
  '实物',
  '现货',
  '邮寄',
  '快递'
];

function resolveExplicitType(goods) {
  const rawType = `${goods.goodsType || goods.type || goods.itemType || goods.productType || ''}`.toLowerCase();
  if (!rawType) {
    return '';
  }

  if (['digital', 'virtual', 'content', 'subscription', 'vip'].includes(rawType)) {
    return 'digital';
  }

  if (['offline_service', 'offline-service', 'offline', 'onsite', 'ticket', 'service'].includes(rawType)) {
    return 'offline_service';
  }

  if (['physical', 'entity', 'goods'].includes(rawType)) {
    return 'physical';
  }

  return '';
}

function inferGoodsType(goods) {
  const explicitType = resolveExplicitType(goods);
  if (explicitType) {
    return explicitType;
  }

  const sourceText = [
    goods.title,
    goods.goodsTitle,
    goods.name,
    goods.description,
    goods.summary
  ]
    .filter(Boolean)
    .join(' ')
    .toLowerCase();

  const hasDigitalHint = DIGITAL_KEYWORDS.some((keyword) => sourceText.includes(keyword.toLowerCase()));
  const hasOfflineHint = OFFLINE_SERVICE_KEYWORDS.some((keyword) => sourceText.includes(keyword.toLowerCase()));
  const hasPhysicalHint = PHYSICAL_KEYWORDS.some((keyword) => sourceText.includes(keyword.toLowerCase()));

  if (hasDigitalHint && !hasOfflineHint && !hasPhysicalHint) {
    return 'digital';
  }

  if (hasOfflineHint && !hasPhysicalHint) {
    return 'offline_service';
  }

  return 'physical';
}

function getFulfillmentLabel(goodsType) {
  if (goodsType === 'digital') {
    return '在线发放';
  }

  if (goodsType === 'offline_service') {
    return '线下核销';
  }

  return '实物发货';
}

function getGoodsTypeLabel(goodsType) {
  if (goodsType === 'digital') {
    return '数字权益';
  }

  if (goodsType === 'offline_service') {
    return '线下服务';
  }

  return '实物商品';
}

function getIOSRestriction(goods, platformInfo) {
  const goodsType = inferGoodsType(goods);
  const blocked = !!(platformInfo && platformInfo.isIOS && goodsType === 'digital');

  return {
    goodsType,
    goodsTypeLabel: getGoodsTypeLabel(goodsType),
    fulfillmentLabel: getFulfillmentLabel(goodsType),
    iosPurchaseBlocked: blocked,
    iosRestrictionText: blocked
      ? '当前商品会被识别为数字权益。为满足 iOS 端审核要求，小程序暂不展示该商品的下单与支付入口。'
      : ''
  };
}

module.exports = {
  inferGoodsType,
  getGoodsTypeLabel,
  getFulfillmentLabel,
  getIOSRestriction
};
