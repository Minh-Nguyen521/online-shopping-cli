package com.onlineshopping.model;

public class Customer {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private String address;
    private int ranking; // Based on total orders/purchases

    public Customer() {}

    public Customer(String username, String email, String passwordHash, String fullName, String address) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.address = address;
        this.ranking = 0;
    }

    public Customer(int id, String username, String email, String passwordHash, 
                   String fullName, String address, int ranking) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.address = address;
        this.ranking = ranking;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getRanking() { return ranking; }
    public void setRanking(int ranking) { this.ranking = ranking; }

    @Override
    public String toString() {
        return String.format("Customer{id=%d, username='%s', email='%s', fullName='%s', address='%s', ranking=%d}",
                id, username, email, fullName, address, ranking);
    }
}
