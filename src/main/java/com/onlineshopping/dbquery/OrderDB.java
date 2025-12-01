package com.onlineshopping.dbquery;

import com.onlineshopping.database.DatabaseManager;
import com.onlineshopping.model.Order;
import com.onlineshopping.model.OrderItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDB {
    private final DatabaseManager dbManager;

    public OrderDB() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean addOrder(Order order) {
        String sql = "INSERT INTO orders (customer_id, order_date, status, total_amount) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, order.getCustomerId());
            pstmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));
            pstmt.setString(3, order.getStatus().name());
            pstmt.setDouble(4, order.getTotalAmount());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the last inserted ID
                try (Statement stmt = dbManager.getConnection().createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()")) {
                    if (rs.next()) {
                        order.setId(rs.getInt(1));
                    }
                }
                
                // Add order items
                for (OrderItem item : order.getItems()) {
                    addOrderItem(order.getId(), item);
                }
                
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding order: " + e.getMessage());
        }
        return false;
    }

    public boolean updateOrderStatus(int orderId, Order.OrderStatus newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, newStatus.name());
            pstmt.setInt(2, orderId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
        }
        return false;
    }

    public boolean updateOrderTotal(int orderId, double newTotal) {
        String sql = "UPDATE orders SET total_amount = ? WHERE order_id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setDouble(1, newTotal);
            pstmt.setInt(2, orderId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order total: " + e.getMessage());
        }
        return false;
    }

    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Order order = new Order(
                    rs.getInt("order_id"),
                    rs.getInt("customer_id"),
                    rs.getTimestamp("order_date").toLocalDateTime(),
                    Order.OrderStatus.valueOf(rs.getString("status")),
                    rs.getDouble("total_amount")
                );
                
                // Load order items
                order.setItems(getOrderItems(orderId));
                
                return order;
            }
        } catch (SQLException e) {
            System.err.println("Error getting order: " + e.getMessage());
        }
        return null;
    }

    public List<Order> getOrdersByCustomerId(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY order_date DESC";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("order_id"),
                    rs.getInt("customer_id"),
                    rs.getTimestamp("order_date").toLocalDateTime(),
                    Order.OrderStatus.valueOf(rs.getString("status")),
                    rs.getDouble("total_amount")
                );
                
                // Load order items
                order.setItems(getOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by customer: " + e.getMessage());
        }
        return orders;
    }

    public Order getActiveOrderByCustomerId(int customerId) {
        String sql = "SELECT * FROM orders WHERE customer_id = ? AND status = 'ON_SHOPPING' ORDER BY order_date DESC LIMIT 1";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Order order = new Order(
                    rs.getInt("order_id"),
                    rs.getInt("customer_id"),
                    rs.getTimestamp("order_date").toLocalDateTime(),
                    Order.OrderStatus.valueOf(rs.getString("status")),
                    rs.getDouble("total_amount")
                );
                
                // Load order items
                order.setItems(getOrderItems(order.getId()));
                
                return order;
            }
        } catch (SQLException e) {
            System.err.println("Error getting active order: " + e.getMessage());
        }
        return null;
    }

    private boolean addOrderItem(int orderId, OrderItem item) {
        String sql = "INSERT INTO order_items (order_id, product_id, product_name, price, quantity) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, item.getProductId());
            pstmt.setString(3, item.getProductName());
            pstmt.setDouble(4, item.getPrice());
            pstmt.setInt(5, item.getQuantity());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the last inserted ID
                try (Statement stmt = dbManager.getConnection().createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()")) {
                    if (rs.next()) {
                        item.setId(rs.getInt(1));
                        item.setOrderId(orderId);
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding order item: " + e.getMessage());
        }
        return false;
    }

    public boolean addItemToOrder(int orderId, OrderItem item) {
        // Check if item already exists in order
        OrderItem existingItem = getOrderItem(orderId, item.getProductId());
        
        if (existingItem != null) {
            // Update quantity
            return updateOrderItemQuantity(existingItem.getId(), existingItem.getQuantity() + item.getQuantity());
        } else {
            // Add new item
            return addOrderItem(orderId, item);
        }
    }

    public boolean removeItemFromOrder(int orderId, int productId) {
        String sql = "DELETE FROM order_items WHERE order_id = ? AND product_id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, productId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing item from order: " + e.getMessage());
        }
        return false;
    }

    private boolean updateOrderItemQuantity(int itemId, int newQuantity) {
        String sql = "UPDATE order_items SET quantity = ? WHERE order_item_id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, itemId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order item quantity: " + e.getMessage());
        }
        return false;
    }

    private OrderItem getOrderItem(int orderId, int productId) {
        String sql = "SELECT * FROM order_items WHERE order_id = ? AND product_id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, productId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new OrderItem(
                    rs.getInt("order_item_id"),
                    rs.getInt("order_id"),
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting order item: " + e.getMessage());
        }
        return null;
    }

    private List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                items.add(new OrderItem(
                    rs.getInt("order_item_id"),
                    rs.getInt("order_id"),
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting order items: " + e.getMessage());
        }
        return items;
    }
}
