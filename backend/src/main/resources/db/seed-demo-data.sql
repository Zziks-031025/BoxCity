USE boxcity;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
START TRANSACTION;

DELETE FROM review;
DELETE FROM refund_logistics;
DELETE FROM refund;
DELETE FROM order_logistics;
DELETE FROM payment;
DELETE FROM order_item;
DELETE FROM `order`;
DELETE FROM cart;
DELETE FROM goods_attribute;
DELETE FROM goods_image;
DELETE FROM goods;
DELETE FROM freight_template_rule;
DELETE FROM freight_template;
DELETE FROM merchant_address;
DELETE FROM merchant;
DELETE FROM user_address;
DELETE FROM report;
DELETE FROM message;
DELETE FROM payment_config;
DELETE FROM freight_template_global;
DELETE FROM user;

INSERT INTO user (id, openid, union_id, nickname, avatar, phone, gender, birthday, city_id, status, deleted, created_at, updated_at) VALUES
(1, 'mock_openid_29ad1dc8685327ac', NULL, '盒友小宁', 'http://localhost:8080/demo-assets/avatar-user-1.png', '13900010001', 2, '1998-05-12', 111, 1, 0, '2026-03-18 10:00:00', '2026-03-20 16:00:00'),
(2, 'demo_openid_user_2', NULL, '阿澄', 'http://localhost:8080/demo-assets/avatar-user-2.png', '13900010002', 1, '1996-09-08', 112, 1, 0, '2026-03-17 11:20:00', '2026-03-20 16:00:00'),
(3, 'demo_openid_user_3', NULL, '小棠', 'http://localhost:8080/demo-assets/avatar-user-3.png', '13900010003', 2, '1999-03-21', 108, 1, 0, '2026-03-16 09:30:00', '2026-03-20 16:00:00'),
(4, 'demo_openid_user_4', NULL, '远山', 'http://localhost:8080/demo-assets/avatar-user-4.png', '13900010004', 1, '1994-11-16', 116, 1, 0, '2026-03-15 18:10:00', '2026-03-20 16:00:00'),
(5, 'demo_openid_user_5', NULL, '巡城员', 'http://localhost:8080/demo-assets/avatar-user-5.png', '13900010005', 0, NULL, 111, 0, 0, '2026-03-14 08:00:00', '2026-03-20 16:00:00');

INSERT INTO user_address (id, user_id, name, phone, province, city, district, detail, is_default, deleted, created_at, updated_at) VALUES
(1001, 1, '林小满', '13900010001', '江苏省', '南京市', '秦淮区', '长乐路18号悦来公寓2栋1203', 1, 0, '2026-03-18 10:10:00', '2026-03-20 16:00:00'),
(1002, 1, '林小满', '13900010001', '江苏省', '苏州市', '工业园区', '星湖街88号天际花园6幢502', 0, 0, '2026-03-18 10:12:00', '2026-03-20 16:00:00'),
(1003, 2, '周澄', '13900010002', '江苏省', '苏州市', '姑苏区', '平江路127号临河小院', 1, 0, '2026-03-17 11:25:00', '2026-03-20 16:00:00'),
(1004, 3, '沈棠', '13900010003', '浙江省', '杭州市', '上城区', '湖滨路66号观景公寓1801', 1, 0, '2026-03-16 09:35:00', '2026-03-20 16:00:00'),
(1005, 4, '陈远山', '13900010004', '湖北省', '武汉市', '江岸区', '沿江大道101号云锦里9幢901', 1, 0, '2026-03-15 18:15:00', '2026-03-20 16:00:00');

