package com.jpmc.midascore.external;

public class Balance {
    private String userId;
    private double balance;

    // ✅ Default constructor required by Jackson
    public Balance() {}

    public Balance(String userId, double balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Balance{userId='" + userId + "', balance=" + balance + "}";
    }
}
