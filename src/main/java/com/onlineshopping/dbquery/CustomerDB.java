package com.onlineshopping.dbquery;

import com.onlineshopping.database.DatabaseManager;
import com.onlineshopping.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDB {
    private final DatabaseManager dbManager;

    public CustomerDB() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean addCustomer(Customer customer) {
        String sql = "{CALL add_customer(?, ?, ?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setString(1, customer.getUsername());
            cstmt.setString(2, customer.getPassword());
            cstmt.registerOutParameter(3, Types.INTEGER);
            
            cstmt.execute();
            
            int customerId = cstmt.getInt(3);
            customer.setId(customerId);
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding customer: " + e.getMessage());
        }
        return false;
    }

    public boolean updateCustomer(Customer customer) {
        String sql = "{CALL update_customer(?, ?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, customer.getId());
            cstmt.setString(2, customer.getUsername());
            
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
        }
        return false;
    }

    public boolean updatePassword(int customerId, String newPassword) {
        String sql = "{CALL update_password(?, ?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, customerId);
            cstmt.setString(2, newPassword);
            
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating customer password: " + e.getMessage());
        }
        return false;
    }

    public Customer getCustomerByUsername(String username) {
        String sql = "{CALL get_customer_by_username(?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setString(1, username);
            ResultSet rs = cstmt.executeQuery();
            
            if (rs.next()) {
                return new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("username"),
                    rs.getString("password")
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
            return customer.getPassword().equals(password);
        }
        return false;
    }

    public boolean usernameExists(String username) {
        String sql = "{CALL customer_exists(?)}";
        
        try (CallableStatement cstmt = dbManager.getConnection().prepareCall(sql)) {
            cstmt.setString(1, username);
            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("exists_flag") == 1;
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
        }
        return false;
    }

}
