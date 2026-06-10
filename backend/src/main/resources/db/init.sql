-- ============================================================
-- 盒集·城市盲盒交换平台 (BoxCity) 数据库初始化脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS boxcity DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE boxcity;

-- ============================================================
-- T01 - 用户表
-- ============================================================
CREATE TABLE IF NOT EXISTS `user` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `openid`     VARCHAR(64)  NULL     COMMENT '微信openid',
  `union_id`   VARCHAR(64)  NULL     COMMENT '微信union_id',
  `nickname`   VARCHAR(50)  NULL     COMMENT '昵称',
  `avatar`     VARCHAR(500) NULL     COMMENT '头像URL',
  `phone`      VARCHAR(20)  NULL     COMMENT '手机号',
  `gender`     TINYINT      NOT NULL DEFAULT 0 COMMENT '性别: 0未知 1男 2女',
  `birthday`   DATE         NULL     COMMENT '生日',
  `city_id`    BIGINT       NULL     COMMENT '所在城市ID',
  `status`     TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1正常 0禁用',
  `deleted`    TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
  `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_openid` (`openid`),
  INDEX `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- T02 - 用户收货地址表
-- ============================================================
CREATE TABLE IF NOT EXISTS `user_address` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id`    BIGINT       NOT NULL COMMENT '用户ID',
  `name`       VARCHAR(50)  NULL     COMMENT '收货人姓名',
  `phone`      VARCHAR(20)  NULL     COMMENT '收货人电话',
  `province`   VARCHAR(50)  NULL     COMMENT '省份',
  `city`       VARCHAR(50)  NULL     COMMENT '城市',
  `district`   VARCHAR(50)  NULL     COMMENT '区县',
  `detail`     VARCHAR(200) NULL     COMMENT '详细地址',
  `is_default` TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认: 0否 1是',
  `deleted`    TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
  `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';

-- ============================================================
-- T03 - 商户表
-- ============================================================
CREATE TABLE IF NOT EXISTS `merchant` (
  `id`                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '商户ID',
  `user_id`               BIGINT       NULL     COMMENT '关联用户ID',
  `shop_name`             VARCHAR(100) NULL     COMMENT '店铺名称',
  `shop_avatar`           VARCHAR(500) NULL     COMMENT '店铺头像URL',
  `shop_intro`            VARCHAR(500) NULL     COMMENT '店铺简介',
  `shop_notice`           VARCHAR(500) NULL     COMMENT '店铺公告',
  `contact_phone`         VARCHAR(20)  NULL     COMMENT '联系电话',
  `contact_email`         VARCHAR(100) NULL     COMMENT '联系邮箱',
  `subject_type`          TINYINT      NULL     COMMENT '主体类型: 1个人 2企业',
  `license_url`           VARCHAR(500) NULL     COMMENT '营业执照URL',
  `id_card_front`         VARCHAR(500) NULL     COMMENT '身份证正面URL',
  `id_card_back`          VARCHAR(500) NULL     COMMENT '身份证反面URL',
  `legal_person_id_front` VARCHAR(500) NULL     COMMENT '法人身份证正面URL',
  `legal_person_id_back`  VARCHAR(500) NULL     COMMENT '法人身份证反面URL',
  `audit_status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '审核状态: 0待审核 1通过 2不通过 3冻结',
  `audit_remark`          VARCHAR(500) NULL     COMMENT '审核备注',
  `credit_score`          INT          NOT NULL DEFAULT 100 COMMENT '信用分',
  `password`              VARCHAR(200) NULL     COMMENT '商户密码',
  `deleted`               TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
  `created_at`            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户表';

-- ============================================================
-- T04 - 商户地址表
-- ============================================================
CREATE TABLE IF NOT EXISTS `merchant_address` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `merchant_id` BIGINT       NOT NULL COMMENT '商户ID',
  `province`    VARCHAR(50)  NULL     COMMENT '省份',
  `city`        VARCHAR(50)  NULL     COMMENT '城市',
  `district`    VARCHAR(50)  NULL     COMMENT '区县',
  `detail`      VARCHAR(200) NULL     COMMENT '详细地址',
  `zipcode`     VARCHAR(10)  NULL     COMMENT '邮编',
  `is_default`  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认: 0否 1是',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户地址表';

-- ============================================================
-- T05 - 商品分类表
-- ============================================================
CREATE TABLE IF NOT EXISTS `category` (
  `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id`  BIGINT      NOT NULL DEFAULT 0 COMMENT '父分类ID, 0为顶级',
  `name`       VARCHAR(50) NOT NULL COMMENT '分类名称',
  `sort`       INT         NOT NULL DEFAULT 0 COMMENT '排序值',
  `status`     TINYINT     NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
  `deleted`    TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
  `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ============================================================
-- T06 - 城市表
-- ============================================================
CREATE TABLE IF NOT EXISTS `city` (
  `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '城市ID',
  `parent_id`  BIGINT      NOT NULL DEFAULT 0 COMMENT '父级ID, 0为顶级',
  `name`       VARCHAR(50) NOT NULL COMMENT '名称',
  `level`      TINYINT     NULL     COMMENT '层级: 1省 2市 3区',
  `sort`       INT         NOT NULL DEFAULT 0 COMMENT '排序值',
  `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='城市表';

-- ============================================================
-- T07 - 商品表
-- ============================================================
CREATE TABLE IF NOT EXISTS `goods` (
  `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `merchant_id`    BIGINT        NOT NULL COMMENT '商户ID',
  `category_id`    BIGINT        NULL     COMMENT '分类ID',
  `title`          VARCHAR(200)  NOT NULL COMMENT '商品标题',
  `description`    TEXT          NULL     COMMENT '商品描述',
  `price`          DECIMAL(10,2) NOT NULL COMMENT '售价',
  `original_price` DECIMAL(10,2) NULL     COMMENT '原价',
  `stock`          INT           NOT NULL DEFAULT 0 COMMENT '库存',
  `sales`          INT           NOT NULL DEFAULT 0 COMMENT '销量',
  `city_id`        BIGINT        NULL     COMMENT '城市ID',
  `audit_status`   TINYINT       NOT NULL DEFAULT 0 COMMENT '审核状态: 0待审核 1通过 2不通过',
  `audit_remark`   VARCHAR(500)  NULL     COMMENT '审核备注',
  `status`         TINYINT       NOT NULL DEFAULT 0 COMMENT '上下架: 0下架 1上架',
  `views`          INT           NOT NULL DEFAULT 0 COMMENT '浏览量',
  `deleted`        TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
  `created_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_merchant_status_audit` (`merchant_id`, `status`, `audit_status`),
  INDEX `idx_category_status` (`category_id`, `status`),
  INDEX `idx_city_status` (`city_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ============================================================
-- T08 - 商品图片表
-- ============================================================
CREATE TABLE IF NOT EXISTS `goods_image` (
  `id`        BIGINT       NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `goods_id`  BIGINT       NOT NULL COMMENT '商品ID',
  `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
  `type`      TINYINT      NULL     COMMENT '图片类型: 1主图 2详情图',
  `sort`      INT          NOT NULL DEFAULT 0 COMMENT '排序值',
  PRIMARY KEY (`id`),
  INDEX `idx_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';

-- ============================================================
-- T09 - 商品属性表
-- ============================================================
CREATE TABLE IF NOT EXISTS `goods_attribute` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '属性ID',
  `goods_id`       BIGINT       NOT NULL COMMENT '商品ID',
  `attribute_name` VARCHAR(100) NOT NULL COMMENT '属性名称',
  PRIMARY KEY (`id`),
  INDEX `idx_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品属性表';

-- ============================================================
-- T10 - 购物车表
-- ============================================================
CREATE TABLE IF NOT EXISTS `cart` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
  `goods_id`    BIGINT   NOT NULL COMMENT '商品ID',
  `merchant_id` BIGINT   NOT NULL COMMENT '商户ID',
  `quantity`    INT      NOT NULL DEFAULT 1 COMMENT '数量',
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ============================================================
-- T11 - 订单表
-- ============================================================
CREATE TABLE IF NOT EXISTS `order` (
  `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no`         VARCHAR(32)   NOT NULL COMMENT '订单编号',
  `user_id`          BIGINT        NOT NULL COMMENT '用户ID',
  `merchant_id`      BIGINT        NOT NULL COMMENT '商户ID',
  `total_amount`     DECIMAL(10,2) NULL     COMMENT '商品总金额',
  `freight_amount`   DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '运费',
  `pay_amount`       DECIMAL(10,2) NULL     COMMENT '实付金额',
  `address_snapshot` TEXT          NULL     COMMENT '收货地址快照',
  `buyer_message`    VARCHAR(500)  NULL     COMMENT '买家留言',
  `status`           TINYINT       NOT NULL DEFAULT 0 COMMENT '订单状态: 0待付款 1待发货 2待收货 3已完成 4已取消 5售后中',
  `pay_time`         DATETIME      NULL     COMMENT '支付时间',
  `ship_time`        DATETIME      NULL     COMMENT '发货时间',
  `receive_time`     DATETIME      NULL     COMMENT '收货时间',
  `cancel_time`      DATETIME      NULL     COMMENT '取消时间',
  `auto_cancel_time` DATETIME      NULL     COMMENT '自动取消时间',
  `deleted`          TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
  `created_at`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_order_no` (`order_no`),
  INDEX `idx_user_status` (`user_id`, `status`),
  INDEX `idx_merchant_status` (`merchant_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ============================================================
-- T12 - 订单明细表
-- ============================================================
CREATE TABLE IF NOT EXISTS `order_item` (
  `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id`       BIGINT        NOT NULL COMMENT '订单ID',
  `goods_id`       BIGINT        NOT NULL COMMENT '商品ID',
  `goods_snapshot` TEXT          NULL     COMMENT '商品快照',
  `quantity`       INT           NULL     COMMENT '数量',
  `price`          DECIMAL(10,2) NULL     COMMENT '单价',
  `created_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ============================================================
-- T13 - 订单物流表
-- ============================================================
CREATE TABLE IF NOT EXISTS `order_logistics` (
  `id`               BIGINT      NOT NULL AUTO_INCREMENT COMMENT '物流ID',
  `order_id`         BIGINT      NOT NULL COMMENT '订单ID',
  `logistics_company` VARCHAR(50) NULL    COMMENT '物流公司',
  `logistics_no`     VARCHAR(50) NULL     COMMENT '物流单号',
  `logistics_status` TINYINT     NOT NULL DEFAULT 0 COMMENT '物流状态',
  `logistics_data`   TEXT        NULL     COMMENT '物流轨迹数据',
  `created_at`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单物流表';

-- ============================================================
-- T14 - 支付记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS `payment` (
  `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `order_id`       BIGINT        NOT NULL COMMENT '订单ID',
  `transaction_id` VARCHAR(64)   NULL     COMMENT '第三方交易号',
  `pay_amount`     DECIMAL(10,2) NULL     COMMENT '支付金额',
  `pay_status`     TINYINT       NOT NULL DEFAULT 0 COMMENT '支付状态: 0未支付 1已支付 2已退款',
  `pay_time`       DATETIME      NULL     COMMENT '支付时间',
  `created_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_transaction_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- ============================================================
-- T15 - 退款/售后表
-- ============================================================
CREATE TABLE IF NOT EXISTS `refund` (
  `id`                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '退款ID',
  `order_id`            BIGINT        NOT NULL COMMENT '订单ID',
  `order_item_id`       BIGINT        NULL     COMMENT '订单明细ID',
  `user_id`             BIGINT        NOT NULL COMMENT '用户ID',
  `merchant_id`         BIGINT        NOT NULL COMMENT '商户ID',
  `refund_type`         TINYINT       NULL     COMMENT '退款类型: 1仅退款 2退货退款',
  `refund_reason`       VARCHAR(500)  NULL     COMMENT '退款原因',
  `evidence_images`     VARCHAR(2000) NULL     COMMENT '凭证图片',
  `refund_amount`       DECIMAL(10,2) NULL     COMMENT '退款金额',
  `status`              TINYINT       NOT NULL DEFAULT 0 COMMENT '状态: 0待商家处理 1商家同意待退货 2退货中 3退款成功 4商家拒绝 5平台介入中 6售后关闭',
  `reject_reason`       VARCHAR(500)  NULL     COMMENT '拒绝原因',
  `platform_intervene`  TINYINT       NOT NULL DEFAULT 0 COMMENT '平台是否介入: 0否 1是',
  `merchant_deadline`   DATETIME      NULL     COMMENT '商家处理截止时间',
  `return_deadline`     DATETIME      NULL     COMMENT '退货截止时间',
  `deleted`             TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
  `created_at`          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_merchant_status` (`merchant_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款售后表';

-- ============================================================
-- T16 - 退货物流表
-- ============================================================
CREATE TABLE IF NOT EXISTS `refund_logistics` (
  `id`               BIGINT      NOT NULL AUTO_INCREMENT COMMENT '物流ID',
  `refund_id`        BIGINT      NOT NULL COMMENT '退款ID',
  `logistics_company` VARCHAR(50) NULL    COMMENT '物流公司',
  `logistics_no`     VARCHAR(50) NULL     COMMENT '物流单号',
  `logistics_status` TINYINT     NOT NULL DEFAULT 0 COMMENT '物流状态',
  `logistics_data`   TEXT        NULL     COMMENT '物流轨迹数据',
  `created_at`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_refund_id` (`refund_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退货物流表';

-- ============================================================
-- T17 - 评价表
-- ============================================================
CREATE TABLE IF NOT EXISTS `review` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id`      BIGINT        NULL     COMMENT '订单ID',
  `order_item_id` BIGINT        NULL     COMMENT '订单明细ID',
  `user_id`       BIGINT        NOT NULL COMMENT '用户ID',
  `goods_id`      BIGINT        NOT NULL COMMENT '商品ID',
  `merchant_id`   BIGINT        NULL     COMMENT '商户ID',
  `rating`        TINYINT       NULL     COMMENT '评分(1-5)',
  `content`       VARCHAR(1000) NULL     COMMENT '评价内容',
  `images`        VARCHAR(2000) NULL     COMMENT '评价图片',
  `is_append`     TINYINT       NOT NULL DEFAULT 0 COMMENT '是否追评: 0否 1是',
  `parent_id`     BIGINT        NOT NULL DEFAULT 0 COMMENT '父评价ID, 0为顶级',
  `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
  `created_at`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_goods_id` (`goods_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- ============================================================
-- T18 - 运费模板表
-- ============================================================
CREATE TABLE IF NOT EXISTS `freight_template` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `merchant_id` BIGINT       NOT NULL COMMENT '商户ID',
  `name`        VARCHAR(100) NULL     COMMENT '模板名称',
  `charge_type` TINYINT      NULL     COMMENT '计费方式: 0包邮 1按件 2按重量',
  `is_default`  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认: 0否 1是',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运费模板表';

-- ============================================================
-- T19 - 运费模板规则表
-- ============================================================
CREATE TABLE IF NOT EXISTS `freight_template_rule` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `template_id`     BIGINT        NOT NULL COMMENT '模板ID',
  `region_ids`      VARCHAR(2000) NULL     COMMENT '地区ID集合',
  `first_unit`      INT           NOT NULL DEFAULT 1 COMMENT '首件/首重',
  `first_fee`       DECIMAL(10,2) NULL     COMMENT '首费',
  `additional_unit` INT           NOT NULL DEFAULT 1 COMMENT '续件/续重',
  `additional_fee`  DECIMAL(10,2) NULL     COMMENT '续费',
  PRIMARY KEY (`id`),
  INDEX `idx_template_id` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运费模板规则表';

-- ============================================================
-- T20 - 运费模板不配送区域表
-- ============================================================
CREATE TABLE IF NOT EXISTS `freight_template_exclude` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '排除ID',
  `template_id` BIGINT        NOT NULL COMMENT '模板ID',
  `region_ids`  VARCHAR(2000) NULL     COMMENT '不配送地区ID集合',
  PRIMARY KEY (`id`),
  INDEX `idx_template_id` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运费模板不配送区域表';

-- ============================================================
-- T21 - 轮播图表
-- ============================================================
CREATE TABLE IF NOT EXISTS `banner` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `image_url`  VARCHAR(500) NOT NULL COMMENT '图片URL',
  `link_url`   VARCHAR(500) NULL     COMMENT '跳转链接',
  `sort`       INT          NOT NULL DEFAULT 0 COMMENT '排序值',
  `status`     TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
  `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- ============================================================
-- T22 - 管理员表
-- ============================================================
CREATE TABLE IF NOT EXISTS `admin` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username`   VARCHAR(50)  NOT NULL COMMENT '用户名',
  `password`   VARCHAR(200) NOT NULL COMMENT '密码',
  `role`       VARCHAR(20)  NOT NULL DEFAULT 'admin' COMMENT '角色',
  `status`     TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
  `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- ============================================================
-- T23 - 支付配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS `payment_config` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `mch_id`     VARCHAR(50)  NULL     COMMENT '商户号',
  `api_key`    VARCHAR(200) NULL     COMMENT 'API密钥',
  `cert_path`  VARCHAR(500) NULL     COMMENT '证书路径',
  `notify_url` VARCHAR(500) NULL     COMMENT '回调地址',
  `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付配置表';

-- ============================================================
-- T24 - 全局运费模板表
-- ============================================================
CREATE TABLE IF NOT EXISTS `freight_template_global` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '全局模板ID',
  `charge_type` TINYINT  NULL     COMMENT '计费方式',
  `rules`       TEXT     NULL     COMMENT '规则JSON',
  `updated_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全局运费模板表';

-- ============================================================
-- T25 - 举报表
-- ============================================================
CREATE TABLE IF NOT EXISTS `report` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '举报ID',
  `reporter_id`     BIGINT        NOT NULL COMMENT '举报人ID',
  `target_type`     TINYINT       NULL     COMMENT '举报目标类型: 1用户 2评价 3商品',
  `target_id`       BIGINT        NOT NULL COMMENT '举报目标ID',
  `reason`          VARCHAR(500)  NULL     COMMENT '举报原因',
  `evidence_images` VARCHAR(2000) NULL     COMMENT '证据图片',
  `status`          TINYINT       NOT NULL DEFAULT 0 COMMENT '状态: 0待处理 1已处理',
  `handle_result`   VARCHAR(500)  NULL     COMMENT '处理结果',
  `handler_id`      BIGINT        NULL     COMMENT '处理人ID',
  `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报表';

-- ============================================================
-- T26 - 消息表
-- ============================================================
CREATE TABLE IF NOT EXISTS `message` (
  `id`         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `user_id`    BIGINT        NOT NULL COMMENT '用户ID',
  `type`       TINYINT       NULL     COMMENT '消息类型',
  `title`      VARCHAR(200)  NULL     COMMENT '消息标题',
  `content`    VARCHAR(1000) NULL     COMMENT '消息内容',
  `is_read`    TINYINT       NOT NULL DEFAULT 0 COMMENT '是否已读: 0未读 1已读',
  `created_at` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_read` (`user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- ============================================================
-- T27 - 搜索历史表
-- ============================================================
CREATE TABLE IF NOT EXISTS `search_history` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '搜索历史ID',
  `user_id`    BIGINT       NOT NULL COMMENT '用户ID',
  `keyword`    VARCHAR(100) NULL     COMMENT '搜索关键词',
  `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索历史表';

-- ============================================================
-- T28 - 热门搜索表
-- ============================================================
CREATE TABLE IF NOT EXISTS `search_hot` (
  `id`      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '热搜ID',
  `keyword` VARCHAR(100) NOT NULL COMMENT '热搜关键词',
  `sort`    INT          NOT NULL DEFAULT 0 COMMENT '排序值',
  `status`  TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热门搜索表';

-- ============================================================
-- 初始数据
-- ============================================================

-- 管理员账号 (密码为 admin123 的 BCrypt 哈希)
INSERT INTO `admin` (`username`, `password`, `role`, `status`, `created_at`, `updated_at`) VALUES
('admin', '$2a$10$sdeipQuLbqi.cnjkny38e.ta6oG4wtMPEGXWEagY0TxnS4230lP7y', 'super_admin', 1, NOW(), NOW());

-- 商品分类
INSERT INTO `category` (`id`, `parent_id`, `name`, `sort`, `status`, `created_at`, `updated_at`) VALUES
(1, 0, '手工艺品', 1, 1, NOW(), NOW()),
(2, 0, '特产食品', 2, 1, NOW(), NOW()),
(3, 0, '文创周边', 3, 1, NOW(), NOW()),
(4, 0, '潮玩手办', 4, 1, NOW(), NOW()),
(5, 0, '城市限定', 5, 1, NOW(), NOW());

-- 城市数据 - 省级
INSERT INTO `city` (`id`, `parent_id`, `name`, `level`, `sort`, `created_at`) VALUES
(1,  0, '北京市',   1, 1,  NOW()),
(2,  0, '上海市',   1, 2,  NOW()),
(3,  0, '广东省',   1, 3,  NOW()),
(4,  0, '浙江省',   1, 4,  NOW()),
(5,  0, '江苏省',   1, 5,  NOW()),
(6,  0, '四川省',   1, 6,  NOW()),
(7,  0, '湖北省',   1, 7,  NOW()),
(8,  0, '湖南省',   1, 8,  NOW()),
(9,  0, '山东省',   1, 9,  NOW()),
(10, 0, '河南省',   1, 10, NOW()),
(11, 0, '福建省',   1, 11, NOW()),
(12, 0, '重庆市',   1, 12, NOW()),
(13, 0, '天津市',   1, 13, NOW()),
(14, 0, '陕西省',   1, 14, NOW()),
(15, 0, '辽宁省',   1, 15, NOW());

-- 城市数据 - 市级
INSERT INTO `city` (`id`, `parent_id`, `name`, `level`, `sort`, `created_at`) VALUES
-- 北京(直辖市, 市级同省级)
(101, 1,  '北京市',   2, 1, NOW()),
-- 上海(直辖市)
(102, 2,  '上海市',   2, 1, NOW()),
-- 广东省
(103, 3,  '广州市',   2, 1, NOW()),
(104, 3,  '深圳市',   2, 2, NOW()),
(105, 3,  '东莞市',   2, 3, NOW()),
(106, 3,  '佛山市',   2, 4, NOW()),
(107, 3,  '珠海市',   2, 5, NOW()),
-- 浙江省
(108, 4,  '杭州市',   2, 1, NOW()),
(109, 4,  '宁波市',   2, 2, NOW()),
(110, 4,  '温州市',   2, 3, NOW()),
-- 江苏省
(111, 5,  '南京市',   2, 1, NOW()),
(112, 5,  '苏州市',   2, 2, NOW()),
(113, 5,  '无锡市',   2, 3, NOW()),
-- 四川省
(114, 6,  '成都市',   2, 1, NOW()),
(115, 6,  '绵阳市',   2, 2, NOW()),
-- 湖北省
(116, 7,  '武汉市',   2, 1, NOW()),
(117, 7,  '宜昌市',   2, 2, NOW()),
-- 湖南省
(118, 8,  '长沙市',   2, 1, NOW()),
-- 山东省
(119, 9,  '济南市',   2, 1, NOW()),
(120, 9,  '青岛市',   2, 2, NOW()),
-- 河南省
(121, 10, '郑州市',   2, 1, NOW()),
-- 福建省
(122, 11, '福州市',   2, 1, NOW()),
(123, 11, '厦门市',   2, 2, NOW()),
-- 重庆(直辖市)
(124, 12, '重庆市',   2, 1, NOW()),
-- 天津(直辖市)
(125, 13, '天津市',   2, 1, NOW()),
-- 陕西省
(126, 14, '西安市',   2, 1, NOW()),
-- 辽宁省
(127, 15, '沈阳市',   2, 1, NOW()),
(128, 15, '大连市',   2, 2, NOW());

-- 城市数据 - 区级
INSERT INTO `city` (`id`, `parent_id`, `name`, `level`, `sort`, `created_at`) VALUES
-- 北京市
(1001, 101, '东城区',   3, 1,  NOW()),
(1002, 101, '西城区',   3, 2,  NOW()),
(1003, 101, '朝阳区',   3, 3,  NOW()),
(1004, 101, '海淀区',   3, 4,  NOW()),
(1005, 101, '丰台区',   3, 5,  NOW()),
(1006, 101, '通州区',   3, 6,  NOW()),
-- 上海市
(1007, 102, '黄浦区',   3, 1,  NOW()),
(1008, 102, '徐汇区',   3, 2,  NOW()),
(1009, 102, '静安区',   3, 3,  NOW()),
(1010, 102, '浦东新区', 3, 4,  NOW()),
(1011, 102, '长宁区',   3, 5,  NOW()),
-- 广州市
(1012, 103, '天河区',   3, 1,  NOW()),
(1013, 103, '越秀区',   3, 2,  NOW()),
(1014, 103, '海珠区',   3, 3,  NOW()),
(1015, 103, '番禺区',   3, 4,  NOW()),
-- 深圳市
(1016, 104, '南山区',   3, 1,  NOW()),
(1017, 104, '福田区',   3, 2,  NOW()),
(1018, 104, '罗湖区',   3, 3,  NOW()),
(1019, 104, '宝安区',   3, 4,  NOW()),
-- 杭州市
(1020, 108, '西湖区',   3, 1,  NOW()),
(1021, 108, '上城区',   3, 2,  NOW()),
(1022, 108, '拱墅区',   3, 3,  NOW()),
(1023, 108, '余杭区',   3, 4,  NOW()),
-- 南京市
(1024, 111, '玄武区',   3, 1,  NOW()),
(1025, 111, '秦淮区',   3, 2,  NOW()),
(1026, 111, '鼓楼区',   3, 3,  NOW()),
(1027, 111, '建邺区',   3, 4,  NOW()),
-- 成都市
(1028, 114, '锦江区',   3, 1,  NOW()),
(1029, 114, '青羊区',   3, 2,  NOW()),
(1030, 114, '武侯区',   3, 3,  NOW()),
(1031, 114, '高新区',   3, 4,  NOW()),
-- 武汉市
(1032, 116, '武昌区',   3, 1,  NOW()),
(1033, 116, '江汉区',   3, 2,  NOW()),
(1034, 116, '洪山区',   3, 3,  NOW()),
(1035, 116, '汉阳区',   3, 4,  NOW()),
-- 长沙市
(1036, 118, '岳麓区',   3, 1,  NOW()),
(1037, 118, '芙蓉区',   3, 2,  NOW()),
(1038, 118, '天心区',   3, 3,  NOW()),
-- 重庆市
(1039, 124, '渝中区',   3, 1,  NOW()),
(1040, 124, '江北区',   3, 2,  NOW()),
(1041, 124, '南岸区',   3, 3,  NOW()),
(1042, 124, '渝北区',   3, 4,  NOW()),
-- 西安市
(1043, 126, '雁塔区',   3, 1,  NOW()),
(1044, 126, '碑林区',   3, 2,  NOW()),
(1045, 126, '未央区',   3, 3,  NOW()),
-- 东莞市
(1046, 105, '莞城区',   3, 1,  NOW()),
(1047, 105, '南城区',   3, 2,  NOW()),
(1048, 105, '东城区',   3, 3,  NOW()),
(1049, 105, '万江区',   3, 4,  NOW()),
-- 佛山市
(1050, 106, '禅城区',   3, 1,  NOW()),
(1051, 106, '南海区',   3, 2,  NOW()),
(1052, 106, '顺德区',   3, 3,  NOW()),
-- 珠海市
(1053, 107, '香洲区',   3, 1,  NOW()),
(1054, 107, '斗门区',   3, 2,  NOW()),
(1055, 107, '金湾区',   3, 3,  NOW()),
-- 宁波市
(1056, 109, '海曙区',   3, 1,  NOW()),
(1057, 109, '江北区',   3, 2,  NOW()),
(1058, 109, '鄞州区',   3, 3,  NOW()),
-- 温州市
(1059, 110, '鹿城区',   3, 1,  NOW()),
(1060, 110, '龙湾区',   3, 2,  NOW()),
(1061, 110, '瓯海区',   3, 3,  NOW()),
-- 苏州市
(1062, 112, '姑苏区',   3, 1,  NOW()),
(1063, 112, '虎丘区',   3, 2,  NOW()),
(1064, 112, '吴中区',   3, 3,  NOW()),
(1065, 112, '工业园区', 3, 4,  NOW()),
-- 无锡市
(1066, 113, '锡山区',   3, 1,  NOW()),
(1067, 113, '惠山区',   3, 2,  NOW()),
(1068, 113, '滨湖区',   3, 3,  NOW()),
(1069, 113, '梁溪区',   3, 4,  NOW()),
-- 绵阳市
(1070, 115, '涪城区',   3, 1,  NOW()),
(1071, 115, '游仙区',   3, 2,  NOW()),
-- 宜昌市
(1072, 117, '西陵区',   3, 1,  NOW()),
(1073, 117, '伍家岗区', 3, 2,  NOW()),
-- 济南市
(1074, 119, '历下区',   3, 1,  NOW()),
(1075, 119, '市中区',   3, 2,  NOW()),
(1076, 119, '槐荫区',   3, 3,  NOW()),
-- 青岛市
(1077, 120, '市南区',   3, 1,  NOW()),
(1078, 120, '市北区',   3, 2,  NOW()),
(1079, 120, '崂山区',   3, 3,  NOW()),
-- 郑州市
(1080, 121, '金水区',   3, 1,  NOW()),
(1081, 121, '二七区',   3, 2,  NOW()),
(1082, 121, '中原区',   3, 3,  NOW()),
-- 福州市
(1083, 122, '鼓楼区',   3, 1,  NOW()),
(1084, 122, '台江区',   3, 2,  NOW()),
(1085, 122, '仓山区',   3, 3,  NOW()),
-- 厦门市
(1086, 123, '思明区',   3, 1,  NOW()),
(1087, 123, '湖里区',   3, 2,  NOW()),
(1088, 123, '集美区',   3, 3,  NOW()),
-- 天津市
(1089, 125, '和平区',   3, 1,  NOW()),
(1090, 125, '河西区',   3, 2,  NOW()),
(1091, 125, '南开区',   3, 3,  NOW()),
-- 沈阳市
(1092, 127, '沈河区',   3, 1,  NOW()),
(1093, 127, '和平区',   3, 2,  NOW()),
(1094, 127, '铁西区',   3, 3,  NOW()),
-- 大连市
(1095, 128, '中山区',   3, 1,  NOW()),
(1096, 128, '西岗区',   3, 2,  NOW()),
(1097, 128, '沙河口区', 3, 3,  NOW());

-- 热门搜索关键词
INSERT INTO `search_hot` (`keyword`, `sort`, `status`) VALUES
('城市限定', 1, 1),
('手工艺品', 2, 1),
('文创周边', 3, 1),
('潮玩盲盒', 4, 1);
