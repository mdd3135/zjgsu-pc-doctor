CREATE TABLE
  `appointment_table` (
    `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `user_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `problem_description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `problem_category` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `problem_picture` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `create_time` datetime NOT NULL,
    `appointment_time` datetime DEFAULT NULL,
    `done_time` datetime DEFAULT NULL,
    `appointment_location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `doctor_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `status` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
  ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci