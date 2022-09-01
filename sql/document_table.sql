CREATE TABLE
  `document_table` (
    `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `create_time` datetime NOT NULL,
    `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `summary` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `file` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci