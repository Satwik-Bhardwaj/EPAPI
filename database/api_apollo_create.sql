/*
 Navicat Premium Data Transfer

 Source Server         : pro_entpackApi
 Source Server Type    : MySQL
 Source Server Version : 80035 (8.0.35)
 Source Host           : localhost:3310
 Source Schema         : entpackApi

 Target Server Type    : MySQL
 Target Server Version : 80035 (8.0.35)
 File Encoding         : 65001

 Date: 20/04/2024 07:58:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE entpack;

-- ----------------------------
-- Table structure for apollo_create
-- ----------------------------
DROP TABLE IF EXISTS `entpackApi`.`api_apollo_create`;
CREATE TABLE `entpack`.`api_apollo_create`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `uid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `api` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `agentId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '代理',
  `memberId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL UNIQUE COMMENT '会员信息 id',
  `currency` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '币种',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `userName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `pwd` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `createDate` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for apollo_ticket
-- ----------------------------
DROP TABLE IF EXISTS `entpackApi`.`api_apollo_ticket`;
CREATE TABLE `entpack`.	`api_apollo_ticket`  (
  `uuid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '注单号 (唯一值)',
  `uid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `BeginBlance` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `ClassID` int NULL DEFAULT NULL,
  `CreateTime` datetime NULL DEFAULT NULL,
  `EndBlance` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `GameID` int NULL DEFAULT NULL,
  `GameType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `GameName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `des` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `LineNum` int NULL DEFAULT NULL,
  `LogDataStr` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `LogDataType` int NULL DEFAULT NULL,
  `RoundNO` int NULL DEFAULT NULL,
  `Rownum` int NULL DEFAULT NULL,
  `TableID` int NULL DEFAULT NULL,
  `Win` decimal(12, 2) NULL DEFAULT NULL,
  `bet` decimal(12, 2) NULL DEFAULT NULL,
  `cday` int NULL DEFAULT NULL,
  `cno` int NULL DEFAULT NULL,
  `id` int NULL DEFAULT NULL,
  `ticketStatus` tinyint NULL DEFAULT 0 COMMENT 'redisTicket 状态',
  `ticketMsg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'redisTicket 信息',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `api` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for apollo_transfer_log
-- ----------------------------
DROP TABLE IF EXISTS `entpackApi`.`api_apollo_transfer_log`;
CREATE TABLE `entpack`.`api_apollo_transfer_log`  (
  `tranId` int NOT NULL AUTO_INCREMENT COMMENT '唯一键',
  `api` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员账号',
  `memberId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `txCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'txId 转账单据号',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '1 withdraw 提出;0 deposit 存入',
  `amount` int NULL DEFAULT NULL COMMENT '转账金额',
  `createDate` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `errCode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Error Code 报错码',
  `errMsg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Error Message 报错内容',
  `url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `after` decimal(15, 2) NULL DEFAULT NULL COMMENT '改动后',
  `before` decimal(15, 2) NULL DEFAULT NULL COMMENT '改动前',
  `statusCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `tranStatus` tinyint NULL DEFAULT 1,
  PRIMARY KEY (`txCode`) USING BTREE,
  UNIQUE INDEX `id`(`tranId` ASC) USING BTREE,
  INDEX `referenceid`(`txCode` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 190 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;