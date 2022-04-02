package com.csd.bftsmart.infrastructure.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @OneToMany(mappedBy = "user")
    private List<Account> accounts = new ArrayList<>();

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}