package com.onlineshopping.util;

import com.onlineshopping.service.ShoppingService;
import com.onlineshopping.database.DatabaseManager;

public class SampleDataInitializer {
    private final ShoppingService shoppingService;
    private final DatabaseManager databaseManager;

    public SampleDataInitializer() {
        this.shoppingService = new ShoppingService();
        this.databaseManager = DatabaseManager.getInstance();
    }

    public void initializeSampleData() {
        System.out.println("Initializing sample data...");
        databaseManager.initializeTables();

        // Add sample products
        addSampleProducts();
        
        System.out.println("Sample data initialization completed!");
    }

    private void addSampleProducts() {
        // Electronics
        shoppingService.addProduct("iPhone 15 Pro", "Latest Apple iPhone with A17 Pro chip", 999.99, 50, "Electronics");
        shoppingService.addProduct("Samsung Galaxy S24", "Premium Android smartphone with AI features", 849.99, 30, "Electronics");
        shoppingService.addProduct("MacBook Air M3", "Lightweight laptop with M3 chip", 1299.99, 25, "Electronics");
        shoppingService.addProduct("iPad Pro", "Professional tablet for creativity and productivity", 799.99, 40, "Electronics");
        shoppingService.addProduct("Sony WH-1000XM5", "Premium noise-canceling headphones", 349.99, 75, "Electronics");
        
        // Clothing
        shoppingService.addProduct("Nike Air Max 270", "Comfortable running shoes", 129.99, 100, "Clothing");
        shoppingService.addProduct("Levi's 501 Jeans", "Classic denim jeans", 79.99, 80, "Clothing");
        shoppingService.addProduct("North Face Jacket", "Waterproof outdoor jacket", 199.99, 45, "Clothing");
        shoppingService.addProduct("Adidas Hoodie", "Comfortable cotton hoodie", 59.99, 60, "Clothing");
        shoppingService.addProduct("Ray-Ban Sunglasses", "Classic aviator sunglasses", 149.99, 35, "Clothing");
        
        // Books
        shoppingService.addProduct("Clean Code", "A Handbook of Agile Software Craftsmanship", 39.99, 200, "Books");
        shoppingService.addProduct("Design Patterns", "Elements of Reusable Object-Oriented Software", 44.99, 150, "Books");
        shoppingService.addProduct("Effective Java", "Best practices for Java programming", 42.99, 180, "Books");
        shoppingService.addProduct("The Pragmatic Programmer", "From journeyman to master", 41.99, 170, "Books");
        shoppingService.addProduct("Spring in Action", "Comprehensive guide to Spring Framework", 45.99, 140, "Books");
        
        // Home & Garden
        shoppingService.addProduct("Dyson V15 Vacuum", "Powerful cordless vacuum cleaner", 649.99, 20, "Home & Garden");
        shoppingService.addProduct("Instant Pot Duo", "7-in-1 electric pressure cooker", 89.99, 55, "Home & Garden");
        shoppingService.addProduct("Plant Grow Light", "LED light for indoor plants", 29.99, 90, "Home & Garden");
        shoppingService.addProduct("Coffee Maker", "Programmable drip coffee maker", 79.99, 40, "Home & Garden");
        shoppingService.addProduct("Air Purifier", "HEPA filter air purifier", 199.99, 30, "Home & Garden");
        
        // Sports & Outdoors
        shoppingService.addProduct("Yoga Mat", "Non-slip exercise yoga mat", 24.99, 120, "Sports & Outdoors");
        shoppingService.addProduct("Dumbbells Set", "Adjustable weight dumbbells", 149.99, 35, "Sports & Outdoors");
        shoppingService.addProduct("Camping Tent", "4-person waterproof camping tent", 299.99, 25, "Sports & Outdoors");
        shoppingService.addProduct("Mountain Bike", "21-speed mountain bicycle", 599.99, 15, "Sports & Outdoors");
        shoppingService.addProduct("Water Bottle", "Insulated stainless steel bottle", 19.99, 200, "Sports & Outdoors");
        
        System.out.println("Added 25 sample products across 5 categories.");
    }

    public static void main(String[] args) {
        SampleDataInitializer initializer = new SampleDataInitializer();
        initializer.initializeSampleData();
    }
}