INSERT INTO merchant (id, user_id, shop_name, shop_avatar, shop_intro, shop_notice, contact_phone, contact_email, subject_type, license_url, id_card_front, id_card_back, legal_person_id_front, legal_person_id_back, audit_status, audit_remark, credit_score, password, deleted, created_at, updated_at) VALUES
(101, 2, '金陵手作研究所', 'http://localhost:8080/demo-assets/avatar-merchant-1.png', '聚焦南京在地手作与城市礼物，主打可日常使用的轻文创。', '工作日 16:00 前订单当天发出，支持礼盒包装。', '13800000001', 'jinling@boxcity.local', 2, 'http://localhost:8080/demo-assets/doc-license-1.png', 'http://localhost:8080/demo-assets/doc-idcard-1.png', 'http://localhost:8080/demo-assets/doc-idcard-2.png', 'http://localhost:8080/demo-assets/doc-idcard-1.png', 'http://localhost:8080/demo-assets/doc-idcard-2.png', 1, '资料完整，已通过平台审核', 98, '$2a$10$gTBsVW7.RIdclD2/i/SH1uCVtxQRWMgOQdkS9cfWsEgiTzlhf50k.', 0, '2026-03-10 09:00:00', '2026-03-20 16:00:00'),
(102, 3, '秦淮风物社', 'http://localhost:8080/demo-assets/avatar-merchant-2.png', '做一间带着烟火气的城市伴手礼小店，偏食品与家居小摆件。', '满 2 件自动减运费，支持商家发票。', '13800000002', 'qinhuai@boxcity.local', 1, 'http://localhost:8080/demo-assets/doc-license-2.png', 'http://localhost:8080/demo-assets/doc-idcard-1.png', 'http://localhost:8080/demo-assets/doc-idcard-2.png', NULL, NULL, 1, '资料完整，已通过平台审核', 96, '$2a$10$gTBsVW7.RIdclD2/i/SH1uCVtxQRWMgOQdkS9cfWsEgiTzlhf50k.', 0, '2026-03-11 10:30:00', '2026-03-20 16:00:00'),
(103, 4, '城市漫游局', 'http://localhost:8080/demo-assets/avatar-merchant-3.png', '偏年轻化的城市限定商店，售卖潮玩、旅行配饰与路线周边。', '周末订单会顺延到周一处理，请耐心等待。', '13800000003', 'citywalk@boxcity.local', 2, 'http://localhost:8080/demo-assets/doc-license-1.png', 'http://localhost:8080/demo-assets/doc-idcard-1.png', 'http://localhost:8080/demo-assets/doc-idcard-2.png', 'http://localhost:8080/demo-assets/doc-idcard-1.png', 'http://localhost:8080/demo-assets/doc-idcard-2.png', 1, '资料完整，已通过平台审核', 99, '$2a$10$gTBsVW7.RIdclD2/i/SH1uCVtxQRWMgOQdkS9cfWsEgiTzlhf50k.', 0, '2026-03-12 14:00:00', '2026-03-20 16:00:00'),
(104, NULL, '未央潮玩铺', 'http://localhost:8080/demo-assets/avatar-merchant-2.png', '主打潮玩盲盒和收藏摆件，正在等待平台审核。', '审核中，暂未开放商品展示。', '13800000004', 'waiting@boxcity.local', 1, 'http://localhost:8080/demo-assets/doc-license-2.png', 'http://localhost:8080/demo-assets/doc-idcard-1.png', 'http://localhost:8080/demo-assets/doc-idcard-2.png', NULL, NULL, 0, NULL, 100, '$2a$10$gTBsVW7.RIdclD2/i/SH1uCVtxQRWMgOQdkS9cfWsEgiTzlhf50k.', 0, '2026-03-18 09:20:00', '2026-03-20 16:00:00'),
(105, NULL, '老城拾光铺', 'http://localhost:8080/demo-assets/avatar-merchant-1.png', '旧城街景与老物件风格店，当前处于风控冻结状态。', '店铺冻结中，暂不可登录经营。', '13800000005', 'frozen@boxcity.local', 1, 'http://localhost:8080/demo-assets/doc-license-1.png', 'http://localhost:8080/demo-assets/doc-idcard-1.png', 'http://localhost:8080/demo-assets/doc-idcard-2.png', NULL, NULL, 3, '因异常退款率过高，店铺已冻结', 72, '$2a$10$gTBsVW7.RIdclD2/i/SH1uCVtxQRWMgOQdkS9cfWsEgiTzlhf50k.', 0, '2026-03-13 15:10:00', '2026-03-20 16:00:00'),
(106, NULL, '雨花小食局', 'http://localhost:8080/demo-assets/avatar-merchant-3.png', '专做城市零食礼盒与便携伴手礼，目前审核未通过。', '请补充清晰资质后重新提交。', '13800000006', 'reject@boxcity.local', 2, 'http://localhost:8080/demo-assets/doc-license-2.png', 'http://localhost:8080/demo-assets/doc-idcard-1.png', 'http://localhost:8080/demo-assets/doc-idcard-2.png', 'http://localhost:8080/demo-assets/doc-idcard-1.png', 'http://localhost:8080/demo-assets/doc-idcard-2.png', 2, '资质图片不清晰，请补充后重新提交', 84, '$2a$10$gTBsVW7.RIdclD2/i/SH1uCVtxQRWMgOQdkS9cfWsEgiTzlhf50k.', 0, '2026-03-17 13:40:00', '2026-03-20 16:00:00');

INSERT INTO merchant_address (id, merchant_id, province, city, district, detail, zipcode, is_default, deleted, created_at, updated_at) VALUES
(2001, 101, '江苏省', '南京市', '秦淮区', '钞库街18号夫子庙手作工坊', '210001', 1, 0, '2026-03-10 09:10:00', '2026-03-20 16:00:00'),
(2002, 101, '江苏省', '南京市', '雨花台区', '软件大道18号设计谷B座', '210012', 0, 0, '2026-03-10 09:15:00', '2026-03-20 16:00:00'),
(2003, 102, '江苏省', '南京市', '建邺区', '江东中路56号风物仓', '210019', 1, 0, '2026-03-11 10:40:00', '2026-03-20 16:00:00'),
(2004, 103, '浙江省', '杭州市', '上城区', '延安路266号城市漫游站', '310000', 1, 0, '2026-03-12 14:10:00', '2026-03-20 16:00:00');

INSERT INTO freight_template (id, merchant_id, name, charge_type, is_default, deleted, created_at, updated_at) VALUES
(3001, 101, '金陵默认运费', 1, 1, 0, '2026-03-10 09:20:00', '2026-03-20 16:00:00'),
(3002, 102, '秦淮轻量运费', 1, 1, 0, '2026-03-11 10:45:00', '2026-03-20 16:00:00'),
(3003, 103, '城市漫游包邮', 0, 1, 0, '2026-03-12 14:20:00', '2026-03-20 16:00:00');

INSERT INTO freight_template_rule (id, template_id, region_ids, first_unit, first_fee, additional_unit, additional_fee) VALUES
(3101, 3001, NULL, 1, 6.00, 1, 2.00),
(3102, 3002, NULL, 2, 5.00, 1, 1.00),
(3103, 3003, NULL, 1, 0.00, 1, 0.00);

INSERT INTO payment_config (id, mch_id, api_key, cert_path, notify_url, updated_at) VALUES
(1, 'demo_mch_2026', 'demo_api_key_for_local_debug', 'classpath:cert/mock-demo-cert.p12', 'http://localhost:8080/api/app/order/wechat/notify', '2026-03-20 16:00:00');

INSERT INTO freight_template_global (id, charge_type, rules, updated_at) VALUES
(1, 1, '{"defaultFirstUnit":1,"defaultFirstFee":8,"defaultAdditionalUnit":1,"defaultAdditionalFee":2}', '2026-03-20 16:00:00');

INSERT INTO banner (id, image_url, link_url, sort, status, created_at, updated_at) VALUES
(401, 'http://localhost:8080/demo-assets/banner-1.png', '/pages/shop/index?id=101', 1, 1, '2026-03-19 10:00:00', '2026-03-20 16:00:00'),
(402, 'http://localhost:8080/demo-assets/banner-2.png', '/pages/shop/index?id=102', 2, 1, '2026-03-19 10:05:00', '2026-03-20 16:00:00'),
(403, 'http://localhost:8080/demo-assets/banner-3.png', '/pages/shop/index?id=103', 3, 1, '2026-03-19 10:10:00', '2026-03-20 16:00:00');

INSERT INTO goods (id, merchant_id, category_id, title, description, price, original_price, stock, sales, city_id, audit_status, audit_remark, status, views, deleted, created_at, updated_at) VALUES
(1001, 101, 1, '南京云锦首饰盒', '金陵云锦纹样灵感的木质首饰盒，适合放耳饰、戒指和小型手链。', 168.00, 198.00, 62, 128, 111, 1, '审核通过', 1, 356, 0, '2026-03-10 11:00:00', '2026-03-20 16:00:00'),
(1002, 101, 3, '城门手账贴纸本', '以南京城门与老街路牌为主题的手账贴纸套装，纸质偏厚，适合拼贴。', 59.00, 69.00, 96, 86, 111, 1, '审核通过', 1, 242, 0, '2026-03-10 11:20:00', '2026-03-20 16:00:00'),
(1003, 101, 5, '金陵城门明信片套装', '包含 12 张城市门景明信片与一枚烫金封套，适合旅游伴手礼。', 29.00, 39.00, 140, 64, 111, 1, '审核通过', 1, 188, 0, '2026-03-10 11:35:00', '2026-03-20 16:00:00'),
(1004, 101, 2, '桂花米酿伴手礼', '常温便携版桂花米酿礼盒，适合作为家访或节日小礼。', 36.00, 48.00, 80, 12, 111, 1, '审核通过', 1, 42, 0, '2026-03-10 11:50:00', '2026-03-20 16:00:00'),
(1005, 102, 2, '盐水鸭真空礼盒', '南京经典风味盐水鸭礼盒，单盒为双人分享装，适合冷藏后食用。', 68.00, 88.00, 120, 198, 111, 1, '审核通过', 1, 418, 0, '2026-03-11 12:00:00', '2026-03-20 16:00:00'),
(1006, 102, 5, '秦淮灯影摆件', '夜游秦淮主题摆件，含暖黄氛围灯片，桌面展示效果柔和。', 89.00, 109.00, 74, 74, 111, 1, '审核通过', 1, 207, 0, '2026-03-11 12:15:00', '2026-03-20 16:00:00'),
(1007, 102, 3, '地铁票根徽章盲袋', '复古票根样式的金属徽章，随机发一个城市主题款。', 25.00, 29.00, 150, 0, 111, 0, NULL, 0, 18, 0, '2026-03-11 12:30:00', '2026-03-20 16:00:00'),
(1008, 102, 1, '云锦书签礼盒彩版', '彩版云锦纹书签套装，适合搭配书礼和节日贺卡。', 46.00, 56.00, 60, 0, 111, 2, '图文信息与资质描述不一致', 0, 11, 0, '2026-03-11 12:45:00', '2026-03-20 16:00:00'),
(1009, 103, 4, '城市漫游盲盒·西湖夜游', '西湖夜游路线主题盲盒，内含场景卡、钥匙扣和路线小册。', 129.00, 149.00, 88, 156, 108, 1, '审核通过', 1, 301, 0, '2026-03-12 15:00:00', '2026-03-20 16:00:00'),
(1010, 103, 5, '夜游地图丝巾', '以夜间城市路线图为灵感的轻薄丝巾，适合春秋叠搭。', 79.00, 99.00, 65, 39, 108, 1, '审核通过', 1, 119, 0, '2026-03-12 15:20:00', '2026-03-20 16:00:00'),
(1011, 103, 2, '龙井曲奇旅行装', '小份量旅行装龙井曲奇，适合短途出游时分享和补给。', 49.00, 59.00, 130, 58, 108, 1, '审核通过', 1, 94, 0, '2026-03-12 15:35:00', '2026-03-20 16:00:00'),
(1012, 103, 3, '城市摄影明信片集', '含西湖与老街胶片感摄影作品，适合收藏与寄送。', 39.00, 49.00, 90, 0, 108, 0, NULL, 0, 27, 0, '2026-03-12 15:50:00', '2026-03-20 16:00:00');

