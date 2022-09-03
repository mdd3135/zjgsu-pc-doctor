CREATE TABLE
  `activity_table` (
    `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `create_time` datetime NOT NULL,
    `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `summary` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `file` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `cover` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci