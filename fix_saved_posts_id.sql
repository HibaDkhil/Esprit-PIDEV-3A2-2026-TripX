-- Fix: Ensure saved_posts.id is AUTO_INCREMENT so INSERT without id works.
-- Run this once if you get "Field 'id' doesn't have a default value" when saving a post.

USE tripx_db;

-- Ensure id column is auto-increment (safe to run even if already correct)
ALTER TABLE saved_posts
  MODIFY COLUMN id INT(11) NOT NULL AUTO_INCREMENT;
