package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double balance; // ✅ updated from float to double

    // Getters and setters abc
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() { // ✅ updated
        return balance;
    }

    public void setBalance(double balance) { // ✅ updated
        this.balance = balance;
    }
}
