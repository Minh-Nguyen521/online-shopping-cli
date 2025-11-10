package com.onlineshopping.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Order {
    public enum OrderStatus {
        ON_SHOPPING, DONE, CANCELLED
    }

    private int id;
    private int customerId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private double totalAmount;
    private List<OrderItem> items;

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(int customerId) {
        this.customerId = customerId;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.ON_SHOPPING;
        this.totalAmount = 0.0;
        this.items = new ArrayList<>();
    }

    public Order(int id, int customerId, LocalDateTime orderDate, OrderStatus status, double totalAmount) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.items = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public void addItem(OrderItem item) {
        this.items.add(item);
        calculateTotal();
    }

    public void removeItem(int productId) {
        this.items.removeIf(item -> item.getProductId() == productId);
        calculateTotal();
    }

    private void calculateTotal() {
        this.totalAmount = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    @Override
    public String toString() {
        return String.format("Order{id=%d, customerId=%d, orderDate=%s, status=%s, totalAmount=%.2f, items=%d}",
                id, customerId, orderDate, status, totalAmount, items.size());
    }
}
