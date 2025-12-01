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
        String sql = "{CALL add_order(?, ?, ?, ?, ?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, order.getCustomerId());
            cstmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));
            cstmt.setString(3, order.getStatus().name());
            cstmt.setDouble(4, order.getTotalAmount());
            cstmt.registerOutParameter(5, Types.INTEGER);
            
            cstmt.execute();
            
            int orderId = cstmt.getInt(5);
            order.setId(orderId);
            
            // Add order items
            for (OrderItem item : order.getItems()) {
                addOrderItem(orderId, item);
            }
            
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding order: " + e.getMessage());
        }
        return false;
    }

    public boolean updateOrderStatus(int orderId, Order.OrderStatus newStatus) {
        String sql = "{CALL update_order_status(?, ?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, orderId);
            cstmt.setString(2, newStatus.name());
            
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
        }
        return false;
    }

    public boolean updateOrderTotal(int orderId, double newTotal) {
        String sql = "{CALL update_order_total(?, ?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, orderId);
            cstmt.setDouble(2, newTotal);
            
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating order total: " + e.getMessage());
        }
        return false;
    }

    public Order getOrderById(int orderId) {
        String sql = "{CALL get_order_by_id(?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, orderId);
            ResultSet rs = cstmt.executeQuery();
            
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
        String sql = "{CALL get_orders_by_customer_id(?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, customerId);
            ResultSet rs = cstmt.executeQuery();
            
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
        String sql = "{CALL get_active_order_by_customer_id(?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, customerId);
            ResultSet rs = cstmt.executeQuery();
            
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
        String sql = "{CALL add_order_item(?, ?, ?, ?, ?, ?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, orderId);
            cstmt.setInt(2, item.getProductId());
            cstmt.setString(3, item.getProductName());
            cstmt.setDouble(4, item.getPrice());
            cstmt.setInt(5, item.getQuantity());
            cstmt.registerOutParameter(6, Types.INTEGER);
            
            cstmt.execute();
            
            int itemId = cstmt.getInt(6);
            item.setId(itemId);
            item.setOrderId(orderId);
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding order item: " + e.getMessage());
        }
        return false;
    }

    public boolean addItemToOrder(int orderId, OrderItem item) {
        String sql = "{CALL add_item_to_order(?, ?, ?, ?, ?, ?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, orderId);
            cstmt.setInt(2, item.getProductId());
            cstmt.setString(3, item.getProductName());
            cstmt.setDouble(4, item.getPrice());
            cstmt.setInt(5, item.getQuantity());
            cstmt.registerOutParameter(6, Types.INTEGER);
            
            cstmt.execute();
            
            int itemId = cstmt.getInt(6);
            item.setId(itemId);
            item.setOrderId(orderId);
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding item to order: " + e.getMessage());
        }
        return false;
    }

    public boolean removeItemFromOrder(int orderId, int productId) {
        String sql = "{CALL remove_item_from_order(?, ?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, orderId);
            cstmt.setInt(2, productId);
            
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error removing item from order: " + e.getMessage());
        }
        return false;
    }

    private boolean updateOrderItemQuantity(int itemId, int newQuantity) {
        String sql = "{CALL update_order_item_quantity(?, ?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, itemId);
            cstmt.setInt(2, newQuantity);
            
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating order item quantity: " + e.getMessage());
        }
        return false;
    }

    private OrderItem getOrderItem(int orderId, int productId) {
        String sql = "{CALL get_order_item(?, ?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, orderId);
            cstmt.setInt(2, productId);
            ResultSet rs = cstmt.executeQuery();
            
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
        String sql = "{CALL get_order_items(?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, orderId);
            ResultSet rs = cstmt.executeQuery();
            
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
