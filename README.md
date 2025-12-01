# Online Shopping CLI Application

## Features

### Database Implementation
- **Product Management**
  - Add new products
  - Remove products
  - Update product information
  - Query products by ID or search terms
  - Stock management

- **Customer Management**
  - Customer ranking system
  - Add new customers (registration)
  - Remove customers
  - Update customer information
  - Password validation and hashing (BCrypt)
  - Customer leaderboard

- **Order Management**
  - Create orders
  - Remove orders
  - Update order status (ON_SHOPPING, DONE, CANCELLED)
  - Order history tracking

### Application Features
- **Order Operations**
  - Place orders
  - Cancel orders
  - Add/remove products from cart
  - View order history

- **Customer Features**
  - Register new customers
  - Update customer passwords
  - Customer authentication (login/logout)
  - View customer leaderboard rankings

- **Product Browsing**
  - Browse all products
  - Search products by name, description, or category
  - View product details and availability

- **Admin Functions**
  - Add new products
  - Update existing products
  - Remove products
  - Manage inventory

## Database Schema

### Products Table

- `product_id` (PRIMARY KEY, AUTO INCREMENT)
- `name` (VARCHAR(255), NOT NULL)
- `description` (TEXT)
- `price` (DECIMAL(10,2), NOT NULL)
- `stock` (INT, NOT NULL, DEFAULT 0)
- `category` (VARCHAR(100))
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

### Customers Table

- `customer_id` (PRIMARY KEY, AUTO INCREMENT)
- `username` (VARCHAR(50), UNIQUE, NOT NULL)
- `password` (VARCHAR(255), NOT NULL)
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

### Orders Table

- `order_id` (PRIMARY KEY, AUTO INCREMENT)
- `customer_id` (FOREIGN KEY → customers.customer_id, ON DELETE CASCADE)
- `order_date` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- `status` (VARCHAR(20), NOT NULL, DEFAULT 'ON_SHOPPING')
- `total_amount` (DECIMAL(10,2), DEFAULT 0.0)

### Order Items Table

- `order_item_id` (PRIMARY KEY, AUTO INCREMENT)
- `order_id` (FOREIGN KEY → orders.order_id, ON DELETE CASCADE)
- `product_id` (FOREIGN KEY → products.product_id, ON DELETE CASCADE)
- `product_name` (VARCHAR(255), NOT NULL)
- `price` (DECIMAL(10,2), NOT NULL)
- `quantity` (INT, NOT NULL)
