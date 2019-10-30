
ALTER TABLE `sys_user`
  ADD COLUMN `union_id` varchar(100) NULL COMMENT 'UNIONID，唯一键' AFTER `realname`,
  ADD UNIQUE INDEX `uniq_sys_user_union_id` (`union_id`);

  ALTER TABLE `sys_user`
  ADD COLUMN `referral_code` varchar(6) NULL COMMENT '推荐键' AFTER `union_id`,
  ADD UNIQUE INDEX `uniq_sys_user_referral_code` (`referral_code`);


ALTER TABLE `sys_user`
  ADD COLUMN `refer_user_id` varchar(32) NULL COMMENT '推荐人id' AFTER `referral_code`;
