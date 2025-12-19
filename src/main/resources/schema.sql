-- 用户表
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    role VARCHAR(20) COMMENT '角色',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 订单表
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    total_amount DECIMAL(10, 2) COMMENT '订单金额',
    status INT DEFAULT 0 COMMENT '状态: 0-待付款, 1-已支付, 2-已发货, 3-已完成, 4-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间'
);

-- 分类表
DROP TABLE IF EXISTS categories;
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID (0表示一级分类)',
    sort INT DEFAULT 0 COMMENT '排序权重',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用'
);

-- 品牌表
DROP TABLE IF EXISTS brands;
CREATE TABLE brands (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '品牌名称',
    logo VARCHAR(255) COMMENT '品牌Logo',
    description TEXT COMMENT '品牌描述'
);

-- 商品表
DROP TABLE IF EXISTS products;
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    price DECIMAL(10, 2) NOT NULL COMMENT '销售价格',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存',
    warning_threshold INT DEFAULT 10 COMMENT '库存预警阈值',
    cover_image VARCHAR(255) COMMENT '商品封面图',
    description TEXT COMMENT '商品详情',
    status TINYINT DEFAULT 0 COMMENT '状态：0-下架，1-上架',
    category_id BIGINT COMMENT '关联分类ID',
    brand_id BIGINT COMMENT '关联品牌ID',
    specs TEXT COMMENT '规格参数(JSON格式存储)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
