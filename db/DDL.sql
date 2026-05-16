CREATE TABLE products (
    product_id VARCHAR(50) PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL
);


CREATE TABLE orders (
    order_id VARCHAR(50) PRIMARY KEY, -- 如 Ms20250801...
    member_id VARCHAR(50) NOT NULL,
    total_price INT NOT NULL,
    pay_status INT DEFAULT 0 -- 0 未付款, 1 已付款
);

CREATE TABLE order_details (
    order_item_sn INT GENERATED ALWAYS AS IDENTITY  PRIMARY KEY,
    order_id VARCHAR(50),
    product_id VARCHAR(50),
    quantity INT,
    stand_price INT, -- 購買當下的單價 (避免商品後續調價影響舊訂單)
    item_price INT,  -- 小計 (quantity * stand_price)
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE users (
    user_id VARCHAR(50) PRIMARY KEY,        -- 自動遞增的內部 ID
    username VARCHAR(50) UNIQUE NOT NULL, -- 登入帳號
    password VARCHAR(255) NOT NULL,    -- 加密後的密碼 (BCrypt)
    real_name VARCHAR(100),            -- 真實姓名
    email VARCHAR(100),
    role VARCHAR(20) DEFAULT 'USER',   -- 角色：ADMIN (管理員), USER (顧客)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 修改 orders 表以關聯 users
ALTER TABLE orders 
ADD CONSTRAINT fk_order_user 
FOREIGN KEY (member_id) REFERENCES users(user_id);

INSERT INTO users (user_id, username, password) -- 欄位名稱請根據你的 user 表調整
VALUES ('USER_001', '測試帳號','123');
