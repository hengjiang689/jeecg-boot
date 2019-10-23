
ALTER TABLE `sys_user`
  ADD COLUMN `union_id` varchar(100) NULL COMMENT 'UNIONID，唯一键' AFTER `realname`,
  ADD UNIQUE INDEX `uniq_sys_user_union_id` (`union_id`);
