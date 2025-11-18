package com.onlineshopping.model;

public class Customer {
    private int id;
    private String username;
    private String password;
    private int ranking; // Based on total orders/purchases

    public Customer() {}

    public Customer(String username, String password) {
        this.username = username;
        this.password = password;
        this.ranking = 0;
    }

    public Customer(int id, String username, String password, int ranking) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.ranking = ranking;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getRanking() { return ranking; }
    public void setRanking(int ranking) { this.ranking = ranking; }

    @Override
    public String toString() {
        return String.format("Customer{id=%d, username='%s', ranking=%d}",
                id, username, ranking);
    }
}
