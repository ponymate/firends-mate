/*
 Navicat MySQL Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : localhost:3306
 Source Schema         : friends_mate

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : 65001

 Date: 16/03/2023 22:13:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team`  (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `name` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '队伍名称',
                         `description` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
                         `maxNum` int(11) NOT NULL DEFAULT 1 COMMENT '最大人数',
                         `expireTime` datetime NULL DEFAULT NULL COMMENT '过期时间',
                         `userId` bigint(20) NOT NULL COMMENT '用户id（队长 id）',
                         `status` int(11) NOT NULL DEFAULT 0 COMMENT '0 - 公开，1 - 私有，2 - 加密',
                         `password` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
                         `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '队伍' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of team
-- ----------------------------
INSERT INTO `team` VALUES (1, '经纬小组', '经纬测试', 5, '2023-03-20 00:00:00', 20, 0, '', '2023-03-13 22:33:25', '2023-03-15 22:44:34', 0);
INSERT INTO `team` VALUES (2, '敬畏再改名', '改变概述', 10, '2023-03-22 16:00:00', 24, 1, '', '2023-03-15 19:23:01', '2023-03-15 21:06:39', 0);
INSERT INTO `team` VALUES (3, '敬畏小组', '测试接口', 10, '2023-03-20 00:00:00', 24, 0, '', '2023-03-15 19:23:18', '2023-03-15 19:23:18', 0);
INSERT INTO `team` VALUES (4, '敬畏小组', '测试接口', 10, '2023-03-20 00:00:00', 24, 2, '123', '2023-03-15 19:23:34', '2023-03-15 20:45:40', 0);
INSERT INTO `team` VALUES (5, '敬畏小组', '测试接口', 10, '2023-03-20 00:00:00', 24, 2, '123', '2023-03-15 19:23:47', '2023-03-15 21:07:55', 1);
INSERT INTO `team` VALUES (6, '敬畏小组', '测试接口', 10, '2023-03-20 00:00:00', 24, 0, '123', '2023-03-15 19:23:48', '2023-03-15 19:27:03', 1);
INSERT INTO `team` VALUES (7, '敬畏二组', '测试用', 3, '2023-03-21 16:00:00', 24, 1, '', '2023-03-15 19:27:24', '2023-03-15 19:27:24', 0);

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `username` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
                         `userAccount` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '账号',
                         `avatarUrl` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户头像',
                         `gender` tinyint(4) NULL DEFAULT NULL COMMENT '性别',
                         `userPassword` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
                         `phone` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话',
                         `email` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
                         `userStatus` int(11) NOT NULL DEFAULT 0 COMMENT '状态 0 - 正常',
                         `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
                         `userRole` int(11) NOT NULL DEFAULT 0 COMMENT '用户角色 0 - 普通用户 1 - 管理员',
                         `planetCode` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '星球编号',
                         `tags` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标签 json 列表',
                         `profile` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '个人简介',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '', '', 'www.abc.com', 0, '', '110', 'abc@qq.com', 0, '2023-03-11 18:18:08', '2023-03-15 15:31:19', 0, 0, '2', '[\"Java\",\"SpringBoot\",\"大三\",\"求职\"]', NULL);
INSERT INTO `user` VALUES (2, '顾擎苍', '程立辉', 'www.dannie-heidenreich.info', 1, '毛明轩', '17822795834', 'maria.boyle@gmail.com', 0, '2023-03-11 18:18:08', '2023-03-15 18:49:26', 0, 0, '374', '[\"cg\",\"男\"]', NULL);
INSERT INTO `user` VALUES (3, '谭鸿煊', '钱明轩', 'www.delbert-cartwright.io', 0, '万子涵', '15907975837', 'keven.kiehn@yahoo.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '35', '[\"Icon\"]', NULL);
INSERT INTO `user` VALUES (4, '方鹤轩', '冯擎宇', 'www.wayne-skiles.net', 1, '谭烨华', '15581254927', 'gordon.murphy@yahoo.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '354571393', '[\"bc\"]', NULL);
INSERT INTO `user` VALUES (5, '万苑博', '尹博涛', 'www.sasha-hamill.info', 1, '张荣轩', '17706788940', 'candance.hand@hotmail.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '9011643', '[\"SAS\"]', NULL);
INSERT INTO `user` VALUES (6, '黎耀杰', '陆明辉', 'www.shamika-hermann.net', 1, '李鹏', '14592863046', 'svetlana.paucek@gmail.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '373', '[\"F#\"]', NULL);
INSERT INTO `user` VALUES (7, '赵烨伟', '许擎宇', 'www.leo-kertzmann.org', 1, '谢晟睿', '17753821678', 'mckinley.smith@hotmail.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '40920964', '[\"OCaml\"]', NULL);
INSERT INTO `user` VALUES (8, '吴建辉', '石鑫磊', 'www.chantell-lehner.net', 0, '莫展鹏', '13846423508', 'deb.sporer@gmail.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '553090', '[\"MQL5\"]', NULL);
INSERT INTO `user` VALUES (9, '孙煜祺', '姜金鑫', 'www.tierra-bode.io', 1, '许建辉', '17146876214', 'nichelle.ortiz@hotmail.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '724', '[\"JavaScript\"]', NULL);
INSERT INTO `user` VALUES (10, '秦瑞霖', '孙绍齐', 'www.stephine-hirthe.com', 1, '郭弘文', '17594206421', 'lindy.abshire@gmail.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '591', '[\"bc\"]', NULL);
INSERT INTO `user` VALUES (11, '宋明辉', '许明杰', 'www.alise-yundt.org', 0, '戴绍齐', '15977084341', 'tracey.cruickshank@gmail.com', 0, '2023-03-11 18:18:09', '2023-03-15 18:33:38', 0, 0, '4374047', '[\"Java\"]', NULL);
INSERT INTO `user` VALUES (12, '钱志泽', '江金鑫', 'www.ardelia-kuvalis.com', 1, '熊远航', '15974257598', 'carlton.erdman@gmail.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '24565', '[\"Visual Basic\"]', NULL);
INSERT INTO `user` VALUES (13, '姜思源', '邓哲瀚', 'www.dalila-rempel.info', 0, '贾荣轩', '17189973677', 'johnny.murazik@yahoo.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '2368127', '[\"Dart\"]', NULL);
INSERT INTO `user` VALUES (14, '吕烨磊', '胡果', 'www.tomas-huel.name', 1, '丁楷瑞', '13818530445', 'elly.hills@gmail.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '6', '[\"CLIPS\"]', NULL);
INSERT INTO `user` VALUES (15, '杨思淼', '薛烨霖', 'www.rea-brekke.co', 1, '吴黎昕', '15865362557', 'jasmin.batz@yahoo.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '728', '[\"Forth\"]', NULL);
INSERT INTO `user` VALUES (16, '侯鸿煊', '魏浩轩', 'www.marty-zulauf.name', 0, '韦旭尧', '18161911390', 'suzie.hammes@gmail.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '5758470', '[\"SAS\"]', NULL);
INSERT INTO `user` VALUES (17, '姜雨泽', '杨哲瀚', 'www.cherrie-reilly.name', 1, '郑立辉', '14702360543', 'estell.kiehn@hotmail.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '47', '[\"Dart\"]', NULL);
INSERT INTO `user` VALUES (18, '钱绍辉', '田哲瀚', 'www.noma-marquardt.co', 1, '姚钰轩', '15749987306', 'lady.daugherty@hotmail.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '613', '[\"ML\"]', NULL);
INSERT INTO `user` VALUES (19, '邓明杰', '龙浩然', 'www.jamie-bradtke.info', 1, '秦昊天', '15841265963', 'craig.carter@hotmail.com', 0, '2023-03-11 18:18:09', '2023-03-11 19:07:23', 0, 0, '15849448', '[\"LabVIEW\"]', NULL);
INSERT INTO `user` VALUES (20, '严鑫磊', '陈凯瑞', 'www.jan-schowalter.com', 0, '林黎昕', '15661876168', 'rudolph.jakubowski@hotmail.com', 0, '2023-03-11 18:18:09', '2023-03-15 19:14:55', 0, 0, '1092966243', '[\"Crystal\",\"大三\"]', NULL);
INSERT INTO `user` VALUES (21, '王宇硕', 'wangyushuo', NULL, 0, 'f811a1190f2216902de07ecd06cc8e71', NULL, NULL, 0, '2023-03-15 20:02:55', '2023-03-15 22:19:34', 0, 0, NULL, NULL, NULL);
INSERT INTO `user` VALUES (24, 'majingwei', 'ponymate', NULL, NULL, 'f811a1190f2216902de07ecd06cc8e71', NULL, NULL, 0, '2023-03-15 16:24:23', '2023-03-15 19:16:19', 0, 0, 'abc', '[\"Java\",\"Crystal\",\"大三\",\"Visual Basic\",\"求职\"]', NULL);

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Table structure for user_team
-- ----------------------------
DROP TABLE IF EXISTS `user_team`;
CREATE TABLE `user_team`  (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `userId` bigint(20) NOT NULL COMMENT '用户id',
                              `teamId` bigint(20) NOT NULL COMMENT '队伍id',
                              `joinTime` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '加入时间',
                              `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户队伍关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_team
-- ----------------------------
INSERT INTO `user_team` VALUES (1, 20, 1, '2023-03-15 22:39:13', '2023-03-13 22:33:25', '2023-03-15 22:39:13', 0);
INSERT INTO `user_team` VALUES (2, 24, 2, '2023-03-15 11:23:01', '2023-03-15 19:23:01', '2023-03-15 19:23:01', 0);
INSERT INTO `user_team` VALUES (3, 24, 3, '2023-03-15 11:23:18', '2023-03-15 19:23:18', '2023-03-15 19:23:18', 0);
INSERT INTO `user_team` VALUES (4, 24, 4, '2023-03-15 11:23:34', '2023-03-15 19:23:34', '2023-03-15 19:23:34', 0);
INSERT INTO `user_team` VALUES (5, 24, 5, '2023-03-15 11:23:48', '2023-03-15 19:23:47', '2023-03-15 21:07:55', 1);
INSERT INTO `user_team` VALUES (6, 24, 6, '2023-03-15 11:23:48', '2023-03-15 19:23:48', '2023-03-15 19:27:03', 1);
INSERT INTO `user_team` VALUES (7, 24, 7, '2023-03-15 11:27:25', '2023-03-15 19:27:24', '2023-03-15 19:27:24', 0);
INSERT INTO `user_team` VALUES (11, 2, 1, '2023-03-15 22:37:21', '2023-03-15 22:20:41', '2023-03-15 22:37:21', 0);
INSERT INTO `user_team` VALUES (12, 3, 1, '2023-03-15 22:37:25', '2023-03-15 22:21:03', '2023-03-15 22:37:25', 0);
INSERT INTO `user_team` VALUES (13, 4, 1, '2023-03-15 22:37:30', '2023-03-15 22:21:20', '2023-03-15 22:37:30', 0);
INSERT INTO `user_team` VALUES (19, 21, 1, '2023-03-15 22:57:30', '2023-03-15 22:56:33', '2023-03-15 22:57:30', 1);
INSERT INTO `user_team` VALUES (20, 21, 1, '2023-03-15 22:57:53', '2023-03-15 22:57:43', '2023-03-15 22:57:53', 1);
INSERT INTO `user_team` VALUES (21, 21, 1, '2023-03-15 22:59:02', '2023-03-15 22:58:41', '2023-03-15 22:59:02', 1);

SET FOREIGN_KEY_CHECKS = 1;


-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                        `tagName` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标签名称',
                        `userId` bigint(20) NULL DEFAULT NULL COMMENT '用户 id',
                        `parentId` bigint(20) NULL DEFAULT NULL COMMENT '父标签 id',
                        `isParent` tinyint(4) NULL DEFAULT NULL COMMENT '0 - 不是, 1 - 父标签',
                        `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
                        PRIMARY KEY (`id`) USING BTREE,
                        UNIQUE INDEX `uniIdx_tagName`(`tagName`) USING BTREE,
                        INDEX `idx_userId`(`userId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, 'Java', NULL, NULL, 0, '2023-03-15 18:25:50', '2023-03-15 18:25:50', 0);
INSERT INTO `tag` VALUES (2, 'SpringBoot', NULL, NULL, 0, '2023-03-15 18:26:07', '2023-03-15 18:26:07', 0);
INSERT INTO `tag` VALUES (3, 'cg', NULL, NULL, 0, '2023-03-15 18:26:43', '2023-03-15 18:26:43', 0);
INSERT INTO `tag` VALUES (4, 'Icon', NULL, NULL, 0, '2023-03-15 18:26:54', '2023-03-15 18:26:54', 0);

SET FOREIGN_KEY_CHECKS = 1;