package com.onlineshopping.dbquery;

import com.onlineshopping.database.DatabaseManager;
import com.onlineshopping.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDB {
    private final DatabaseManager dbManager;

    public ProductDB() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean addProduct(Product product) {
        String sql = "{CALL add_product(?, ?, ?, ?, ?, ?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setString(1, product.getName());
            cstmt.setString(2, product.getDescription());
            cstmt.setDouble(3, product.getPrice());
            cstmt.setInt(4, product.getStock());
            cstmt.setString(5, product.getCategory());
            cstmt.registerOutParameter(6, Types.INTEGER);
            
            cstmt.execute();
            
            int productId = cstmt.getInt(6);
            product.setId(productId);
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
        }
        return false;
    }

    public boolean removeProduct(int productId) {
        String sql = "{CALL remove_product(?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, productId);
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error removing product: " + e.getMessage());
        }
        return false;
    }

    public boolean updateProduct(Product product) {
        String sql = "{CALL update_product(?, ?, ?, ?, ?, ?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, product.getId());
            cstmt.setString(2, product.getName());
            cstmt.setString(3, product.getDescription());
            cstmt.setDouble(4, product.getPrice());
            cstmt.setInt(5, product.getStock());
            cstmt.setString(6, product.getCategory());
            
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
        }
        return false;
    }

    public Product getProductById(int productId) {
        String sql = "{CALL get_product_by_id(?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, productId);
            ResultSet rs = cstmt.executeQuery();
            
            if (rs.next()) {
                return new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting product: " + e.getMessage());
        }
        return null;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "{CALL get_all_products()}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            
            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all products: " + e.getMessage());
        }
        return products;
    }

    public List<Product> searchProducts(String searchTerm) {
        List<Product> products = new ArrayList<>();
        String sql = "{CALL search_products(?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setString(1, searchTerm);
            
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage());
        }
        return products;
    }

    public boolean updateStock(int productId, int newStock) {
        String sql = "{CALL update_stock(?, ?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, productId);
            cstmt.setInt(2, newStock);
            
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating product stock: " + e.getMessage());
        }
        return false;
    }
}
