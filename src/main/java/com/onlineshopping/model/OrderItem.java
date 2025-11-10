package com.onlineshopping.model;

public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private String productName;
    private double price;
    private int quantity;

    public OrderItem() {}

    public OrderItem(int productId, String productName, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public OrderItem(int id, int orderId, int productId, String productName, double price, int quantity) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getSubtotal() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return String.format("OrderItem{id=%d, orderId=%d, productId=%d, productName='%s', price=%.2f, quantity=%d, subtotal=%.2f}",
                id, orderId, productId, productName, price, quantity, getSubtotal());
    }
}