INSERT INTO goods_image (id, goods_id, image_url, type, sort) VALUES
(4001, 1001, 'http://localhost:8080/demo-assets/goods-1001.png', 1, 0),
(4002, 1001, 'http://localhost:8080/demo-assets/goods-1001.png', 2, 1),
(4003, 1002, 'http://localhost:8080/demo-assets/goods-1002.png', 1, 0),
(4004, 1002, 'http://localhost:8080/demo-assets/goods-1002.png', 2, 1),
(4005, 1003, 'http://localhost:8080/demo-assets/goods-1003.png', 1, 0),
(4006, 1003, 'http://localhost:8080/demo-assets/goods-1003.png', 2, 1),
(4007, 1004, 'http://localhost:8080/demo-assets/goods-1004.png', 1, 0),
(4008, 1004, 'http://localhost:8080/demo-assets/goods-1004.png', 2, 1),
(4009, 1005, 'http://localhost:8080/demo-assets/goods-1005.png', 1, 0),
(4010, 1005, 'http://localhost:8080/demo-assets/goods-1005.png', 2, 1),
(4011, 1006, 'http://localhost:8080/demo-assets/goods-1006.png', 1, 0),
(4012, 1006, 'http://localhost:8080/demo-assets/goods-1006.png', 2, 1),
(4013, 1007, 'http://localhost:8080/demo-assets/goods-1007.png', 1, 0),
(4014, 1007, 'http://localhost:8080/demo-assets/goods-1007.png', 2, 1),
(4015, 1008, 'http://localhost:8080/demo-assets/goods-1008.png', 1, 0),
(4016, 1008, 'http://localhost:8080/demo-assets/goods-1008.png', 2, 1),
(4017, 1009, 'http://localhost:8080/demo-assets/goods-1009.png', 1, 0),
(4018, 1009, 'http://localhost:8080/demo-assets/goods-1009.png', 2, 1),
(4019, 1010, 'http://localhost:8080/demo-assets/goods-1010.png', 1, 0),
(4020, 1010, 'http://localhost:8080/demo-assets/goods-1010.png', 2, 1),
(4021, 1011, 'http://localhost:8080/demo-assets/goods-1011.png', 1, 0),
(4022, 1011, 'http://localhost:8080/demo-assets/goods-1011.png', 2, 1),
(4023, 1012, 'http://localhost:8080/demo-assets/goods-1012.png', 1, 0),
(4024, 1012, 'http://localhost:8080/demo-assets/goods-1012.png', 2, 1);

INSERT INTO goods_attribute (id, goods_id, attribute_name) VALUES
(5001, 1001, '手工拼木'),
(5002, 1001, '南京限定'),
(5003, 1001, '礼盒包装'),
(5004, 1002, '厚纸覆膜'),
(5005, 1002, '适合手账'),
(5006, 1002, '城市纹样'),
(5007, 1003, '12 张一套'),
(5008, 1003, '烫金封套'),
(5009, 1003, '旅行伴手礼'),
(5010, 1004, '常温保存'),
(5011, 1004, '节日礼盒'),
(5012, 1004, '南京风味'),
(5013, 1005, '冷藏更佳'),
(5014, 1005, '双人分享'),
(5015, 1005, '经典风味'),
(5016, 1006, '暖光氛围'),
(5017, 1006, '桌面摆件'),
(5018, 1006, '夜游主题'),
(5019, 1007, '随机款式'),
(5020, 1007, '金属徽章'),
(5021, 1007, '票根设计'),
(5022, 1008, '云锦纹样'),
(5023, 1008, '书礼搭配'),
(5024, 1008, '礼盒彩版'),
(5025, 1009, '夜游路线'),
(5026, 1009, '盲盒周边'),
(5027, 1009, '场景卡册'),
(5028, 1010, '轻薄丝巾'),
(5029, 1010, '城市地图'),
(5030, 1010, '通勤搭配'),
(5031, 1011, '龙井风味'),
(5032, 1011, '旅行装'),
(5033, 1011, '便携分享'),
(5034, 1012, '胶片风格'),
(5035, 1012, '老街取景'),
(5036, 1012, '收藏明信片');

INSERT INTO cart (id, user_id, goods_id, merchant_id, quantity, created_at, updated_at) VALUES
(6001, 1, 1001, 101, 1, '2026-03-20 15:00:00', '2026-03-20 16:00:00'),
(6002, 1, 1002, 101, 1, '2026-03-20 15:02:00', '2026-03-20 16:00:00'),
(6003, 1, 1005, 102, 2, '2026-03-20 15:05:00', '2026-03-20 16:00:00'),
(6004, 1, 1009, 103, 1, '2026-03-20 15:10:00', '2026-03-20 16:00:00');

