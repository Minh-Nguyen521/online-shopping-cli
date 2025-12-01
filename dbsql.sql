CREATE DATABASE IF NOT EXISTS online_shopping;
USE online_shopping;

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ON_SHOPPING',
    total_amount DECIMAL(10, 2) DEFAULT 0.0,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- Create order_items table
CREATE TABLE IF NOT EXISTS order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

INSERT INTO products (name, description, price, stock, category) VALUES
('iPhone 15 Pro', 'Latest Apple iPhone with A17 Pro chip', 999.99, 50, 'Electronics'),
('Samsung Galaxy S24', 'Premium Android smartphone with AI features', 849.99, 30, 'Electronics'),
('MacBook Air M3', 'Lightweight laptop with M3 chip', 1299.99, 25, 'Electronics'),
('iPad Pro', 'Professional tablet for creativity and productivity', 799.99, 40, 'Electronics'),
('Sony WH-1000XM5', 'Premium noise-canceling headphones', 349.99, 75, 'Electronics'),

('Nike Air Max 270', 'Comfortable running shoes', 129.99, 100, 'Clothing'),
('Levi''s 501 Jeans', 'Classic denim jeans', 79.99, 80, 'Clothing'),
('North Face Jacket', 'Waterproof outdoor jacket', 199.99, 45, 'Clothing'),
('Adidas Hoodie', 'Comfortable cotton hoodie', 59.99, 60, 'Clothing'),
('Ray-Ban Sunglasses', 'Classic aviator sunglasses', 149.99, 35, 'Clothing'),

('Clean Code', 'A Handbook of Agile Software Craftsmanship', 39.99, 200, 'Books'),
('Design Patterns', 'Elements of Reusable Object-Oriented Software', 44.99, 150, 'Books'),
('Effective Java', 'Best practices for Java programming', 42.99, 180, 'Books'),
('The Pragmatic Programmer', 'From journeyman to master', 41.99, 170, 'Books'),
('Spring in Action', 'Comprehensive guide to Spring Framework', 45.99, 140, 'Books'),

('Dyson V15 Vacuum', 'Powerful cordless vacuum cleaner', 649.99, 20, 'Home & Garden'),
('Instant Pot Duo', '7-in-1 electric pressure cooker', 89.99, 55, 'Home & Garden'),
('Plant Grow Light', 'LED light for indoor plants', 29.99, 90, 'Home & Garden'),
('Coffee Maker', 'Programmable drip coffee maker', 79.99, 40, 'Home & Garden'),
('Air Purifier', 'HEPA filter air purifier', 199.99, 30, 'Home & Garden'),

('Yoga Mat', 'Non-slip exercise yoga mat', 24.99, 120, 'Sports & Outdoors'),
('Dumbbells Set', 'Adjustable weight dumbbells', 149.99, 35, 'Sports & Outdoors'),
('Camping Tent', '4-person waterproof camping tent', 299.99, 25, 'Sports & Outdoors'),
('Mountain Bike', '21-speed mountain bicycle', 599.99, 15, 'Sports & Outdoors'),
('Water Bottle', 'Insulated stainless steel bottle', 19.99, 200, 'Sports & Outdoors');

DELIMITER $$

CREATE PROCEDURE add_product(
    IN p_name        VARCHAR(255),
    IN p_description TEXT,
    IN p_price       DECIMAL(10,2),
    IN p_stock       INT,
    IN p_category    VARCHAR(255),
    OUT p_product_id INT
)
BEGIN
    INSERT INTO products (name, description, price, stock, category)
    VALUES (p_name, p_description, p_price, p_stock, p_category);

    SELECT LAST_INSERT_ID() INTO p_product_id;
END $$


CREATE PROCEDURE remove_product (
    IN p_product_id INT
)
BEGIN
    DELETE FROM products
    WHERE product_id = p_product_id;
END $$


CREATE PROCEDURE update_product (
    IN p_product_id  INT,
    IN p_name        VARCHAR(255),
    IN p_description TEXT,
    IN p_price       DECIMAL(10,2),
    IN p_stock       INT,
    IN p_category    VARCHAR(255)
)
BEGIN
    UPDATE products
    SET name        = p_name,
        description = p_description,
        price       = p_price,
        stock       = p_stock,
        category    = p_category
    WHERE product_id = p_product_id;
END $$


CREATE PROCEDURE get_product_by_id (
    IN p_product_id INT
)
BEGIN
    SELECT product_id, name, description, price, stock, category
    FROM products
    WHERE product_id = p_product_id;
END $$


CREATE PROCEDURE get_all_products ()
BEGIN
    SELECT product_id, name, description, price, stock, category
    FROM products
    ORDER BY name;
END $$


CREATE PROCEDURE search_products (
    IN p_search_term VARCHAR(255)
)
BEGIN
    DECLARE v_pattern VARCHAR(261);
    SET v_pattern = CONCAT('%', p_search_term, '%');

    SELECT product_id, name, description, price, stock, category
    FROM products
    WHERE name        LIKE v_pattern
       OR description LIKE v_pattern
       OR category    LIKE v_pattern
    ORDER BY name;
END $$


CREATE PROCEDURE update_stock (
    IN p_product_id INT,
    IN p_new_stock  INT
)
BEGIN
    UPDATE products
    SET stock = p_new_stock
    WHERE product_id = p_product_id;
END $$


CREATE PROCEDURE add_customer (
    IN  p_username VARCHAR(50),
    IN  p_password VARCHAR(255),
    OUT p_customer_id INT
)
BEGIN
    INSERT INTO customers (username, password)
    VALUES (p_username, p_password);

    SELECT LAST_INSERT_ID() INTO p_customer_id;
END $$


