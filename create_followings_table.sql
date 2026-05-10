-- Run this in your tripx_db database if "Follow" shows "Could not update follow."
-- Creates the followings table required by the blog module.
-- Ensure the user table exists with column user_id (e.g. in tripx_db).

USE tripx_db;

CREATE TABLE IF NOT EXISTS `followings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `follower_id` int(11) NOT NULL,
  `followed_id` int(11) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_follow` (`follower_id`, `followed_id`),
  KEY `idx_follower` (`follower_id`),
  KEY `idx_followed` (`followed_id`),
  CONSTRAINT `fk_follower` FOREIGN KEY (`follower_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_followed` FOREIGN KEY (`followed_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
