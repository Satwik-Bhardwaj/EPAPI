/*
 Navicat Premium Data Transfer

 Source Server         : pro_entpack
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

-- ----------------------------
-- Table structure for api_apollo_create
-- ----------------------------
DROP TABLE IF EXISTS `api_apollo_create`;
CREATE TABLE `api_apollo_create`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `api` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `agentId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '代理',
  `memberId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会员信息 id',
  `currency` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '币种',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `userName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `pwd` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `createDate` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for api_apollo_ticket
-- ----------------------------
DROP TABLE IF EXISTS `api_apollo_ticket`;
CREATE TABLE `api_apollo_ticket`  (
  `uuid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '注单号 (唯一值)',
  `BeginBlance` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `ClassID` int NULL DEFAULT NULL,
  `CreateTime` datetime NULL DEFAULT NULL,
  `EndBlance` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `GameID` int NULL DEFAULT NULL,
  `GameName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
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
-- Table structure for api_apollo_transfer_log
-- ----------------------------
DROP TABLE IF EXISTS `api_apollo_transfer_log`;
CREATE TABLE `api_apollo_transfer_log`  (
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

SET FOREIGN_KEY_CHECKS = 1;



INSERT INTO `api_apollo_create` (`id`, `api`, `agentId`, `memberId`, `currency`, `account`, `userName`, `pwd`, `createDate`) VALUES (1, 'apollo', 'kiss1356', 'xLGwiudp', 'MYR', '01851235313', '340test', '11111111', '2023-12-18 19:57:27');



INSERT INTO `api_apollo_ticket` (`uuid`, `BeginBlance`, `ClassID`, `CreateTime`, `EndBlance`, `GameID`, `GameName`, `LineNum`, `LogDataStr`, `LogDataType`, `RoundNO`, `Rownum`, `TableID`, `Win`, `bet`, `cday`, `cno`, `id`, `ticketStatus`, `ticketMsg`, `account`, `api`) VALUES ('00000c16-0ec1-45d6-ada7-c9ccedaefc6f', '2006', 3, '2024-04-15 00:41:58', '1979', 166, 'GodofWealth', 9, '27.00,0.00,1979.00,7,8,2,8,5,5,4,5,2,11,7,5,1,8,5,3.00,9,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,1,0,0', 2, 0, 25, 0, 0.00, 27.00, 0, 0, 0, 1, NULL, '01179628344', 'apollo');
INSERT INTO `api_apollo_ticket` (`uuid`, `BeginBlance`, `ClassID`, `CreateTime`, `EndBlance`, `GameID`, `GameName`, `LineNum`, `LogDataStr`, `LogDataType`, `RoundNO`, `Rownum`, `TableID`, `Win`, `bet`, `cday`, `cno`, `id`, `ticketStatus`, `ticketMsg`, `account`, `api`) VALUES ('00016d26-32f8-4023-b089-67e403315f87', '13636', 3, '2024-04-15 11:22:08', '13636', 166, 'GodofWealth', 9, '36.00,36.00,13636.00,5,4,8,8,3,4,2,5,5,9,6,6,3,5,3,4.00,9,28.00,0.00,0.00,0.00,0.00,0.00,0.00,8.00,0.00,0,0,0', 2, 0, 18, 0, 36.00, 36.00, 0, 0, 0, 1, NULL, '01179628344', 'apollo');
INSERT INTO `api_apollo_ticket` (`uuid`, `BeginBlance`, `ClassID`, `CreateTime`, `EndBlance`, `GameID`, `GameName`, `LineNum`, `LogDataStr`, `LogDataType`, `RoundNO`, `Rownum`, `TableID`, `Win`, `bet`, `cday`, `cno`, `id`, `ticketStatus`, `ticketMsg`, `account`, `api`) VALUES ('00025f23-61f0-467d-9e10-9ed5858e4821', '34997.8', 3, '2024-04-15 14:59:22', '34997.8', 166, 'GodofWealth', 9, '45.00,45.00,34997.80,8,5,5,3,7,10,8,8,3,6,10,5,6,4,4,5.00,9,10.00,0.00,0.00,0.00,0.00,0.00,0.00,35.00,0.00,0,0,0', 2, 0, 5, 0, 45.00, 45.00, 0, 0, 0, 1, NULL, '01179628344', 'apollo');
INSERT INTO `api_apollo_ticket` (`uuid`, `BeginBlance`, `ClassID`, `CreateTime`, `EndBlance`, `GameID`, `GameName`, `LineNum`, `LogDataStr`, `LogDataType`, `RoundNO`, `Rownum`, `TableID`, `Win`, `bet`, `cday`, `cno`, `id`, `ticketStatus`, `ticketMsg`, `account`, `api`) VALUES ('0008c6d5-655d-46f1-b205-59a78583ec8d', '14169', 3, '2024-04-15 11:47:29', '14124', 166, 'GodofWealth', 9, '45.00,0.00,14124.00,5,4,3,9,2,2,5,11,9,2,4,6,6,2,5,5.00,9,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,1,0,0', 2, 0, 27, 0, 0.00, 45.00, 0, 0, 0, 1, NULL, '01179628344', 'apollo');
INSERT INTO `api_apollo_ticket` (`uuid`, `BeginBlance`, `ClassID`, `CreateTime`, `EndBlance`, `GameID`, `GameName`, `LineNum`, `LogDataStr`, `LogDataType`, `RoundNO`, `Rownum`, `TableID`, `Win`, `bet`, `cday`, `cno`, `id`, `ticketStatus`, `ticketMsg`, `account`, `api`) VALUES ('000c5066-4ef3-4c27-bac1-cc9a83a788cf', '10178', 3, '2024-04-15 12:52:03', '10142', 166, 'GodofWealth', 9, '36.00,0.00,10142.00,9,6,8,6,6,5,8,5,1,6,5,11,3,8,7,4.00,9,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,1,0,0', 2, 0, 14, 0, 0.00, 36.00, 0, 0, 0, 1, NULL, '01179628344', 'apollo');
INSERT INTO `api_apollo_ticket` (`uuid`, `BeginBlance`, `ClassID`, `CreateTime`, `EndBlance`, `GameID`, `GameName`, `LineNum`, `LogDataStr`, `LogDataType`, `RoundNO`, `Rownum`, `TableID`, `Win`, `bet`, `cday`, `cno`, `id`, `ticketStatus`, `ticketMsg`, `account`, `api`) VALUES ('00126719-d14c-403a-a653-79abdc8d1a16', '33997.8', 3, '2024-04-15 14:57:56', '34107.8', 166, 'GodofWealth', 9, '45.00,155.00,34107.80,5,8,10,4,3,5,7,5,5,9,2,6,6,5,9,5.00,9,35.00,0.00,0.00,0.00,35.00,0.00,35.00,50.00,0.00,0,0,0', 2, 0, 2, 0, 155.00, 45.00, 0, 0, 0, 1, NULL, '01179628344', 'apollo');
INSERT INTO `api_apollo_ticket` (`uuid`, `BeginBlance`, `ClassID`, `CreateTime`, `EndBlance`, `GameID`, `GameName`, `LineNum`, `LogDataStr`, `LogDataType`, `RoundNO`, `Rownum`, `TableID`, `Win`, `bet`, `cday`, `cno`, `id`, `ticketStatus`, `ticketMsg`, `account`, `api`) VALUES ('00132c7f-a05f-43b6-9f1c-4ea422c90eb4', '11845', 3, '2024-04-15 10:56:45', '11818', 166, 'GodofWealth', 9, '27.00,0.00,11818.00,5,3,8,4,6,1,5,7,5,4,1,8,1,2,7,3.00,9,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0,0,0', 2, 0, 3, 0, 0.00, 27.00, 0, 0, 0, 1, NULL, '01179628344', 'apollo');
INSERT INTO `api_apollo_ticket` (`uuid`, `BeginBlance`, `ClassID`, `CreateTime`, `EndBlance`, `GameID`, `GameName`, `LineNum`, `LogDataStr`, `LogDataType`, `RoundNO`, `Rownum`, `TableID`, `Win`, `bet`, `cday`, `cno`, `id`, `ticketStatus`, `ticketMsg`, `account`, `api`) VALUES ('00139f4f-b23c-4d00-aecd-f477fb68cacd', '4868', 3, '2024-04-15 22:31:40', '4848', 166, 'GodofWealth', 9, '36.00,16.00,4848.00,5,6,3,3,2,9,6,1,6,4,9,8,10,7,4,4.00,9,0.00,0.00,8.00,0.00,0.00,0.00,0.00,0.00,8.00,0,0,0', 2, 0, 20, 0, 16.00, 36.00, 0, 0, 0, 1, NULL, '01179628344', 'apollo');
INSERT INTO `api_apollo_ticket` (`uuid`, `BeginBlance`, `ClassID`, `CreateTime`, `EndBlance`, `GameID`, `GameName`, `LineNum`, `LogDataStr`, `LogDataType`, `RoundNO`, `Rownum`, `TableID`, `Win`, `bet`, `cday`, `cno`, `id`, `ticketStatus`, `ticketMsg`, `account`, `api`) VALUES ('0013c8da-d3d3-4e1a-8f56-4a6bcba7b578', '4744', 3, '2024-04-15 22:31:12', '4708', 166, 'GodofWealth', 9, '36.00,0.00,4708.00,1,9,5,6,5,1,1,3,8,11,10,1,7,2,6,4.00,9,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,1,0,0', 2, 0, 5, 0, 0.00, 36.00, 0, 0, 0, 1, NULL, '01179628344', 'apollo');
INSERT INTO `api_apollo_ticket` (`uuid`, `BeginBlance`, `ClassID`, `CreateTime`, `EndBlance`, `GameID`, `GameName`, `LineNum`, `LogDataStr`, `LogDataType`, `RoundNO`, `Rownum`, `TableID`, `Win`, `bet`, `cday`, `cno`, `id`, `ticketStatus`, `ticketMsg`, `account`, `api`) VALUES ('00193de3-b568-452d-8b3a-fa38f279c479', '15049', 3, '2024-04-15 12:36:34', '15004', 166, 'GodofWealth', 9, '45.00,0.00,15004.00,9,5,7,8,7,5,8,6,7,2,5,5,4,11,5,5.00,9,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,1,0,0', 2, 0, 14, 0, 0.00, 45.00, 0, 0, 0, 1, NULL, '01179628344', 'apollo');


INSERT INTO `api_apollo_transfer_log` (`tranId`, `api`, `account`, `memberId`, `txCode`, `type`, `amount`, `createDate`, `errCode`, `errMsg`, `url`, `after`, `before`, `statusCode`, `tranStatus`) VALUES (113, 'apollo', '420Cag20bo', 'aZBRcqWi', '0P7kzaWJ', '0', 1087, '2024-04-14 23:39:37', NULL, NULL, NULL, NULL, NULL, '0', 1);
INSERT INTO `api_apollo_transfer_log` (`tranId`, `api`, `account`, `memberId`, `txCode`, `type`, `amount`, `createDate`, `errCode`, `errMsg`, `url`, `after`, `before`, `statusCode`, `tranStatus`) VALUES (67, 'apollosgd', '530test', '0xvc16Ey', '0xvc16Eyapollosgd-Qiqb4i0d', '1', -9758, '2024-02-24 14:09:29', NULL, NULL, NULL, NULL, NULL, '0', 1);
INSERT INTO `api_apollo_transfer_log` (`tranId`, `api`, `account`, `memberId`, `txCode`, `type`, `amount`, `createDate`, `errCode`, `errMsg`, `url`, `after`, `before`, `statusCode`, `tranStatus`) VALUES (46, 'apollo', '340test', 'xLGwiudp', '18ytM0HF', '0', 40083, '2024-01-25 13:38:39', NULL, NULL, NULL, NULL, NULL, '0', 1);
INSERT INTO `api_apollo_transfer_log` (`tranId`, `api`, `account`, `memberId`, `txCode`, `type`, `amount`, `createDate`, `errCode`, `errMsg`, `url`, `after`, `before`, `statusCode`, `tranStatus`) VALUES (47, 'apollo', '340test', 'xLGwiudp', '1cmrx8ey-8GQz9JM0', '1', -40083, '2024-01-25 13:39:04', NULL, NULL, NULL, NULL, NULL, '0', 1);
INSERT INTO `api_apollo_transfer_log` (`tranId`, `api`, `account`, `memberId`, `txCode`, `type`, `amount`, `createDate`, `errCode`, `errMsg`, `url`, `after`, `before`, `statusCode`, `tranStatus`) VALUES (108, 'apollo', '420Cag20bo', 'aZBRcqWi', '26oG4P7C', '0', 1500, '2024-04-14 22:17:28', NULL, NULL, NULL, NULL, NULL, '0', 1);
INSERT INTO `api_apollo_transfer_log` (`tranId`, `api`, `account`, `memberId`, `txCode`, `type`, `amount`, `createDate`, `errCode`, `errMsg`, `url`, `after`, `before`, `statusCode`, `tranStatus`) VALUES (61, 'apollo', '410test', 'KPGiOSAd', '3ibfGkAN', '0', 1047, '2024-02-22 16:14:41', NULL, NULL, NULL, NULL, NULL, '0', 1);
INSERT INTO `api_apollo_transfer_log` (`tranId`, `api`, `account`, `memberId`, `txCode`, `type`, `amount`, `createDate`, `errCode`, `errMsg`, `url`, `after`, `before`, `statusCode`, `tranStatus`) VALUES (175, 'apollo', '420Cag20bo', 'aZBRcqWi', '3tiaGXHb', '0', 2927, '2024-04-17 12:26:10', NULL, NULL, NULL, NULL, NULL, '0', 1);
INSERT INTO `api_apollo_transfer_log` (`tranId`, `api`, `account`, `memberId`, `txCode`, `type`, `amount`, `createDate`, `errCode`, `errMsg`, `url`, `after`, `before`, `statusCode`, `tranStatus`) VALUES (174, 'apollo', '420Cag20bo', 'aZBRcqWi', '3tiaGXHb-kkcQktME', '1', -2927, '2024-04-17 12:26:10', NULL, NULL, NULL, NULL, NULL, '0', 1);
INSERT INTO `api_apollo_transfer_log` (`tranId`, `api`, `account`, `memberId`, `txCode`, `type`, `amount`, `createDate`, `errCode`, `errMsg`, `url`, `after`, `before`, `statusCode`, `tranStatus`) VALUES (177, 'apollo', '420Cag20bo', 'aZBRcqWi', '3vsF9VJO', '0', 2927, '2024-04-17 12:30:03', NULL, NULL, NULL, NULL, NULL, '0', 1);
INSERT INTO `api_apollo_transfer_log` (`tranId`, `api`, `account`, `memberId`, `txCode`, `type`, `amount`, `createDate`, `errCode`, `errMsg`, `url`, `after`, `before`, `statusCode`, `tranStatus`) VALUES (57, 'apollo', '410test2', 'oYMPObuS', '465oV1bG', '0', 924, '2024-02-18 17:05:18', NULL, NULL, NULL, NULL, NULL, '0', 1);