CREATE PROCEDURE update_customer (
    IN p_customer_id INT,
    IN p_username    VARCHAR(50)
)
BEGIN
    UPDATE customers
    SET username = p_username
    WHERE customer_id = p_customer_id;
END $$


CREATE PROCEDURE update_password (
    IN p_customer_id INT,
    IN p_new_password VARCHAR(255)
)
BEGIN
    UPDATE customers
    SET password = p_new_password
    WHERE customer_id = p_customer_id;
END $$


CREATE PROCEDURE get_customer_by_username (
    IN p_username VARCHAR(50)
)
BEGIN
    SELECT customer_id, username, password
    FROM customers
    WHERE username = p_username;
END $$


CREATE PROCEDURE customer_exists (
    IN p_username VARCHAR(50)
)
BEGIN
    SELECT CASE
        WHEN EXISTS (SELECT 1 FROM customers WHERE username = p_username)
        THEN 1
        ELSE 0
    END AS exists_flag;
END $$

CREATE PROCEDURE add_order (
    IN  p_customer_id INT,
    IN  p_order_date  DATETIME,
    IN  p_status      VARCHAR(20),
    IN  p_total_amount DECIMAL(10,2),
    OUT p_order_id    INT
)
BEGIN
    INSERT INTO orders (customer_id, order_date, status, total_amount)
    VALUES (p_customer_id,
            COALESCE(p_order_date, NOW()),
            p_status,
            p_total_amount);

    SET p_order_id = LAST_INSERT_ID();
END $$


CREATE PROCEDURE update_order_status (
    IN p_order_id INT,
    IN p_status   VARCHAR(20)
)
BEGIN
    UPDATE orders
    SET status = p_status
    WHERE order_id = p_order_id;
END $$


CREATE PROCEDURE update_order_total (
    IN p_order_id   INT,
    IN p_total_amt  DECIMAL(10,2)
)
BEGIN
    UPDATE orders
    SET total_amount = p_total_amt
    WHERE order_id = p_order_id;
END $$


CREATE PROCEDURE get_order_by_id (
    IN p_order_id INT
)
BEGIN
    SELECT order_id,
           customer_id,
           order_date,
           status,
           total_amount
    FROM orders
    WHERE order_id = p_order_id;
END $$


CREATE PROCEDURE get_orders_by_customer_id (
    IN p_customer_id INT
)
BEGIN
    SELECT order_id,
           customer_id,
           order_date,
           status,
           total_amount
    FROM orders
    WHERE customer_id = p_customer_id
    ORDER BY order_date DESC;
END $$


CREATE PROCEDURE get_active_order_by_customer_id (
    IN p_customer_id INT
)
BEGIN
    SELECT order_id,
           customer_id,
           order_date,
           status,
           total_amount
    FROM orders
    WHERE customer_id = p_customer_id
      AND status = 'ON_SHOPPING'
    ORDER BY order_date DESC
    LIMIT 1;
END $$


CREATE PROCEDURE add_order_item (
    IN  p_order_id      INT,
    IN  p_product_id    INT,
    IN  p_product_name  VARCHAR(255),
    IN  p_price         DECIMAL(10,2),
    IN  p_quantity      INT,
    OUT p_order_item_id INT
)
BEGIN
    INSERT INTO order_items (order_id, product_id, product_name, price, quantity)
    VALUES (p_order_id, p_product_id, p_product_name, p_price, p_quantity);

    SET p_order_item_id = LAST_INSERT_ID();
END $$


CREATE PROCEDURE get_order_item (
    IN p_order_id   INT,
    IN p_product_id INT
)
BEGIN
    SELECT order_item_id,
           order_id,
           product_id,
           product_name,
           price,
           quantity
    FROM order_items
    WHERE order_id = p_order_id
      AND product_id = p_product_id;
END $$


CREATE PROCEDURE get_order_items (
    IN p_order_id INT
)
BEGIN
    SELECT order_item_id,
           order_id,
           product_id,
           product_name,
           price,
           quantity
    FROM order_items
    WHERE order_id = p_order_id;
END $$


CREATE PROCEDURE update_order_item_quantity (
    IN p_order_item_id INT,
    IN p_quantity      INT
)
BEGIN
    UPDATE order_items
    SET quantity = p_quantity
    WHERE order_item_id = p_order_item_id;
END $$


CREATE PROCEDURE remove_item_from_order (
    IN p_order_id   INT,
    IN p_product_id INT
)
BEGIN
    DELETE FROM order_items
    WHERE order_id = p_order_id
      AND product_id = p_product_id;
END $$


CREATE PROCEDURE add_item_to_order (
    IN  p_order_id      INT,
    IN  p_product_id    INT,
    IN  p_product_name  VARCHAR(255),
    IN  p_price         DECIMAL(10,2),
    IN  p_quantity      INT,
    OUT p_order_item_id INT
)
BEGIN
    DECLARE v_item_id      INT;
    DECLARE v_old_quantity INT;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET v_item_id = NULL;

    -- Check if item already exists
    SELECT order_item_id, quantity
      INTO v_item_id, v_old_quantity
    FROM order_items
    WHERE order_id = p_order_id
      AND product_id = p_product_id
    LIMIT 1;

    IF v_item_id IS NULL THEN
        -- Insert new item
        INSERT INTO order_items (order_id, product_id, product_name, price, quantity)
        VALUES (p_order_id, p_product_id, p_product_name, p_price, p_quantity);

        SET p_order_item_id = LAST_INSERT_ID();
    ELSE
        -- Update quantity
        UPDATE order_items
        SET quantity = v_old_quantity + p_quantity
        WHERE order_item_id = v_item_id;

        SET p_order_item_id = v_item_id;
    END IF;
END $$

DELIMITER ;
