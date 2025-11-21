package com.onlineshopping.ui;

import com.onlineshopping.model.*;
import com.onlineshopping.service.ShoppingService;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final ShoppingService shoppingService;
    private final Scanner scanner;
    private boolean running;

    public ConsoleUI() {
        this.shoppingService = new ShoppingService();
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    public void start() {        
        while (running) {
            if (shoppingService.isLoggedIn()) {
                showMainMenu();
            } else {
                showLoginMenu();
            }
        }
        
        scanner.close();
    }

    private void showLoginMenu() {
        System.out.println("\n=== Login Menu ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1" -> handleLogin();
            case "2" -> handleRegister();
            case "3" -> {
                running = false;
                System.out.println("Thank you for using Online Shopping CLI!");
            }
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private void showMainMenu() {
        Customer customer = shoppingService.getCurrentCustomer();
        System.out.printf("\n=== Welcome, %s! ===\n", customer.getUsername());
        System.out.println("1. Browse Products");
        System.out.println("2. Search Products");
        System.out.println("3. View Cart");
        System.out.println("4. View Order History");
        System.out.println("5. Update Password");
        System.out.println("6. Admin Functions");
        System.out.println("7. Logout");
        System.out.print("Choose an option: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1" -> browseProducts();
            case "2" -> searchProducts();
            case "3" -> viewCart();
            case "4" -> viewOrderHistory();
            case "5" -> updatePassword();
            case "6" -> showAdminMenu();
            case "7" -> {
                shoppingService.logout();
                System.out.println("Logged out successfully!");
            }
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        
        if (shoppingService.login(username, password)) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password!");
        }
    }

    private void handleRegister() {
        System.out.println("\n=== Register New Customer ===");
        
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        
        if (shoppingService.registerCustomer(username, password)) {
            System.out.println("Registration successful! You can now login.");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }

    private void browseProducts() {
        System.out.println("\n=== All Products ===");
        List<Product> products = shoppingService.getAllProducts();
        
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }
        
        displayProducts(products);
        
        System.out.print("\nEnter product ID to add to cart (or 0 to go back): ");
        try {
            int productId = Integer.parseInt(scanner.nextLine().trim());
            if (productId > 0) {
                addProductToCart(productId);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid product ID.");
        }
    }

    private void searchProducts() {
        System.out.print("Enter search term: ");
        String searchTerm = scanner.nextLine().trim();
        
        if (searchTerm.isEmpty()) {
            System.out.println("Search term cannot be empty.");
            return;
        }
        
        List<Product> products = shoppingService.searchProducts(searchTerm);
        
        if (products.isEmpty()) {
            System.out.println("No products found matching your search.");
            return;
        }
        
        System.out.println("\n=== Search Results ===");
        displayProducts(products);
        
        System.out.print("\nEnter product ID to add to cart (or 0 to go back): ");
        try {
            int productId = Integer.parseInt(scanner.nextLine().trim());
            if (productId > 0) {
                addProductToCart(productId);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid product ID.");
        }
    }

    private void displayProducts(List<Product> products) {
        System.out.printf("%-5s %-20s %-30s %-10s %-8s %-15s%n", 
                         "ID", "Name", "Description", "Price", "Stock", "Category");
        System.out.println("─".repeat(90));
        
        for (Product product : products) {
            System.out.printf("%-5d %-20s %-30s $%-9.2f %-8d %-15s%n",
                             product.getId(),
                             truncateString(product.getName(), 20),
                             truncateString(product.getDescription(), 30),
                             product.getPrice(),
                             product.getStock(),
                             truncateString(product.getCategory(), 15));
        }
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    private void addProductToCart(int productId) {
        Product product = shoppingService.getProductById(productId);
        if (product == null) {
            System.out.println("Product not found!");
            return;
        }
        
        System.out.printf("Adding '%s' to cart.\n", product.getName());
        System.out.printf("Available stock: %d\n", product.getStock());
        System.out.print("Enter quantity: ");
        
        try {
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            if (quantity <= 0) {
                System.out.println("Quantity must be greater than 0.");
                return;
            }
            
            if (shoppingService.addProductToOrder(productId, quantity)) {
                System.out.println("Product added to cart successfully!");
            } else {
                System.out.println("Failed to add product to cart.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
        }
    }

    private void viewCart() {
        Order currentOrder = shoppingService.getCurrentOrder();
        
        if (currentOrder == null || currentOrder.getItems().isEmpty()) {
            System.out.println("\nYour cart is empty.");
            return;
        }
        
        System.out.println("\n=== Your Cart ===");
        System.out.printf("%-5s %-20s %-10s %-8s %-10s%n", 
                         "ID", "Product", "Price", "Qty", "Subtotal");
        System.out.println("─".repeat(55));
        
        double total = 0;
        for (OrderItem item : currentOrder.getItems()) {
            System.out.printf("%-5d %-20s $%-9.2f %-8d $%-9.2f%n",
                             item.getProductId(),
                             truncateString(item.getProductName(), 20),
                             item.getPrice(),
                             item.getQuantity(),
                             item.getSubtotal());
            total += item.getSubtotal();
        }
        
        System.out.println("─".repeat(55));
        System.out.printf("Total: $%.2f%n", total);
        
        System.out.println("\n1. Place Order");
        System.out.println("2. Remove Item from Cart");
        System.out.println("3. Cancel Order");
        System.out.println("4. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1" -> placeOrder();
            case "2" -> removeItemFromCart();
            case "3" -> cancelOrder();
            case "4" -> {} // Do nothing, go back to main menu
            default -> System.out.println("Invalid option.");
        }
    }

    private void placeOrder() {
        if (shoppingService.placeOrder()) {
            System.out.println("Order placed successfully! Thank you for your purchase.");
        } else {
            System.out.println("Failed to place order.");
        }
    }

    private void removeItemFromCart() {
        System.out.print("Enter product ID to remove: ");
        try {
            int productId = Integer.parseInt(scanner.nextLine().trim());
            if (shoppingService.removeProductFromOrder(productId)) {
                System.out.println("Item removed from cart successfully!");
            } else {
                System.out.println("Failed to remove item from cart.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid product ID.");
        }
    }

    private void cancelOrder() {
        System.out.print("Are you sure you want to cancel your order? (y/n): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if ("y".equals(confirmation) || "yes".equals(confirmation)) {
            if (shoppingService.cancelOrder()) {
                System.out.println("Order cancelled successfully.");
            } else {
                System.out.println("Failed to cancel order.");
            }
        }
    }

    private void viewOrderHistory() {
        List<Order> orders = shoppingService.getCustomerOrderHistory();
        
        if (orders.isEmpty()) {
            System.out.println("\nNo order history found.");
            return;
        }
        
        System.out.println("\n=== Order History ===");
        System.out.printf("%-5s %-20s %-15s %-10s %-8s%n", 
                         "ID", "Date", "Status", "Total", "Items");
        System.out.println("─".repeat(60));
        
        for (Order order : orders) {
            System.out.printf("%-5d %-20s %-15s $%-9.2f %-8d%n",
                             order.getId(),
                             order.getOrderDate().toLocalDate().toString(),
                             order.getStatus(),
                             order.getTotalAmount(),
                             order.getItems().size());
        }
    }

    private void updatePassword() {
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine().trim();
        
        if (shoppingService.updateCustomerPassword(newPassword)) {
            System.out.println("Password updated successfully!");
        } else {
            System.out.println("Failed to update password.");
        }
    }

    private void showAdminMenu() {
        System.out.println("\n=== Admin Functions ===");
        System.out.println("1. Add Product");
        System.out.println("2. Remove Product");
        System.out.println("3. Update Product");
        System.out.println("4. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1" -> addProduct();
            case "2" -> removeProduct();
            case "3" -> updateProduct();
            case "4" -> {} // go back to main menu
            default -> System.out.println("Invalid option.");
        }
    }

    private void addProduct() {
        System.out.println("\n=== Add New Product ===");
        
        System.out.print("Product Name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        
        System.out.print("Price: $");
        double price;
        try {
            price = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid price.");
            return;
        }
        
        System.out.print("Stock: ");
        int stock;
        try {
            stock = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid stock.");
            return;
        }
        
        System.out.print("Category: ");
        String category = scanner.nextLine().trim();
        
        if (shoppingService.addProduct(name, description, price, stock, category)) {
            System.out.println("Product added successfully!");
        } else {
            System.out.println("Failed to add product.");
        }
    }

    private void removeProduct() {
        System.out.print("Enter product ID to remove: ");
        try {
            int productId = Integer.parseInt(scanner.nextLine().trim());
            
            Product product = shoppingService.getProductById(productId);
            if (product == null) {
                System.out.println("Product not found!");
                return;
            }
            
            System.out.printf("Are you sure you want to remove '%s'? (y/n): ", product.getName());
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if ("y".equals(confirmation) || "yes".equals(confirmation)) {
                if (shoppingService.removeProduct(productId)) {
                    System.out.println("Product removed successfully!");
                } else {
                    System.out.println("Failed to remove product.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid product ID.");
        }
    }

    private void updateProduct() {
        System.out.print("Enter product ID to update: ");
        try {
            int productId = Integer.parseInt(scanner.nextLine().trim());
            
            Product product = shoppingService.getProductById(productId);
            if (product == null) {
                System.out.println("Product not found!");
                return;
            }
            
            System.out.println("\n=== Update Product ===");
            System.out.printf("Current Name: %s%n", product.getName());
            System.out.print("New Name (or press Enter to keep current): ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) name = product.getName();
            
            System.out.printf("Current Description: %s%n", product.getDescription());
            System.out.print("New Description (or press Enter to keep current): ");
            String description = scanner.nextLine().trim();
            if (description.isEmpty()) description = product.getDescription();
            
            System.out.printf("Current Price: $%.2f%n", product.getPrice());
            System.out.print("New Price (or press Enter to keep current): $");
            String priceStr = scanner.nextLine().trim();
            double price = product.getPrice();
            if (!priceStr.isEmpty()) {
                try {
                    price = Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price, keeping current value.");
                }
            }
            
            System.out.printf("Current Stock: %d%n", product.getStock());
            System.out.print("New Stock (or press Enter to keep current): ");
            String stockStr = scanner.nextLine().trim();
            int stock = product.getStock();
            if (!stockStr.isEmpty()) {
                try {
                    stock = Integer.parseInt(stockStr);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid stock, keeping current value.");
                }
            }
            
            System.out.printf("Current Category: %s%n", product.getCategory());
            System.out.print("New Category (or press Enter to keep current): ");
            String category = scanner.nextLine().trim();
            if (category.isEmpty()) category = product.getCategory();
            
            if (shoppingService.updateProduct(productId, name, description, price, stock, category)) {
                System.out.println("Product updated successfully!");
            } else {
                System.out.println("Failed to update product.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid product ID.");
        }
    }
}
