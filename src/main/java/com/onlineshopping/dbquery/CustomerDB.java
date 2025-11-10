package com.onlineshopping.dbquery;

import com.onlineshopping.database.DatabaseManager;
import com.onlineshopping.model.Customer;
import org.mindrot.jbcrypt.BCrypt; // For password hashing

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDB {
    private final DatabaseManager dbManager;

    public CustomerDB() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (username, email, password_hash, full_name, address, ranking) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, customer.getUsername());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPasswordHash());
            pstmt.setString(4, customer.getFullName());
            pstmt.setString(5, customer.getAddress());
            pstmt.setInt(6, customer.getRanking());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the last inserted ID
                try (Statement stmt = dbManager.getConnection().createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()")) {
                    if (rs.next()) {
                        customer.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding customer: " + e.getMessage());
        }
        return false;
    }

    public boolean removeCustomer(int customerId) {
        String sql = "DELETE FROM customers WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing customer: " + e.getMessage());
        }
        return false;
    }

    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET username = ?, email = ?, full_name = ?, address = ?, ranking = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, customer.getUsername());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getFullName());
            pstmt.setString(4, customer.getAddress());
            pstmt.setInt(5, customer.getRanking());
            pstmt.setInt(6, customer.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
        }
        return false;
    }

    public boolean updatePassword(int customerId, String newPassword) {
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        String sql = "UPDATE customers SET password_hash = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, customerId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating customer password: " + e.getMessage());
        }
        return false;
    }

    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Customer(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("full_name"),
                    rs.getString("address"),
                    rs.getInt("ranking")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer: " + e.getMessage());
        }
        return null;
    }

    public Customer getCustomerByUsername(String username) {
        String sql = "SELECT * FROM customers WHERE username = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Customer(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("full_name"),
                    rs.getString("address"),
                    rs.getInt("ranking")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer by username: " + e.getMessage());
        }
        return null;
    }

    public boolean validatePassword(String username, String password) {
        Customer customer = getCustomerByUsername(username);
        if (customer != null) {
            return BCrypt.checkpw(password, customer.getPasswordHash());
        }
        return false;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY ranking DESC, username";
        
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(new Customer(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("full_name"),
                    rs.getString("address"),
                    rs.getInt("ranking")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all customers: " + e.getMessage());
        }
        return customers;
    }

    public List<Customer> getCustomerLeaderboard() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY ranking DESC LIMIT 10";
        
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(new Customer(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("full_name"),
                    rs.getString("address"),
                    rs.getInt("ranking")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer leaderboard: " + e.getMessage());
        }
        return customers;
    }

    public boolean updateRanking(int customerId, int newRanking) {
        String sql = "UPDATE customers SET ranking = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, newRanking);
            pstmt.setInt(2, customerId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating customer ranking: " + e.getMessage());
        }
        return false;
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM customers WHERE username = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
        }
        return false;
    }

    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM customers WHERE email = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        }
        return false;
    }
}