INSERT INTO `order` (id, order_no, user_id, merchant_id, total_amount, freight_amount, pay_amount, address_snapshot, buyer_message, status, pay_time, ship_time, receive_time, cancel_time, auto_cancel_time, deleted, created_at, updated_at) VALUES
(5001, 'BC202603200001', 1, 101, 168.00, 6.00, 174.00, '{"name":"林小满","phone":"13900010001","province":"江苏省","city":"南京市","district":"秦淮区","detail":"长乐路18号悦来公寓2栋1203"}', '首饰盒请帮忙加礼袋', 0, NULL, NULL, NULL, NULL, '2099-12-31 23:59:59', 0, '2026-03-20 16:15:00', '2026-03-20 16:15:00'),
(5002, 'BC202603190001', 1, 102, 157.00, 5.00, 162.00, '{"name":"林小满","phone":"13900010001","province":"江苏省","city":"南京市","district":"秦淮区","detail":"长乐路18号悦来公寓2栋1203"}', '鸭礼盒周末聚餐使用', 1, '2026-03-19 18:00:00', NULL, NULL, NULL, NULL, 0, '2026-03-19 17:30:00', '2026-03-19 18:00:00'),
(5003, 'BC202603180001', 1, 103, 178.00, 0.00, 178.00, '{"name":"林小满","phone":"13900010001","province":"江苏省","city":"南京市","district":"秦淮区","detail":"长乐路18号悦来公寓2栋1203"}', '周末前能收到就好', 2, '2026-03-18 10:10:00', '2026-03-18 18:40:00', NULL, NULL, NULL, 0, '2026-03-18 09:45:00', '2026-03-19 09:20:00'),
(5004, 'BC202603150001', 1, 101, 88.00, 8.00, 96.00, '{"name":"林小满","phone":"13900010001","province":"江苏省","city":"苏州市","district":"工业园区","detail":"星湖街88号天际花园6幢502"}', '明信片麻烦平整包装', 3, '2026-03-15 13:00:00', '2026-03-15 18:10:00', '2026-03-17 19:20:00', NULL, NULL, 0, '2026-03-15 12:30:00', '2026-03-17 19:20:00'),
(5005, 'BC202603140001', 1, 102, 89.00, 5.00, 94.00, '{"name":"林小满","phone":"13900010001","province":"江苏省","city":"南京市","district":"秦淮区","detail":"长乐路18号悦来公寓2栋1203"}', '如果周末发不出就取消', 4, NULL, NULL, NULL, '2026-03-14 12:15:00', '2026-03-14 12:10:00', 0, '2026-03-14 11:40:00', '2026-03-14 12:15:00'),
(5006, 'BC202603130001', 1, 103, 79.00, 0.00, 79.00, '{"name":"林小满","phone":"13900010001","province":"江苏省","city":"南京市","district":"秦淮区","detail":"长乐路18号悦来公寓2栋1203"}', '丝巾颜色希望偏深一点', 5, '2026-03-13 14:00:00', '2026-03-14 09:00:00', NULL, NULL, NULL, 0, '2026-03-13 13:30:00', '2026-03-19 11:00:00'),
(5007, 'BC202603120001', 2, 101, 168.00, 6.00, 174.00, '{"name":"周澄","phone":"13900010002","province":"江苏省","city":"苏州市","district":"姑苏区","detail":"平江路127号临河小院"}', '送朋友生日礼物', 5, '2026-03-12 10:00:00', '2026-03-12 18:30:00', '2026-03-14 20:20:00', NULL, NULL, 0, '2026-03-12 09:20:00', '2026-03-18 15:10:00'),
(5008, 'BC202603110001', 3, 102, 89.00, 5.00, 94.00, '{"name":"沈棠","phone":"13900010003","province":"浙江省","city":"杭州市","district":"上城区","detail":"湖滨路66号观景公寓1801"}', '摆件送同事用', 5, '2026-03-11 16:10:00', '2026-03-12 09:30:00', '2026-03-14 11:50:00', NULL, NULL, 0, '2026-03-11 15:40:00', '2026-03-18 10:40:00'),
(5009, 'BC202603090001', 4, 103, 49.00, 0.00, 49.00, '{"name":"陈远山","phone":"13900010004","province":"湖北省","city":"武汉市","district":"江岸区","detail":"沿江大道101号云锦里9幢901"}', '旅行补给，尽快发货', 4, '2026-03-09 09:20:00', NULL, NULL, '2026-03-10 11:00:00', NULL, 0, '2026-03-09 09:00:00', '2026-03-10 11:00:00'),
(5010, 'BC202603170001', 2, 102, 68.00, 5.00, 73.00, '{"name":"周澄","phone":"13900010002","province":"江苏省","city":"苏州市","district":"姑苏区","detail":"平江路127号临河小院"}', '下周活动用，怕来不及', 5, '2026-03-17 13:30:00', NULL, NULL, NULL, NULL, 0, '2026-03-17 13:00:00', '2026-03-18 08:30:00'),
(5011, 'BC202603080001', 2, 101, 168.00, 6.00, 174.00, '{"name":"周澄","phone":"13900010002","province":"江苏省","city":"苏州市","district":"姑苏区","detail":"平江路127号临河小院"}', '朋友推荐来买', 3, '2026-03-08 11:30:00', '2026-03-08 18:20:00', '2026-03-11 19:00:00', NULL, NULL, 0, '2026-03-08 11:00:00', '2026-03-11 19:00:00'),
(5012, 'BC202603070001', 3, 102, 68.00, 5.00, 73.00, '{"name":"沈棠","phone":"13900010003","province":"浙江省","city":"杭州市","district":"上城区","detail":"湖滨路66号观景公寓1801"}', '想试试口味', 3, '2026-03-07 14:10:00', '2026-03-07 18:10:00', '2026-03-10 12:10:00', NULL, NULL, 0, '2026-03-07 13:45:00', '2026-03-10 12:10:00'),
(5013, 'BC202603060001', 4, 103, 49.00, 0.00, 49.00, '{"name":"陈远山","phone":"13900010004","province":"湖北省","city":"武汉市","district":"江岸区","detail":"沿江大道101号云锦里9幢901"}', '曲奇分给同事', 3, '2026-03-06 10:15:00', '2026-03-06 16:20:00', '2026-03-09 15:10:00', NULL, NULL, 0, '2026-03-06 09:50:00', '2026-03-09 15:10:00');

