package com.jpmc.midascore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")  // 👈 Use 'users' instead of 'user'
public class MyRecord {

    @Id
    private String id;
    private String name;
    private float balance;

    public MyRecord() {
    }

    public MyRecord(String name, float balance) {
        this.id = name;       // 👈 Assuming name is unique and used as ID
        this.name = name;
        this.balance = balance;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
}
