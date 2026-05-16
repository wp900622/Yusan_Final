
CREATE OR REPLACE PROCEDURE sp_insert_product(
    p_id VARCHAR,
    p_name VARCHAR,
    p_price INTEGER,
    p_stock INTEGER
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO products (product_id, product_name, price, stock)
    VALUES (p_id, p_name, p_price, p_stock);
END;
$$;

CREATE OR REPLACE PROCEDURE sp_process_order_item(
    p_order_id VARCHAR,
    p_product_id VARCHAR,
    p_quantity INTEGER
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_stock INTEGER;
    v_price INTEGER;
BEGIN
    SELECT stock, price INTO v_stock, v_price 
    FROM products WHERE product_id = p_product_id FOR UPDATE;

    IF v_stock < p_quantity THEN
        RAISE EXCEPTION '商品 % 庫存不足 (剩餘 %)', p_product_id, v_stock;
    END IF;
    UPDATE products SET stock = stock - p_quantity WHERE product_id = p_product_id;
    INSERT INTO order_details (order_id, product_id, quantity, stand_price, item_price)
    VALUES (p_order_id, p_product_id, p_quantity, v_price, v_price * p_quantity);
END;
$$;