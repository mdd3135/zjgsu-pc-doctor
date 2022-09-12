CREATE TABLE
  `user_table` (
    `user_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `user_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `pwd_md5` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `level` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0',
    `session_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `expiration_time` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT 'NULL',
    `contact_details` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `user_description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `user_picture` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`user_id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci

alter table user_table add `sex` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '未知' after `user_name`