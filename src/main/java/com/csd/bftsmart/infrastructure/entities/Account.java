package com.csd.bftsmart.infrastructure.entities;

import javax.persistence.*;

@Entity
@Table(name = "account")
public class Account {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "balance", nullable = false)
    private int balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}