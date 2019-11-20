
ALTER TABLE `sys_user`
  ADD COLUMN `union_id` varchar(100) NULL COMMENT 'UNIONID，唯一键' AFTER `realname`,
  ADD UNIQUE INDEX `uniq_sys_user_union_id` (`union_id`);

  ALTER TABLE `sys_user`
  ADD COLUMN `referral_code` varchar(6) NULL COMMENT '推荐键' AFTER `union_id`,
  ADD UNIQUE INDEX `uniq_sys_user_referral_code` (`referral_code`);


ALTER TABLE `sys_user`
  ADD COLUMN `refer_user_id` varchar(32) NULL COMMENT '推荐人id' AFTER `referral_code`;


ALTER TABLE `wb_course`
  ADD COLUMN `special_topic` varchar(3) NULL COMMENT '专题' AFTER `publish_date`;

ALTER TABLE `wb_course`
  ADD COLUMN `price` double(8,2) NULL COMMENT '价格' AFTER `special_topic`;


ALTER TABLE `wb_course`
  ADD COLUMN `duration` varchar(32) NULL COMMENT '时长' AFTER `price`;


ALTER TABLE `wb_course_comment`
  ADD COLUMN `publish` varchar(1) default 0 COMMENT '发布' AFTER `content`;

ALTER TABLE `sys_user`
  ADD COLUMN `nickname` varchar(100) NULL COMMENT '昵称' AFTER `realname`;

ALTER TABLE `sys_user`
  ADD COLUMN `identity_no` varchar(18) NULL COMMENT '身份证号码' AFTER `nickname`;
ALTER TABLE `sys_user`
  ADD COLUMN `card_no` varchar(20) NULL COMMENT '银行卡号' AFTER `identity_no`;
ALTER TABLE `sys_user`
  ADD COLUMN `bank_name` varchar(50) NULL COMMENT '开户行名称' AFTER `card_no`;

ALTER TABLE `sys_user`
  ADD COLUMN `card_type` varchar(50) NULL COMMENT '银行卡类型' AFTER `card_no`;
ALTER TABLE `sys_user`
  ADD COLUMN `card_phone` varchar(50) NULL COMMENT '银行卡关联手机号' AFTER `card_type`;

  ALTER TABLE `sys_user`
  ADD COLUMN `balance` double(8,2) NULL COMMENT '账户余额' AFTER `card_phone`;
  ALTER TABLE `sys_user`
  ADD COLUMN `available_balance` double(8,2) NULL COMMENT '可提现余额' AFTER `balance`;
  ALTER TABLE `sys_user`
  ADD COLUMN `total_income` double(8,2) NULL COMMENT '累计收入' AFTER `available_balance`;

  ALTER TABLE `sys_user`
  ADD COLUMN `is_member` tinyint(1) NULL COMMENT '是否会员' AFTER `total_income`;



ALTER TABLE `wb_course`
  ADD COLUMN `category_code` varchar(32) NULL COMMENT '类别值' AFTER `category`;


ALTER TABLE `wb_class`
  ADD COLUMN `duration` varchar(32) NULL COMMENT '时长' AFTER `course_id`;


ALTER TABLE `wb_class`
  ADD COLUMN `learn_no` varchar(32) NULL COMMENT '学习人数' AFTER `duration`;


CREATE TABLE `wb_course_history` (
  `id` varchar(36) NOT NULL COMMENT '主键',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `sys_org_code` varchar(64) DEFAULT NULL COMMENT '所属部门',
  `course_id` varchar(36) NOT NULL COMMENT '外键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE VIEW `wb_course_user_history`
AS SELECT
   `history`.`id` AS `id`,
   `history`.`create_by` AS `create_by`,
   `history`.`create_time` AS `create_time`,
   `history`.`update_by` AS `update_by`,
   `history`.`update_time` AS `update_time`,
   `history`.`sys_org_code` AS `sys_org_code`,
   `history`.`course_id` AS `course_id`,
   `history`.`is_paid` AS `is_paid`,
   `course`.`title` AS `title`,
   `course`.`image` AS `image`,
   `course`.`description` AS `description`,
   `course`.`category` AS `category`,
   `course`.`category_code` AS `category_code`,
   `course`.`video_url` AS `video_url`,
   `course`.`audio_url` AS `audio_url`,
   `course`.`introduction` AS `introduction`,
   `course`.`publish_date` AS `publish_date`,
   `course`.`teacher_name` AS `teacher_name`,
   `course`.`price` AS `price`,
   `course`.`duration` AS `duration`,
   `course`.`class_num` AS `class_num`,
   `course`.`is_free` AS `is_free`,
   `course`.`is_top` AS `is_top`,
   `course`.`learn_num` AS `learn_num`,
   `course`.`sort_no` AS `sort_no`
FROM (`wb_course_history` `history` join `wb_course` `course`) where (`history`.`course_id` = `course`.`id`);


create view `wb_finance_withdraw` as select finance.`id`,finance.`create_by`,finance.`create_time`,finance.`update_by`,finance.`update_time`,finance.`sys_org_code`,finance.`amount`,finance.`status`,user.`username`,user.`realname`,user.`identity_no`,user.`card_no`
,user.`bank_name`,user.`card_type`,user.`card_phone` from `wb_finance` finance, `sys_user` user
where finance.user_id = user.id and finance.type='1'
