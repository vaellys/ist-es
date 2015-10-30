/*
Navicat Oracle Data Transfer
Oracle Client Version : 11.2.0.1.0

Source Server         : 10.6.50.110(awp_n)
Source Server Version : 110200
Source Host           : 10.6.50.110:1521
Source Schema         : AWP

Target Server Type    : ORACLE
Target Server Version : 110200
File Encoding         : 65001

Date: 2015-10-30 16:48:42
*/


-- ----------------------------
-- Table structure for T12_ES_ORGAN_INDEX_ASSO
-- ----------------------------
DROP TABLE "AWP"."T12_ES_ORGAN_INDEX_ASSO";
CREATE TABLE "AWP"."T12_ES_ORGAN_INDEX_ASSO" (
"ES_ID" VARCHAR2(32 BYTE) NOT NULL ,
"ORGAN_KEY" VARCHAR2(32 BYTE) NOT NULL ,
"ES_INDEX" VARCHAR2(32 BYTE) NOT NULL ,
"ES_TYPE" VARCHAR2(10 BYTE) NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON COLUMN "AWP"."T12_ES_ORGAN_INDEX_ASSO"."ES_ID" IS '文档ID';
COMMENT ON COLUMN "AWP"."T12_ES_ORGAN_INDEX_ASSO"."ORGAN_KEY" IS '机构key';
COMMENT ON COLUMN "AWP"."T12_ES_ORGAN_INDEX_ASSO"."ES_INDEX" IS '索引类型';
COMMENT ON COLUMN "AWP"."T12_ES_ORGAN_INDEX_ASSO"."ES_TYPE" IS '文档类型 1.sql 2.附件 3.文档目录';

-- ----------------------------
-- Indexes structure for table T12_ES_ORGAN_INDEX_ASSO
-- ----------------------------

-- ----------------------------
-- Checks structure for table T12_ES_ORGAN_INDEX_ASSO
-- ----------------------------
ALTER TABLE "AWP"."T12_ES_ORGAN_INDEX_ASSO" ADD CHECK ("ES_ID" IS NOT NULL);
ALTER TABLE "AWP"."T12_ES_ORGAN_INDEX_ASSO" ADD CHECK ("ORGAN_KEY" IS NOT NULL);
ALTER TABLE "AWP"."T12_ES_ORGAN_INDEX_ASSO" ADD CHECK ("ES_INDEX" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table T12_ES_ORGAN_INDEX_ASSO
-- ----------------------------
ALTER TABLE "AWP"."T12_ES_ORGAN_INDEX_ASSO" ADD PRIMARY KEY ("ES_ID", "ORGAN_KEY", "ES_INDEX");
