/*
Navicat MySQL Data Transfer

Source Server         : 本机
Source Server Version : 50625
Source Host           : localhost:3306
Source Database       : liberty

Target Server Type    : MYSQL
Target Server Version : 50625
File Encoding         : 65001

Date: 2018-07-19 19:25:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for currency
-- ----------------------------
DROP TABLE IF EXISTS `currency`;
CREATE TABLE `currency` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL COMMENT '代号如:usa/gby等',
  `name` varchar(255) NOT NULL COMMENT '名称如:美元/欧元等',
  `followed` tinyint(1) DEFAULT NULL COMMENT '是否处于跟踪状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=202 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dictionary
-- ----------------------------
DROP TABLE IF EXISTS `dictionary`;
CREATE TABLE `dictionary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` char(4) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for kline
-- ----------------------------
DROP TABLE IF EXISTS `kline`;
CREATE TABLE `kline` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日期',
  `max` double(20,6) NOT NULL COMMENT '最大值',
  `min` double(20,6) NOT NULL COMMENT '最小值',
  `currencyId` int(11) NOT NULL COMMENT '币种id',
  `strokeId` int(11) DEFAULT NULL COMMENT '笔id',
  `type` char(1) DEFAULT NULL COMMENT 'K线级别',
  PRIMARY KEY (`id`),
  KEY `currencyId` (`currencyId`),
  KEY `strokeId` (`strokeId`),
  CONSTRAINT `kline_ibfk_1` FOREIGN KEY (`currencyId`) REFERENCES `currency` (`id`),
  CONSTRAINT `kline_ibfk_2` FOREIGN KEY (`strokeId`) REFERENCES `stroke` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2798699 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for line
-- ----------------------------
DROP TABLE IF EXISTS `line`;
CREATE TABLE `line` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `max` double(20,6) NOT NULL COMMENT '最大值',
  `min` double(20,6) NOT NULL COMMENT '最小值',
  `startDate` timestamp NULL DEFAULT NULL COMMENT '开始K线的日期',
  `endDate` timestamp NULL DEFAULT NULL COMMENT '结束K线的日期',
  `parentId` int(11) DEFAULT NULL COMMENT '父线段的id',
  `currencyId` int(11) DEFAULT NULL COMMENT '币种id',
  `prevId` int(11) DEFAULT NULL COMMENT '前一根线段的id',
  `nextId` int(11) DEFAULT NULL COMMENT '后一根线段的id',
  `type` char(1) DEFAULT NULL COMMENT '线段级别',
  PRIMARY KEY (`id`),
  KEY `parentId` (`parentId`),
  KEY `currencyId` (`currencyId`),
  KEY `prevId` (`prevId`),
  KEY `nextId` (`nextId`),
  CONSTRAINT `line_ibfk_1` FOREIGN KEY (`parentId`) REFERENCES `line` (`id`),
  CONSTRAINT `line_ibfk_2` FOREIGN KEY (`currencyId`) REFERENCES `currency` (`id`),
  CONSTRAINT `line_ibfk_3` FOREIGN KEY (`prevId`) REFERENCES `line` (`id`),
  CONSTRAINT `line_ibfk_4` FOREIGN KEY (`nextId`) REFERENCES `line` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for stroke
-- ----------------------------
DROP TABLE IF EXISTS `stroke`;
CREATE TABLE `stroke` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `max` double(20,6) NOT NULL COMMENT '最大值',
  `min` double(20,6) NOT NULL COMMENT '最小值',
  `startDate` timestamp NULL DEFAULT NULL COMMENT '开始K线的日期',
  `endDate` timestamp NULL DEFAULT NULL COMMENT '结束K线的日期',
  `currencyId` int(11) DEFAULT NULL COMMENT '币种id',
  `lineId` int(11) DEFAULT NULL COMMENT '线段id',
  `prevId` int(11) DEFAULT NULL COMMENT '前一笔的id',
  `nextId` int(11) DEFAULT NULL COMMENT '后一笔的id',
  `type` char(1) DEFAULT NULL COMMENT '笔级别',
  `direction` char(1) DEFAULT NULL COMMENT '笔的方向,上/下',
  PRIMARY KEY (`id`),
  KEY `currencyId` (`currencyId`),
  KEY `lineId` (`lineId`),
  KEY `prevId` (`prevId`),
  KEY `nextId` (`nextId`),
  CONSTRAINT `stroke_ibfk_2` FOREIGN KEY (`currencyId`) REFERENCES `currency` (`id`),
  CONSTRAINT `stroke_ibfk_3` FOREIGN KEY (`lineId`) REFERENCES `line` (`id`),
  CONSTRAINT `stroke_ibfk_4` FOREIGN KEY (`prevId`) REFERENCES `stroke` (`id`),
  CONSTRAINT `stroke_ibfk_5` FOREIGN KEY (`nextId`) REFERENCES `stroke` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3933 DEFAULT CHARSET=utf8;
SET FOREIGN_KEY_CHECKS=1;