INSERT INTO order_item (id, order_id, goods_id, goods_snapshot, quantity, price, created_at) VALUES
(5501, 5001, 1001, '{"goodsId":1001,"title":"南京云锦首饰盒","price":168.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1001.png"}', 1, 168.00, '2026-03-20 16:15:00'),
(5502, 5002, 1005, '{"goodsId":1005,"title":"盐水鸭真空礼盒","price":68.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1005.png"}', 1, 68.00, '2026-03-19 17:30:00'),
(5503, 5002, 1006, '{"goodsId":1006,"title":"秦淮灯影摆件","price":89.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1006.png"}', 1, 89.00, '2026-03-19 17:30:00'),
(5504, 5003, 1009, '{"goodsId":1009,"title":"城市漫游盲盒·西湖夜游","price":129.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1009.png"}', 1, 129.00, '2026-03-18 09:45:00'),
(5505, 5003, 1011, '{"goodsId":1011,"title":"龙井曲奇旅行装","price":49.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1011.png"}', 1, 49.00, '2026-03-18 09:45:00'),
(5506, 5004, 1003, '{"goodsId":1003,"title":"金陵城门明信片套装","price":29.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1003.png"}', 1, 29.00, '2026-03-15 12:30:00'),
(5507, 5004, 1002, '{"goodsId":1002,"title":"城门手账贴纸本","price":59.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1002.png"}', 1, 59.00, '2026-03-15 12:30:00'),
(5508, 5005, 1006, '{"goodsId":1006,"title":"秦淮灯影摆件","price":89.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1006.png"}', 1, 89.00, '2026-03-14 11:40:00'),
(5509, 5006, 1010, '{"goodsId":1010,"title":"夜游地图丝巾","price":79.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1010.png"}', 1, 79.00, '2026-03-13 13:30:00'),
(5510, 5007, 1001, '{"goodsId":1001,"title":"南京云锦首饰盒","price":168.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1001.png"}', 1, 168.00, '2026-03-12 09:20:00'),
(5511, 5008, 1006, '{"goodsId":1006,"title":"秦淮灯影摆件","price":89.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1006.png"}', 1, 89.00, '2026-03-11 15:40:00'),
(5512, 5009, 1011, '{"goodsId":1011,"title":"龙井曲奇旅行装","price":49.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1011.png"}', 1, 49.00, '2026-03-09 09:00:00'),
(5513, 5010, 1005, '{"goodsId":1005,"title":"盐水鸭真空礼盒","price":68.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1005.png"}', 1, 68.00, '2026-03-17 13:00:00'),
(5514, 5011, 1001, '{"goodsId":1001,"title":"南京云锦首饰盒","price":168.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1001.png"}', 1, 168.00, '2026-03-08 11:00:00'),
(5515, 5012, 1005, '{"goodsId":1005,"title":"盐水鸭真空礼盒","price":68.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1005.png"}', 1, 68.00, '2026-03-07 13:45:00'),
(5516, 5013, 1011, '{"goodsId":1011,"title":"龙井曲奇旅行装","price":49.00,"imageUrl":"http://localhost:8080/demo-assets/goods-1011.png"}', 1, 49.00, '2026-03-06 09:50:00');

