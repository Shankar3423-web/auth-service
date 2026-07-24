
CREATE TABLE `client` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `client_id` VARCHAR(255) NOT NULL,
  `client_name` VARCHAR(255) DEFAULT NULL,
  `client_secret` VARCHAR(255) NOT NULL,
  `created_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_client_id` (`client_id`)
);

CREATE TABLE `auth_session` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `access_token` VARCHAR(4096) NOT NULL,
  `access_token_expiry` BIGINT NOT NULL,
  `access_tokenuuid` VARCHAR(255) NOT NULL,
  `client_id` VARCHAR(255) NOT NULL,
  `refresh_token` VARCHAR(4096) NOT NULL,
  `refresh_token_expiry` BIGINT NOT NULL,
  `refresh_tokenuuid` VARCHAR(255) NOT NULL,
  `is_active` BIT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_auth_session_client_id` (`client_id`)
);

CREATE TABLE `auth_session_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `access_token` VARCHAR(4096) NOT NULL,
  `access_token_expiry` BIGINT NOT NULL,
  `access_tokenuuid` VARCHAR(255) NOT NULL,
  `client_id` VARCHAR(255) NOT NULL,
  `refresh_token` VARCHAR(4096) NOT NULL,
  `refresh_token_expiry` BIGINT NOT NULL,
  `refresh_tokenuuid` VARCHAR(255) NOT NULL,
  `created_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_auth_session_access_tokenuuid` (`access_tokenuuid`)
);

CREATE TABLE `resources` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) DEFAULT NULL,
  `path` VARCHAR(255) NOT NULL,
  `service` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_resource_path_service` (`path`, `service`),
  KEY `IDX_resource_path` (`path`)
);

CREATE TABLE `roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `is_active` BIT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_role_name` (`name`)
);

CREATE TABLE `resource_roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT NOT NULL,
  `resource_id` BIGINT NOT NULL,
  `is_active` BIT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `IDX_resource_roles_role_id` (`role_id`),
  KEY `IDX_resource_roles_resource_id` (`resource_id`)
);

CREATE TABLE `client_roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `client_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  `is_active` BIT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `IDX_client_roles_client_id` (`client_id`),
  KEY `IDX_client_roles_role_id` (`role_id`)
);





