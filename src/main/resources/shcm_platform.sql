/*
 Navicat Premium Data Transfer

 Source Server         : sh-cynosdbmysql-grp-33zy53jm.sql.tencentcdb.com
 Source Server Type    : MySQL
 Source Server Version : 50718 (5.7.18-cynos-log)
 Source Host           : sh-cynosdbmysql-grp-33zy53jm.sql.tencentcdb.com:23241
 Source Schema         : shcm_platform

 Target Server Type    : MySQL
 Target Server Version : 50718 (5.7.18-cynos-log)
 File Encoding         : 65001

 Date: 29/02/2024 21:09:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_blog
-- ----------------------------
DROP TABLE IF EXISTS `tb_blog`;
CREATE TABLE `tb_blog`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` bigint(20) NOT NULL COMMENT '商户id',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `images` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '探店的照片，最多9张，多张以\",\"隔开',
  `content` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '探店的文字描述',
  `liked` int(8) UNSIGNED NULL DEFAULT 0 COMMENT '点赞数量',
  `comments` int(8) UNSIGNED NULL DEFAULT NULL COMMENT '评论数量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_blog
-- ----------------------------
INSERT INTO `tb_blog` VALUES (7, 10, 1, '杭州周末好去处｜💰50就可以骑马啦🐎', '/imgs/blogs/blog1.jpg', '杭州周末好去处｜💰50就可以骑马啦🐎', 1, 0, '2022-01-11 16:05:47', '2022-03-10 09:21:42');
INSERT INTO `tb_blog` VALUES (24, 3, 2, '我是可可不吃肉', '/imgs/blogs/8/5/45718d7e-cea2-4815-9f68-ecdb81b813c3.jpg', '大家好', 0, NULL, '2023-11-15 19:04:12', '2023-11-15 19:04:12');
INSERT INTO `tb_blog` VALUES (25, 3, 2, '可可不吃肉', '/imgs/blogs/15/11/05b561a8-0503-495e-a34d-98543081d08b.jpg', '555', 0, NULL, '2023-11-15 19:08:48', '2023-11-15 19:08:48');
INSERT INTO `tb_blog` VALUES (26, 3, 2, '可可不吃', '/imgs/blogs/5/9/b5523218-635c-4021-95ca-d0400befda4e.jpg', '213', 1, NULL, '2023-11-15 19:09:21', '2023-11-17 23:19:57');
INSERT INTO `tb_blog` VALUES (27, 3, 1010, '你好', '/imgs/blogs/2/3/deae5e89-c2c3-4969-b34b-46bbc55c86f8.jpg', '66', 0, NULL, '2023-11-16 19:32:59', '2023-11-16 19:32:59');
INSERT INTO `tb_blog` VALUES (28, 1, 1010, '今天星期五', '', '6', 0, NULL, '2023-11-17 16:19:08', '2023-11-17 16:19:08');
INSERT INTO `tb_blog` VALUES (29, 1, 1010, '111', '', '111', 0, NULL, '2024-01-01 13:50:54', '2024-01-01 13:50:54');
INSERT INTO `tb_blog` VALUES (30, 2, 1010, '123', '/imgs/blogs/11/2/fd7e46a5-92a2-4470-a323-95f5c55852f6.JPG', '123', 0, NULL, '2024-01-01 13:51:25', '2024-01-01 13:51:25');
INSERT INTO `tb_blog` VALUES (31, 1, 1010, '2024年', '/imgs/blogs/11/13/bb43642e-6121-453a-a38d-8b97c3c8d5c9.png', '手抓羊肉', 0, NULL, '2024-01-10 22:41:12', '2024-01-10 22:41:12');
INSERT INTO `tb_blog` VALUES (32, 1, 1010, '好吃', '/imgs/blogs/12/14/a82460a6-64b2-4a6f-b1e9-dfa30962ffed.png', '不错', 0, NULL, '2024-01-14 21:52:15', '2024-01-14 21:52:15');
INSERT INTO `tb_blog` VALUES (33, 1, 1010, '手抓', '/imgs/blogs/5/4/2047043f-96b7-4b0f-a170-a388d6e54c89.png', '很好吃', 0, NULL, '2024-01-14 21:52:46', '2024-01-14 21:52:46');
INSERT INTO `tb_blog` VALUES (34, 2, 1010, 'test', '', 'bucuo', 0, NULL, '2024-01-14 21:57:41', '2024-01-14 21:57:41');
INSERT INTO `tb_blog` VALUES (35, 1, 1010, 'test2024.2.25', '/imgs/blogs/15/1/9ecac71a-44f9-4dc5-96d2-b01150e2483f.jpg', 'test', 0, NULL, '2024-02-25 20:06:31', '2024-02-25 20:06:31');

-- ----------------------------
-- Table structure for tb_blog_comments
-- ----------------------------
DROP TABLE IF EXISTS `tb_blog_comments`;
CREATE TABLE `tb_blog_comments`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
  `blog_id` bigint(20) UNSIGNED NOT NULL COMMENT '探店id',
  `parent_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联的1级评论id，如果是一级评论，则值为0',
  `answer_id` bigint(20) UNSIGNED NOT NULL COMMENT '回复的评论id',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '回复的内容',
  `liked` int(8) UNSIGNED NULL DEFAULT NULL COMMENT '点赞数',
  `status` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '状态，0：正常，1：被举报，2：禁止查看',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_blog_comments
-- ----------------------------

-- ----------------------------
-- Table structure for tb_follow
-- ----------------------------
DROP TABLE IF EXISTS `tb_follow`;
CREATE TABLE `tb_follow`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
  `follow_user_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联的用户id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_follow
-- ----------------------------
INSERT INTO `tb_follow` VALUES (3, 2, 1, '2023-11-15 18:49:37');
INSERT INTO `tb_follow` VALUES (4, 1, 2, '2023-11-15 18:57:56');

-- ----------------------------
-- Table structure for tb_seckill_voucher
-- ----------------------------
DROP TABLE IF EXISTS `tb_seckill_voucher`;
CREATE TABLE `tb_seckill_voucher`  (
  `voucher_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联的优惠券的id',
  `stock` int(8) NOT NULL COMMENT '库存',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `begin_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生效时间',
  `end_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '失效时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`voucher_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '秒杀优惠券表，与优惠券是一对一关系' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_seckill_voucher
-- ----------------------------
INSERT INTO `tb_seckill_voucher` VALUES (10, 99, '2023-11-16 20:20:12', '2023-10-10 10:08:18', '2024-10-10 23:08:18', '2023-11-16 20:31:02');

-- ----------------------------
-- Table structure for tb_shop
-- ----------------------------
DROP TABLE IF EXISTS `tb_shop`;
CREATE TABLE `tb_shop`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商铺名称',
  `type_id` bigint(20) UNSIGNED NOT NULL COMMENT '商铺类型的id',
  `images` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商铺图片，多个图片以\',\'隔开',
  `area` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商圈，例如陆家嘴',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地址',
  `x` double UNSIGNED NOT NULL COMMENT '经度',
  `y` double UNSIGNED NOT NULL COMMENT '维度',
  `avg_price` bigint(10) UNSIGNED NULL DEFAULT NULL COMMENT '均价，取整数',
  `sold` int(10) UNSIGNED ZEROFILL NOT NULL COMMENT '销量',
  `comments` int(10) UNSIGNED ZEROFILL NOT NULL COMMENT '评论数量',
  `score` int(2) UNSIGNED ZEROFILL NOT NULL COMMENT '评分，1~5分，乘10保存，避免小数',
  `open_hours` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '营业时间，例如 10:00-22:00',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `foreign_key_type`(`type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_shop
-- ----------------------------
INSERT INTO `tb_shop` VALUES (1, '超意兴666', 1, 'https://qcloud.dpfile.com/pc/jiclIsCKmOI2arxKN1Uf0Hx3PucIJH8q0QSz-Z8llzcN56-_QiKuOvyio1OOxsRtFoXqu0G3iT2T27qat3WhLVEuLYk00OmSS1IdNpm8K8sG4JN9RIm2mTKcbLtc2o2vfCF2ubeXzk49OsGrXt_KYDCngOyCwZK-s3fqawWswzk.jpg,https://qcloud.dpfile.com/pc/IOf6VX3qaBgFXFVgp75w-KKJmWZjFc8GXDU8g9bQC6YGCpAmG00QbfT4vCCBj7njuzFvxlbkWx5uwqY2qcjixFEuLYk00OmSS1IdNpm8K8sG4JN9RIm2mTKcbLtc2o2vmIU_8ZGOT1OjpJmLxG6urQ.jpg', '大关', '金华路锦昌文华苑29号', 120.149192, 30.316078, 80, 0000004215, 0000003035, 37, '10:00-22:00', '2021-12-22 18:10:39', '2023-11-16 20:07:46');
INSERT INTO `tb_shop` VALUES (2, '蔡馬洪涛烤肉·老北京铜锅涮羊肉', 1, 'https://p0.meituan.net/bbia/c1870d570e73accbc9fee90b48faca41195272.jpg,http://p0.meituan.net/mogu/397e40c28fc87715b3d5435710a9f88d706914.jpg,https://qcloud.dpfile.com/pc/MZTdRDqCZdbPDUO0Hk6lZENRKzpKRF7kavrkEI99OxqBZTzPfIxa5E33gBfGouhFuzFvxlbkWx5uwqY2qcjixFEuLYk00OmSS1IdNpm8K8sG4JN9RIm2mTKcbLtc2o2vmIU_8ZGOT1OjpJmLxG6urQ.jpg', '拱宸桥/上塘', '上塘路1035号（中国工商银行旁）', 120.151505, 30.333422, 85, 0000002160, 0000001460, 46, '11:30-03:00', '2021-12-22 19:00:13', '2022-01-11 16:12:26');
INSERT INTO `tb_shop` VALUES (3, '新白鹿餐厅(运河上街店)', 1, 'https://p0.meituan.net/biztone/694233_1619500156517.jpeg,https://img.meituan.net/msmerchant/876ca8983f7395556eda9ceb064e6bc51840883.png,https://img.meituan.net/msmerchant/86a76ed53c28eff709a36099aefe28b51554088.png', '运河上街', '台州路2号运河上街购物中心F5', 120.151954, 30.32497, 61, 0000012035, 0000008045, 47, '10:30-21:00', '2021-12-22 19:10:05', '2022-01-11 16:12:42');
INSERT INTO `tb_shop` VALUES (4, 'Mamala(杭州远洋乐堤港店)', 1, 'https://img.meituan.net/msmerchant/232f8fdf09050838bd33fb24e79f30f9606056.jpg,https://qcloud.dpfile.com/pc/rDe48Xe15nQOHCcEEkmKUp5wEKWbimt-HDeqYRWsYJseXNncvMiXbuED7x1tXqN4uzFvxlbkWx5uwqY2qcjixFEuLYk00OmSS1IdNpm8K8sG4JN9RIm2mTKcbLtc2o2vmIU_8ZGOT1OjpJmLxG6urQ.jpg', '拱宸桥/上塘', '丽水路66号远洋乐堤港商城2期1层B115号', 120.146659, 30.312742, 290, 0000013519, 0000009529, 49, '11:00-22:00', '2021-12-22 19:17:15', '2022-01-11 16:12:51');
INSERT INTO `tb_shop` VALUES (5, '海底捞火锅(水晶城购物中心店）', 1, 'https://img.meituan.net/msmerchant/054b5de0ba0b50c18a620cc37482129a45739.jpg,https://img.meituan.net/msmerchant/59b7eff9b60908d52bd4aea9ff356e6d145920.jpg,https://qcloud.dpfile.com/pc/Qe2PTEuvtJ5skpUXKKoW9OQ20qc7nIpHYEqJGBStJx0mpoyeBPQOJE4vOdYZwm9AuzFvxlbkWx5uwqY2qcjixFEuLYk00OmSS1IdNpm8K8sG4JN9RIm2mTKcbLtc2o2vmIU_8ZGOT1OjpJmLxG6urQ.jpg', '大关', '上塘路458号水晶城购物中心F6', 120.15778, 30.310633, 104, 0000004125, 0000002764, 49, '10:00-07:00', '2021-12-22 19:20:58', '2022-01-11 16:13:01');
INSERT INTO `tb_shop` VALUES (6, '幸福里老北京涮锅（丝联店）', 1, 'https://img.meituan.net/msmerchant/e71a2d0d693b3033c15522c43e03f09198239.jpg,https://img.meituan.net/msmerchant/9f8a966d60ffba00daf35458522273ca658239.jpg,https://img.meituan.net/msmerchant/ef9ca5ef6c05d381946fe4a9aa7d9808554502.jpg', '拱宸桥/上塘', '金华南路189号丝联166号', 120.148603, 30.318618, 130, 0000009531, 0000007324, 46, '11:00-13:50,17:00-20:50', '2021-12-22 19:24:53', '2022-01-11 16:13:09');
INSERT INTO `tb_shop` VALUES (7, '炉鱼(拱墅万达广场店)', 1, 'https://img.meituan.net/msmerchant/909434939a49b36f340523232924402166854.jpg,https://img.meituan.net/msmerchant/32fd2425f12e27db0160e837461c10303700032.jpg,https://img.meituan.net/msmerchant/f7022258ccb8dabef62a0514d3129562871160.jpg', '北部新城', '杭行路666号万达商业中心4幢2单元409室(铺位号4005)', 120.124691, 30.336819, 85, 0000002631, 0000001320, 47, '00:00-24:00', '2021-12-22 19:40:52', '2022-01-11 16:13:19');
INSERT INTO `tb_shop` VALUES (8, '浅草屋寿司（运河上街店）', 1, 'https://img.meituan.net/msmerchant/cf3dff697bf7f6e11f4b79c4e7d989e4591290.jpg,https://img.meituan.net/msmerchant/0b463f545355c8d8f021eb2987dcd0c8567811.jpg,https://img.meituan.net/msmerchant/c3c2516939efaf36c4ccc64b0e629fad587907.jpg', '运河上街', '拱墅区金华路80号运河上街B1', 120.150526, 30.325231, 88, 0000002406, 0000001206, 46, ' 11:00-21:30', '2021-12-22 19:51:06', '2022-01-11 16:13:25');
INSERT INTO `tb_shop` VALUES (9, '羊老三羊蝎子牛仔排北派炭火锅(运河上街店)', 1, 'https://p0.meituan.net/biztone/163160492_1624251899456.jpeg,https://img.meituan.net/msmerchant/e478eb16f7e31a7f8b29b5e3bab6de205500837.jpg,https://img.meituan.net/msmerchant/6173eb1d18b9d70ace7fdb3f2dd939662884857.jpg', '运河上街', '台州路2号运河上街购物中心F5', 120.150598, 30.325251, 101, 0000002763, 0000001363, 44, '11:00-21:30', '2021-12-22 19:53:59', '2022-01-11 16:13:34');
INSERT INTO `tb_shop` VALUES (10, '开乐迪KTV（运河上街店）', 2, 'https://p0.meituan.net/joymerchant/a575fd4adb0b9099c5c410058148b307-674435191.jpg,https://p0.meituan.net/merchantpic/68f11bf850e25e437c5f67decfd694ab2541634.jpg,https://p0.meituan.net/dpdeal/cb3a12225860ba2875e4ea26c6d14fcc197016.jpg', '运河上街', '台州路2号运河上街购物中心F4', 120.149093, 30.324666, 67, 0000026891, 0000000902, 37, '00:00-24:00', '2021-12-22 20:25:16', '2021-12-22 20:25:16');
INSERT INTO `tb_shop` VALUES (11, 'INLOVE KTV(水晶城店)', 2, 'https://p0.meituan.net/dpmerchantpic/53e74b200211d68988a4f02ae9912c6c1076826.jpg,https://qcloud.dpfile.com/pc/4iWtIvzLzwM2MGgyPu1PCDb4SWEaKqUeHm--YAt1EwR5tn8kypBcqNwHnjg96EvT_Gd2X_f-v9T8Yj4uLt25Gg.jpg,https://qcloud.dpfile.com/pc/WZsJWRI447x1VG2x48Ujgu7vwqksi_9WitdKI4j3jvIgX4MZOpGNaFtM93oSSizbGybIjx5eX6WNgCPvcASYAw.jpg', '水晶城', '上塘路458号水晶城购物中心6层', 120.15853, 30.310002, 75, 0000035977, 0000005684, 47, '11:30-06:00', '2021-12-22 20:29:02', '2021-12-22 20:39:00');
INSERT INTO `tb_shop` VALUES (12, '魅(杭州远洋乐堤港店)', 2, 'https://p0.meituan.net/dpmerchantpic/63833f6ba0393e2e8722420ef33f3d40466664.jpg,https://p0.meituan.net/dpmerchantpic/ae3c94cc92c529c4b1d7f68cebed33fa105810.png,', '远洋乐堤港', '丽水路58号远洋乐堤港F4', 120.14983, 30.31211, 88, 0000006444, 0000000235, 46, '10:00-02:00', '2021-12-22 20:34:34', '2021-12-22 20:34:34');
INSERT INTO `tb_shop` VALUES (13, '讴K拉量贩KTV(北城天地店)', 2, 'https://p1.meituan.net/merchantpic/598c83a8c0d06fe79ca01056e214d345875600.jpg,https://qcloud.dpfile.com/pc/HhvI0YyocYHRfGwJWqPQr34hRGRl4cWdvlNwn3dqghvi4WXlM2FY1te0-7pE3Wb9_Gd2X_f-v9T8Yj4uLt25Gg.jpg,https://qcloud.dpfile.com/pc/F5ZVzZaXFE27kvQzPnaL4V8O9QCpVw2nkzGrxZE8BqXgkfyTpNExfNG5CEPQX4pjGybIjx5eX6WNgCPvcASYAw.jpg', 'D32天阳购物中心', '湖州街567号北城天地5层', 120.130453, 30.327655, 58, 0000018997, 0000001857, 41, '12:00-02:00', '2021-12-22 20:38:54', '2021-12-22 20:40:04');
INSERT INTO `tb_shop` VALUES (14, '星聚会KTV(拱墅区万达店)', 2, 'https://p0.meituan.net/dpmerchantpic/f4cd6d8d4eb1959c3ea826aa05a552c01840451.jpg,https://p0.meituan.net/dpmerchantpic/2efc07aed856a8ab0fc75c86f4b9b0061655777.jpg,https://qcloud.dpfile.com/pc/zWfzzIorCohKT0bFwsfAlHuayWjI6DBEMPHHncmz36EEMU9f48PuD9VxLLDAjdoU_Gd2X_f-v9T8Yj4uLt25Gg.jpg', '北部新城', '杭行路666号万达广场C座1-2F', 120.128958, 30.337252, 60, 0000017771, 0000000685, 47, '10:00-22:00', '2021-12-22 20:48:54', '2021-12-22 20:48:54');

-- ----------------------------
-- Table structure for tb_shop_type
-- ----------------------------
DROP TABLE IF EXISTS `tb_shop_type`;
CREATE TABLE `tb_shop_type`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型名称',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` int(3) UNSIGNED NULL DEFAULT NULL COMMENT '顺序',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_shop_type
-- ----------------------------
INSERT INTO `tb_shop_type` VALUES (1, '美食', '/types/ms.png', 1, '2021-12-22 20:17:47', '2021-12-23 11:24:31');
INSERT INTO `tb_shop_type` VALUES (2, 'KTV', '/types/KTV.png', 2, '2021-12-22 20:18:27', '2021-12-23 11:24:31');
INSERT INTO `tb_shop_type` VALUES (3, '丽人·美发', '/types/lrmf.png', 3, '2021-12-22 20:18:48', '2021-12-23 11:24:31');
INSERT INTO `tb_shop_type` VALUES (4, '健身运动', '/types/jsyd.png', 10, '2021-12-22 20:19:04', '2021-12-23 11:24:31');
INSERT INTO `tb_shop_type` VALUES (5, '按摩·足疗', '/types/amzl.png', 5, '2021-12-22 20:19:27', '2021-12-23 11:24:31');
INSERT INTO `tb_shop_type` VALUES (6, '美容SPA', '/types/spa.png', 6, '2021-12-22 20:19:35', '2021-12-23 11:24:31');
INSERT INTO `tb_shop_type` VALUES (7, '亲子游乐', '/types/qzyl.png', 7, '2021-12-22 20:19:53', '2021-12-23 11:24:31');
INSERT INTO `tb_shop_type` VALUES (8, '酒吧', '/types/jiuba.png', 8, '2021-12-22 20:20:02', '2021-12-23 11:24:31');
INSERT INTO `tb_shop_type` VALUES (9, '轰趴馆', '/types/hpg.png', 9, '2021-12-22 20:20:08', '2021-12-23 11:24:31');
INSERT INTO `tb_shop_type` VALUES (10, '美睫·美甲', '/types/mjmj.png', 4, '2021-12-22 20:21:46', '2021-12-23 11:24:31');

-- ----------------------------
-- Table structure for tb_sign
-- ----------------------------
DROP TABLE IF EXISTS `tb_sign`;
CREATE TABLE `tb_sign`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
  `year` year NOT NULL COMMENT '签到的年',
  `month` tinyint(2) NOT NULL COMMENT '签到的月',
  `date` date NOT NULL COMMENT '签到的日期',
  `is_backup` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '是否补签',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_sign
-- ----------------------------

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号码',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码，加密存储',
  `nick_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '昵称，默认是用户id',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '人物头像',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniqe_key_phone`(`phone`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1014 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES (1, '13686869696', '', '小鱼同学', '/imgs/blogs/blog1.jpg', '2021-12-24 10:27:19', '2022-01-11 16:04:00');
INSERT INTO `tb_user` VALUES (2, '13838411438', '', '可可今天不吃肉', '/imgs/icons/kkjtbcr.jpg', '2021-12-24 15:14:39', '2021-12-28 19:58:04');
INSERT INTO `tb_user` VALUES (4, '13456789011', '', 'user_slxaxy2au9f3tanffaxr', '', '2022-01-07 12:07:53', '2022-01-07 12:07:53');
INSERT INTO `tb_user` VALUES (5, '13456789001', '', '可爱多', '/imgs/icons/user5-icon.png', '2022-01-07 16:11:33', '2022-03-11 09:09:20');
INSERT INTO `tb_user` VALUES (6, '13456762069', '', 'user_xn5wr3hpsv', '', '2022-02-07 17:54:10', '2022-02-07 17:54:10');
INSERT INTO `tb_user` VALUES (10, '13688668889', '', 'user_88arndojw9', '', '2022-02-28 10:50:47', '2022-02-28 10:50:47');
INSERT INTO `tb_user` VALUES (900, '13688669779', '', 'user_l0lfqjjzs0', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (901, '13688669780', '', 'user_6uyxk7pa4u', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (902, '13688669781', '', 'user_f17o0qymn9', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (903, '13688669782', '', 'user_ogpqk1b39a', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (904, '13688669783', '', 'user_9jpofrgda1', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (905, '13688669784', '', 'user_n298v8udm3', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (906, '13688669785', '', 'user_0biwjc5wwt', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (907, '13688669786', '', 'user_xbbdx6wq53', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (908, '13688669787', '', 'user_nh79qly5ir', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (909, '13688669788', '', 'user_v86oajknbs', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (910, '13688669789', '', 'user_e13odsshad', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (911, '13688669790', '', 'user_6cvwrirdtl', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (912, '13688669791', '', 'user_nqr7bpgz67', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (913, '13688669792', '', 'user_wn1ae0p6gw', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (914, '13688669793', '', 'user_te48rluimb', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (915, '13688669794', '', 'user_p2r85n4k8g', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (916, '13688669795', '', 'user_ca8fdlrbty', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (917, '13688669796', '', 'user_toque00p0i', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (918, '13688669797', '', 'user_uiti5cdbhf', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (919, '13688669798', '', 'user_8pgku7viy8', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (920, '13688669799', '', 'user_cdafki4cwc', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (921, '13688669800', '', 'user_fyyk2yfpk5', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (922, '13688669801', '', 'user_78e1meevls', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (923, '13688669802', '', 'user_qzwls7m33b', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (924, '13688669803', '', 'user_jxuw8ixefk', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (925, '13688669804', '', 'user_1xye60infx', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (926, '13688669805', '', 'user_gvccna2mni', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (927, '13688669806', '', 'user_tftvpegd2c', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (928, '13688669807', '', 'user_6ihh78vpox', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (929, '13688669808', '', 'user_46qroyojdl', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (930, '13688669809', '', 'user_wwi4i2wb77', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (931, '13688669810', '', 'user_s28l0bryil', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (932, '13688669811', '', 'user_4lgib8jvrx', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (933, '13688669812', '', 'user_fczpz5s31b', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (934, '13688669813', '', 'user_3cvkn9pv9w', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (935, '13688669814', '', 'user_wtvk7gx8ar', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (936, '13688669815', '', 'user_yrel6rbyyd', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (937, '13688669816', '', 'user_hmxjnsbnon', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (938, '13688669817', '', 'user_cuxcl0d2oo', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (939, '13688669818', '', 'user_1ax8x9zw0c', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (940, '13688669819', '', 'user_p7v98oe5nm', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (941, '13688669820', '', 'user_m90rt3bwsz', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (942, '13688669821', '', 'user_xhty5jm1hy', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (943, '13688669822', '', 'user_7h88k22eo0', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (944, '13688669823', '', 'user_5a75z9jcqa', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (945, '13688669824', '', 'user_3t0twwq0nh', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (946, '13688669825', '', 'user_861ywr4gfr', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (947, '13688669826', '', 'user_iwkz8k1zpx', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (948, '13688669827', '', 'user_vzmhyoz1ap', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (949, '13688669828', '', 'user_5tmpddukgq', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (950, '13688669829', '', 'user_h6siyam4hb', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (951, '13688669830', '', 'user_n5yqq6mgka', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (952, '13688669831', '', 'user_an9epa7f2r', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (953, '13688669832', '', 'user_5vr0cdy8sz', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (954, '13688669833', '', 'user_xpanlhqjbq', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (955, '13688669834', '', 'user_3cfykc172m', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (956, '13688669835', '', 'user_1n0jceyzim', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (957, '13688669836', '', 'user_4ixi7efxtr', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (958, '13688669837', '', 'user_5adpp336iy', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (959, '13688669838', '', 'user_mflzjd6e6b', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (960, '13688669839', '', 'user_80bwfj72p7', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (961, '13688669840', '', 'user_i3anusitco', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (962, '13688669841', '', 'user_yj4pcsrkl9', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (963, '13688669842', '', 'user_7v9x6gxjdz', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (964, '13688669843', '', 'user_2ahufmnyzp', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (965, '13688669844', '', 'user_1oel6c441t', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (966, '13688669845', '', 'user_qxzcv0ib6g', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (967, '13688669846', '', 'user_9uyh0i8ykg', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (968, '13688669847', '', 'user_tb01d4d9ql', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (969, '13688669848', '', 'user_hwpkx6ovii', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (970, '13688669849', '', 'user_pqd04q9hq2', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (971, '13688669850', '', 'user_4t7wkgkufh', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (972, '13688669851', '', 'user_834e4vzf0e', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (973, '13688669852', '', 'user_pxk4urlnmo', '', '2022-02-28 10:50:52', '2022-02-28 10:50:52');
INSERT INTO `tb_user` VALUES (974, '13688669853', '', 'user_e3x6n0ff0d', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (975, '13688669854', '', 'user_wxnvsvb5ut', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (976, '13688669855', '', 'user_ehi7k4zpjb', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (977, '13688669856', '', 'user_om0pzyh3z1', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (978, '13688669857', '', 'user_9asdqbe7od', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (979, '13688669858', '', 'user_seuabngxt9', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (980, '13688669859', '', 'user_b0qvb27eiy', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (981, '13688669860', '', 'user_63sjue2tkh', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (982, '13688669861', '', 'user_cc3lvxfr1u', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (983, '13688669862', '', 'user_in37hfw5tk', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (984, '13688669863', '', 'user_jtg0c9tyqn', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (985, '13688669864', '', 'user_qzpipaj50w', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (986, '13688669865', '', 'user_ppnb4ljetq', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (987, '13688669866', '', 'user_zbcui7783k', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (988, '13688669867', '', 'user_ki4dxb9q9b', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (989, '13688669868', '', 'user_27b5dxktn0', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (990, '13688669869', '', 'user_fxvb2av882', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (991, '13688669870', '', 'user_6vp3uflnwm', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (992, '13688669871', '', 'user_7ix7djbg30', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (993, '13688669872', '', 'user_vx8r39tjiu', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (994, '13688669873', '', 'user_l2wdiwule0', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (995, '13688669874', '', 'user_z4qe1up5zx', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (996, '13688669875', '', 'user_bklo4b32lu', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (997, '13688669876', '', 'user_ax0y473ndh', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (998, '13688669877', '', 'user_yx2p44qww3', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (999, '13688669878', '', 'user_bnw9bzib34', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (1000, '13688669879', '', 'user_cdj4ojh4pc', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (1001, '13688669880', '', 'user_l7o3r96hn3', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (1002, '13688669881', '', 'user_zbehzrz279', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (1003, '13688669882', '', 'user_tql21zepcx', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (1004, '13688669883', '', 'user_jnxnrk8qt0', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (1005, '13688669884', '', 'user_8e5twg6q0k', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (1006, '13688669885', '', 'user_gfeusukbpp', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (1007, '13688669886', '', 'user_sveivfswhn', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (1008, '13688669887', '', 'user_qgf4t8jkx0', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (1009, '13688669888', '', 'user_4qh6bofkol', '', '2022-02-28 10:50:53', '2022-02-28 10:50:53');
INSERT INTO `tb_user` VALUES (1010, '13361337361', '', 'user_xur7hi03wk', '', '2023-11-15 21:28:25', '2023-11-15 21:28:25');
INSERT INTO `tb_user` VALUES (1011, '13355333333', '', 'user_6jltpyz3t2', '', '2023-11-17 16:15:32', '2023-11-17 16:15:32');
INSERT INTO `tb_user` VALUES (1012, '15192231826', '', 'user_k6f3hyx4p7', '', '2023-11-17 23:19:39', '2023-11-17 23:19:39');
INSERT INTO `tb_user` VALUES (1013, '15965625621', '', 'user_3xfyz3ngoo', '', '2024-01-01 13:42:04', '2024-01-01 13:42:04');

-- ----------------------------
-- Table structure for tb_user_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_info`;
CREATE TABLE `tb_user_info`  (
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '主键，用户id',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '城市名称',
  `introduce` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个人介绍，不要超过128个字符',
  `fans` int(8) UNSIGNED NULL DEFAULT 0 COMMENT '粉丝数量',
  `followee` int(8) UNSIGNED NULL DEFAULT 0 COMMENT '关注的人的数量',
  `gender` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '性别，0：男，1：女',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `credits` int(8) UNSIGNED NULL DEFAULT 0 COMMENT '积分',
  `level` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '会员级别，0~9级,0代表未开通会员',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_user_info
-- ----------------------------

-- ----------------------------
-- Table structure for tb_voucher
-- ----------------------------
DROP TABLE IF EXISTS `tb_voucher`;
CREATE TABLE `tb_voucher`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '商铺id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '代金券标题',
  `sub_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '副标题',
  `rules` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '使用规则',
  `pay_value` bigint(10) UNSIGNED NOT NULL COMMENT '支付金额，单位是分。例如200代表2元',
  `actual_value` bigint(10) NOT NULL COMMENT '抵扣金额，单位是分。例如200代表2元',
  `type` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '0,普通券；1,秒杀券',
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '1,上架; 2,下架; 3,过期',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_voucher
-- ----------------------------
INSERT INTO `tb_voucher` VALUES (1, 1, '50元代金券', '周一至周日均可使用', '全场通用\\n无需预约\\n可无限叠加\\不兑现、不找零\\n仅限堂食', 4750, 5000, 0, 1, '2022-01-04 09:42:39', '2022-01-04 09:43:31');
INSERT INTO `tb_voucher` VALUES (10, 1, '100元代金券', '周一至周五可使用', '全场通用\\n无需预约\\n仅限堂食', 8000, 10000, 1, 1, '2023-11-16 20:20:12', '2023-11-16 20:20:12');

-- ----------------------------
-- Table structure for tb_voucher_order
-- ----------------------------
DROP TABLE IF EXISTS `tb_voucher_order`;
CREATE TABLE `tb_voucher_order`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '下单的用户id',
  `voucher_id` bigint(20) UNSIGNED NOT NULL COMMENT '购买的代金券id',
  `pay_type` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '支付方式 1：余额支付；2：支付宝；3：微信',
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '订单状态，1：未支付；2：已支付；3：已核销；4：已取消；5：退款中；6：已退款',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  `use_time` timestamp NULL DEFAULT NULL COMMENT '核销时间',
  `refund_time` timestamp NULL DEFAULT NULL COMMENT '退款时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_voucher_order
-- ----------------------------
INSERT INTO `tb_voucher_order` VALUES (254139472671670277, 1010, 10, 1, 1, '2023-11-16 20:31:02', NULL, NULL, NULL, '2023-11-16 20:31:02');

SET FOREIGN_KEY_CHECKS = 1;