INSERT INTO payment (id, order_id, transaction_id, pay_amount, pay_status, pay_time, created_at) VALUES
(9001, 5002, 'MOCKPAY202603190001', 162.00, 1, '2026-03-19 18:00:00', '2026-03-19 18:00:00'),
(9002, 5003, 'MOCKPAY202603180001', 178.00, 1, '2026-03-18 10:10:00', '2026-03-18 10:10:00'),
(9003, 5004, 'MOCKPAY202603150001', 96.00, 1, '2026-03-15 13:00:00', '2026-03-15 13:00:00'),
(9004, 5006, 'MOCKPAY202603130001', 79.00, 1, '2026-03-13 14:00:00', '2026-03-13 14:00:00'),
(9005, 5007, 'MOCKPAY202603120001', 174.00, 1, '2026-03-12 10:00:00', '2026-03-12 10:00:00'),
(9006, 5008, 'MOCKPAY202603110001', 94.00, 1, '2026-03-11 16:10:00', '2026-03-11 16:10:00'),
(9007, 5009, 'MOCKPAY202603090001', 49.00, 2, '2026-03-09 09:20:00', '2026-03-09 09:20:00'),
(9008, 5010, 'MOCKPAY202603170001', 73.00, 1, '2026-03-17 13:30:00', '2026-03-17 13:30:00'),
(9009, 5011, 'MOCKPAY202603080001', 174.00, 1, '2026-03-08 11:30:00', '2026-03-08 11:30:00'),
(9010, 5012, 'MOCKPAY202603070001', 73.00, 1, '2026-03-07 14:10:00', '2026-03-07 14:10:00'),
(9011, 5013, 'MOCKPAY202603060001', 49.00, 1, '2026-03-06 10:15:00', '2026-03-06 10:15:00');

INSERT INTO order_logistics (id, order_id, logistics_company, logistics_no, logistics_status, logistics_data, created_at, updated_at) VALUES
(9101, 5003, '顺丰速运', 'SF202603180001', 2, '[{"time":"2026-03-18T18:40:00","content":"包裹已交给顺丰速运","location":"杭州市"},{"time":"2026-03-19T09:20:00","content":"包裹正在运输途中","location":"南京市"}]', '2026-03-18 18:40:00', '2026-03-19 09:20:00'),
(9102, 5004, '中通快递', 'ZT202603150001', 4, '[{"time":"2026-03-15T18:10:00","content":"商家已发货","location":"南京市"},{"time":"2026-03-16T10:30:00","content":"快件正在运输途中","location":"苏州市"},{"time":"2026-03-17T17:10:00","content":"快件已由收件人签收","location":"苏州市"}]', '2026-03-15 18:10:00', '2026-03-17 17:10:00'),
(9103, 5006, '圆通速递', 'YT202603130001', 4, '[{"time":"2026-03-14T09:00:00","content":"商家已发货","location":"杭州市"},{"time":"2026-03-15T11:30:00","content":"快件正在派送，请保持电话畅通","location":"南京市"},{"time":"2026-03-15T18:00:00","content":"快件已到达驿站待取","location":"南京市"}]', '2026-03-14 09:00:00', '2026-03-15 18:00:00'),
(9104, 5007, '顺丰速运', 'SF202603120001', 4, '[{"time":"2026-03-12T18:30:00","content":"商家已发货","location":"南京市"},{"time":"2026-03-13T09:00:00","content":"快件正在运输途中","location":"苏州市"},{"time":"2026-03-14T10:50:00","content":"快件已由收件人签收","location":"苏州市"}]', '2026-03-12 18:30:00', '2026-03-14 10:50:00'),
(9105, 5008, '京东快递', 'JD202603110001', 4, '[{"time":"2026-03-12T09:30:00","content":"商家已发货","location":"南京市"},{"time":"2026-03-13T14:00:00","content":"快件正在运输途中","location":"杭州市"},{"time":"2026-03-14T11:00:00","content":"快件已由收件人签收","location":"杭州市"}]', '2026-03-12 09:30:00', '2026-03-14 11:00:00'),
(9106, 5011, '顺丰速运', 'SF202603080001', 4, '[{"time":"2026-03-08T18:20:00","content":"商家已发货","location":"南京市"},{"time":"2026-03-10T10:00:00","content":"快件已由收件人签收","location":"苏州市"}]', '2026-03-08 18:20:00', '2026-03-10 10:00:00'),
(9107, 5012, '中通快递', 'ZT202603070001', 4, '[{"time":"2026-03-07T18:10:00","content":"商家已发货","location":"南京市"},{"time":"2026-03-09T09:40:00","content":"快件已由收件人签收","location":"杭州市"}]', '2026-03-07 18:10:00', '2026-03-09 09:40:00'),
(9108, 5013, '圆通速递', 'YT202603060001', 4, '[{"time":"2026-03-06T16:20:00","content":"商家已发货","location":"杭州市"},{"time":"2026-03-08T09:30:00","content":"快件已由收件人签收","location":"武汉市"}]', '2026-03-06 16:20:00', '2026-03-08 09:30:00');

