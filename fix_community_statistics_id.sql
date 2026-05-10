-- Fix: Ensure community_statistics.id is AUTO_INCREMENT so INSERT without id works.
-- Run this if you get "Field 'id' doesn't have a default value" when using Rebuild All Stats.
-- If you get error 1075 (only one auto field allowed), use the DROP + CREATE below instead.

USE tripx_db;

-- Option A: Try adding AUTO_INCREMENT to id (if no other column has it)
-- ALTER TABLE community_statistics MODIFY COLUMN id INT(11) NOT NULL AUTO_INCREMENT;

-- Option B: Drop and recreate the table (safe if you only use it for stats display)
DROP TABLE IF EXISTS community_statistics;

CREATE TABLE community_statistics (
  id int(11) NOT NULL AUTO_INCREMENT,
  user_id int(11) NOT NULL,
  posts_count int(11) DEFAULT 0,
  comments_count int(11) DEFAULT 0,
  reactions_count int(11) DEFAULT 0,
  badges_count int(11) DEFAULT 0,
  followers_count int(11) DEFAULT 0,
  other_stats varchar(1000) DEFAULT NULL,
  updated_at datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (id),
  UNIQUE KEY unique_user_stats (user_id),
  CONSTRAINT fk_stats_user FOREIGN KEY (user_id) REFERENCES user (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Seed from current data (optional; app will also do this via Rebuild All Stats)
INSERT INTO community_statistics (user_id, posts_count, comments_count, reactions_count, followers_count, updated_at)
SELECT
    u.user_id,
    (SELECT COUNT(*) FROM posts WHERE user_id = u.user_id),
    (SELECT COUNT(*) FROM comments WHERE user_id = u.user_id),
    (SELECT COUNT(*) FROM reactions WHERE user_id = u.user_id),
    (SELECT COUNT(*) FROM followings WHERE followed_id = u.user_id),
    NOW()
FROM user u
ON DUPLICATE KEY UPDATE
    posts_count     = VALUES(posts_count),
    comments_count  = VALUES(comments_count),
    reactions_count = VALUES(reactions_count),
    followers_count = VALUES(followers_count),
    updated_at      = VALUES(updated_at);
