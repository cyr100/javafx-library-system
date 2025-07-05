-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
  id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `role` varchar(20) NOT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `avatar_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
);

-- 创建图书表
CREATE TABLE IF NOT EXISTS books (
    `id` int NOT NULL AUTO_INCREMENT,
    `title` varchar(100) NOT NULL,
    `author` varchar(100) NOT NULL,
    `isbn` varchar(20) NOT NULL,
    `category` varchar(50) NOT NULL,
    `publish_date` date NOT NULL,
    `available` tinyint(1) NOT NULL DEFAULT '1',
    `image_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `isbn` (`isbn`)
    );

-- 创建借阅记录表
CREATE TABLE IF NOT EXISTS borrow_records (
  `id` int NOT NULL AUTO_INCREMENT,
  `book_id` int NOT NULL,
  `user_id` int NOT NULL,
  `borrow_date` date NOT NULL,
  `return_date` date DEFAULT NULL,
  `returned` tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY `book_id` (`book_id`),
    KEY `user_id` (`user_id`),
    CONSTRAINT `borrow_records_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
    CONSTRAINT `borrow_records_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
    );

-- 插入初始用户数据
INSERT INTO users (username, password, full_name, email, role) VALUES
('admin', 'admin123', '系统管理员', 'admin@library.com', 'ADMIN'),
('user1', 'user123', '普通用户1', 'user1@library.com', 'USER'),
('user2', 'user123', '普通用户2', 'user2@library.com', 'USER');

-- 插入初始图书数据
INSERT INTO books (title, author, isbn, category, publish_date, available) VALUES
('Java编程思想', 'Bruce Eckel', '9787111213826', '编程', '2007-06-01', TRUE),
('Effective Java', 'Joshua Bloch', '9787111556892', '编程', '2018-01-01', TRUE),
('Spring实战', 'Craig Walls', '9787115485891', '框架', '2018-10-01', TRUE),
('设计模式', 'Erich Gamma', '9787111550685', '设计', '2010-09-01', TRUE),
('算法导论', 'Thomas Cormen', '9787115217744', '算法', '2013-01-01', TRUE);

-- 插入初始借阅记录
INSERT INTO borrow_records (book_id, user_id, borrow_date, returned) VALUES
(1, 2, '2023-01-15', FALSE),
(3, 3, '2023-02-10', FALSE);