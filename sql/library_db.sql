-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- 创建图书表
CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    publish_date DATE NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    image_path VARCHAR(255)
);

-- 创建借阅记录表
CREATE TABLE IF NOT EXISTS borrow_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    user_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    return_date DATE,
    returned BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
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