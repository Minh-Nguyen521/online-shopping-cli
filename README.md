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
- `id` (PRIMARY KEY, AUTO INCREMENT)
- `name` (NOT NULL)
- `description`
- `price` (NOT NULL)
- `stock` (NOT NULL, DEFAULT 0)
- `category`
- `created_at` (TIMESTAMP)

### Customers Table
- `id` (PRIMARY KEY, AUTO INCREMENT)
- `username` (UNIQUE, NOT NULL)
- `password` (NOT NULL)
- `created_at` (TIMESTAMP)

### Orders Table
- `id` (PRIMARY KEY, AUTO INCREMENT)
- `customer_id` (FOREIGN KEY)
- `order_date` (TIMESTAMP)
- `status` (NOT NULL, DEFAULT 'ON_SHOPPING')
- `total_amount` (DEFAULT 0.0)

### Order Items Table
- `id` (PRIMARY KEY, AUTO INCREMENT)
- `order_id` (FOREIGN KEY)
- `product_id` (FOREIGN KEY)
- `product_name` (NOT NULL)
- `price` (NOT NULL)
- `quantity` (NOT NULL)
