package com.onlineshopping.service;

import com.onlineshopping.dbquery.CustomerDB;
import com.onlineshopping.dbquery.OrderDB;
import com.onlineshopping.dbquery.ProductDB;
import com.onlineshopping.model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class ShoppingService {
    private final CustomerDB customerDB;
    private final ProductDB productDB;
    private final OrderDB orderDB;
    private Customer currentCustomer;

    public ShoppingService() {
        this.customerDB = new CustomerDB();
        this.productDB = new ProductDB();
        this.orderDB = new OrderDB();
    }

    // Authentication
    public boolean login(String username, String password) {
        if (customerDB.validatePassword(username, password)) {
            currentCustomer = customerDB.getCustomerByUsername(username);
            return true;
        }
        return false;
    }

    public void logout() {
        currentCustomer = null;
    }

    public boolean isLoggedIn() {
        return currentCustomer != null;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    // Customer Management
    public boolean registerCustomer(String username, String email, String password, String fullName, String address) {
        if (customerDB.usernameExists(username)) {
            System.out.println("Username already exists!");
            return false;
        }

        if (customerDB.emailExists(email)) {
            System.out.println("Email already exists!");
            return false;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Customer customer = new Customer(username, email, hashedPassword, fullName, address);

        return customerDB.addCustomer(customer);
    }

    public boolean updateCustomerPassword(String newPassword) {
        if (currentCustomer == null) {
            return false;
        }

        return customerDB.updatePassword(currentCustomer.getId(), newPassword);
    }

    public List<Customer> getCustomerLeaderboard() {
        return customerDB.getCustomerLeaderboard();
    }

    // Product Management
    public List<Product> getAllProducts() {
        return productDB.getAllProducts();
    }

    public List<Product> searchProducts(String searchTerm) {
        return productDB.searchProducts(searchTerm);
    }

    public Product getProductById(int productId) {
        return productDB.getProductById(productId);
    }

    // Order Management
    public Order createNewOrder() {
        if (currentCustomer == null) {
            return null;
        }
        
        // Check if there's already an active order
        Order activeOrder = orderDB.getActiveOrderByCustomerId(currentCustomer.getId());
        if (activeOrder != null) {
            return activeOrder;
        }
        
        Order order = new Order(currentCustomer.getId());
        if (orderDB.addOrder(order)) {
            return order;
        }
        return null;
    }

    public Order getCurrentOrder() {
        if (currentCustomer == null) {
            return null;
        }

        return orderDB.getActiveOrderByCustomerId(currentCustomer.getId());
    }

    public boolean addProductToOrder(int productId, int quantity) {
        if (currentCustomer == null) {
            return false;
        }

        Product product = productDB.getProductById(productId);
        if (product == null) {
            System.out.println("Product not found!");
            return false;
        }
        
        if (product.getStock() < quantity) {
            System.out.println("Insufficient stock! Available: " + product.getStock());
            return false;
        }
        
        Order activeOrder = getCurrentOrder();
        if (activeOrder == null) {
            activeOrder = createNewOrder();
            if (activeOrder == null) {
                return false;
            }
        }
        
        OrderItem item = new OrderItem(productId, product.getName(), product.getPrice(), quantity);
        
        if (orderDB.addItemToOrder(activeOrder.getId(), item)) {
            // Update product stock
            productDB.updateStock(productId, product.getStock() - quantity);
            
            // Recalculate order total
            Order updatedOrder = orderDB.getOrderById(activeOrder.getId());
            double newTotal = updatedOrder.getItems().stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
            orderDB.updateOrderTotal(activeOrder.getId(), newTotal);

            return true;
        }
        
        return false;
    }

    public boolean removeProductFromOrder(int productId) {
        if (currentCustomer == null) {
            return false;
        }
        
        Order activeOrder = getCurrentOrder();
        if (activeOrder == null) {
            return false;
        }
        
        // Find the item to get quantity for stock restoration
        OrderItem itemToRemove = activeOrder.getItems().stream()
            .filter(item -> item.getProductId() == productId)
            .findFirst()
            .orElse(null);
        
        if (itemToRemove == null) {
            return false;
        }
        
        if (orderDB.removeItemFromOrder(activeOrder.getId(), productId)) {
            // Restore product stock
            Product product = productDB.getProductById(productId);
            if (product != null) {
                productDB.updateStock(productId, product.getStock() + itemToRemove.getQuantity());
            }
            
            // Recalculate order total
            Order updatedOrder = orderDB.getOrderById(activeOrder.getId());
            double newTotal = updatedOrder.getItems().stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
            orderDB.updateOrderTotal(activeOrder.getId(), newTotal);

            return true;
        }
        
        return false;
    }

    public boolean placeOrder() {
        if (currentCustomer == null) {
            return false;
        }
        
        Order activeOrder = getCurrentOrder();
        if (activeOrder == null || activeOrder.getItems().isEmpty()) {
            System.out.println("No items in cart!");
            return false;
        }
        
        if (orderDB.updateOrderStatus(activeOrder.getId(), Order.OrderStatus.DONE)) {
            // Update customer ranking (increase by 1 for each completed order)
            int newRanking = currentCustomer.getRanking() + 1;
            customerDB.updateRanking(currentCustomer.getId(), newRanking);
            currentCustomer.setRanking(newRanking);
            
            return true;
        }
        
        return false;
    }

    public boolean cancelOrder() {
        if (currentCustomer == null) {
            return false;
        }
        
        Order activeOrder = getCurrentOrder();
        if (activeOrder == null) {
            return false;
        }
        
        // Restore stock for all items
        for (OrderItem item : activeOrder.getItems()) {
            Product product = productDB.getProductById(item.getProductId());
            if (product != null) {
                productDB.updateStock(item.getProductId(), product.getStock() + item.getQuantity());
            }
        }
        
        return orderDB.updateOrderStatus(activeOrder.getId(), Order.OrderStatus.CANCELLED);
    }

    public List<Order> getCustomerOrderHistory() {
        if (currentCustomer == null) {
            return List.of();
        }
        
        return orderDB.getOrdersByCustomerId(currentCustomer.getId());
    }

    // Admin functions (for demonstration purposes)
    public boolean addProduct(String name, String description, double price, int stock, String category) {
        Product product = new Product(name, description, price, stock, category);
        return productDB.addProduct(product);
    }

    public boolean removeProduct(int productId) {
        return productDB.removeProduct(productId);
    }

    public boolean updateProduct(int productId, String name, String description, double price, int stock, String category) {
        Product product = productDB.getProductById(productId);
        if (product == null) {
            return false;
        }
        
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);

        return productDB.updateProduct(product);
    }
}