INSERT INTO refund (id, order_id, order_item_id, user_id, merchant_id, refund_type, refund_reason, evidence_images, refund_amount, status, reject_reason, platform_intervene, merchant_deadline, return_deadline, deleted, created_at, updated_at) VALUES
(60011, 5006, 5509, 1, 103, 1, '收到时丝巾边角有明显褶皱和轻微脱线', '["http://localhost:8080/demo-assets/goods-1010.png"]', 79.00, 4, '商家拒绝：已签收且商品无明显损坏', 0, '2026-03-18 10:00:00', NULL, 0, '2026-03-18 09:10:00', '2026-03-19 11:00:00'),
(60012, 5007, 5510, 2, 101, 2, '尺寸与详情描述不一致，准备退回', '["http://localhost:8080/demo-assets/goods-1001.png"]', 174.00, 2, NULL, 0, '2026-03-16 12:00:00', '2026-03-24 20:00:00', 0, '2026-03-15 12:00:00', '2026-03-18 15:10:00'),
(60013, 5008, 5511, 3, 102, 2, '摆件包装内缺少挂件配件，希望平台协助处理', '["http://localhost:8080/demo-assets/goods-1006.png"]', 94.00, 5, '商家拒绝：未发现质量问题；用户申请平台介入：包装内配件缺失', 1, '2026-03-16 18:00:00', NULL, 0, '2026-03-15 15:30:00', '2026-03-18 10:40:00'),
(60014, 5009, 5512, 4, 103, 1, '重复下单，申请仅退款', '[]', 49.00, 3, NULL, 0, '2026-03-09 20:00:00', NULL, 0, '2026-03-09 10:00:00', '2026-03-10 11:00:00'),
(60015, 5010, 5513, 2, 102, 1, '活动时间变更，商品暂时不需要了', '[]', 73.00, 0, NULL, 0, '2026-03-19 13:30:00', NULL, 0, '2026-03-18 08:30:00', '2026-03-18 08:30:00');

INSERT INTO refund_logistics (id, refund_id, logistics_company, logistics_no, logistics_status, logistics_data, created_at, updated_at) VALUES
(6101, 60012, '顺丰速运', 'RSF202603180001', 2, '[{"time":"2026-03-18T15:10:00","content":"用户已提交退货物流，等待商家签收","location":"苏州市"},{"time":"2026-03-19T08:30:00","content":"退货包裹运输中","location":"南京市"}]', '2026-03-18 15:10:00', '2026-03-19 08:30:00');

INSERT INTO review (id, order_id, order_item_id, user_id, goods_id, merchant_id, rating, content, images, is_append, parent_id, deleted, created_at) VALUES
(8001, 5004, 5507, 1, 1002, 101, 5, '贴纸纸质不错，颜色饱和，和手账本很搭。', '["http://localhost:8080/demo-assets/goods-1002.png"]', 0, 0, 0, '2026-03-18 20:00:00'),
(8002, 5011, 5514, 2, 1001, 101, 4, '首饰盒质感很好，送朋友很体面，就是包装再厚一点会更安心。', '["http://localhost:8080/demo-assets/goods-1001.png"]', 0, 0, 0, '2026-03-12 20:20:00'),
(8003, 5012, 5515, 3, 1005, 102, 5, '鸭礼盒比预想更新鲜，适合聚会时直接拆来吃。', '["http://localhost:8080/demo-assets/goods-1005.png"]', 0, 0, 0, '2026-03-11 19:10:00'),
(8004, 5011, 5514, 2, 1001, 101, 4, '追评一下，放了几天也没有明显味道，细节处理不错。', '[]', 1, 8002, 0, '2026-03-15 10:15:00'),
(8005, 5013, 5516, 4, 1011, 103, 4, '曲奇口感偏酥，茶味不会太甜，出差带着分食很方便。', '[]', 0, 0, 0, '2026-03-10 18:00:00');

INSERT INTO message (id, user_id, type, title, content, is_read, created_at) VALUES
(10001, 1, 1, '订单支付成功', '订单 BC202603190001 已支付成功，商家将尽快为您发货。', 1, '2026-03-19 18:00:00'),
(10002, 1, 1, '订单已发货', '订单 BC202603180001 已由顺丰速运发出，运单号 SF202603180001。', 0, '2026-03-18 18:45:00'),
(10003, 1, 1, '订单已完成', '订单 BC202603150001 已完成，感谢您的购买，欢迎发表评价。', 0, '2026-03-17 19:20:00'),
(10004, 1, 2, '商家已拒绝售后申请', '订单 BC202603130001 的售后申请已被商家拒绝，您可以申请平台介入。', 0, '2026-03-19 11:00:00'),
(10005, 2, 2, '退货物流已提交', '订单 BC202603120001 的退货物流信息已提交，等待商家确认收货。', 0, '2026-03-18 15:10:00'),
(10006, 3, 2, '平台介入申请已提交', '平台已收到订单 BC202603110001 的介入申请，将尽快处理该售后纠纷。', 0, '2026-03-18 10:40:00');

INSERT INTO report (id, reporter_id, target_type, target_id, reason, evidence_images, status, handle_result, handler_id, created_at, updated_at) VALUES
(7001, 4, 3, 1008, '商品图文与实际经营资质不一致，建议平台进一步核验。', '["http://localhost:8080/demo-assets/goods-1008.png"]', 0, NULL, NULL, '2026-03-19 09:20:00', '2026-03-19 09:20:00'),
(7002, 3, 2, 8002, '评价中疑似包含导流描述，请平台检查。', '[]', 1, '已核查，无违规外部联系方式。', 1, '2026-03-18 14:00:00', '2026-03-18 16:00:00');

COMMIT;
SET FOREIGN_KEY_CHECKS = 1;
