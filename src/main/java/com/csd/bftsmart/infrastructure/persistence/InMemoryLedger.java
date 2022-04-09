package com.csd.bftsmart.infrastructure.persistence;

import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.application.entities.Transaction;
import com.csd.bftsmart.application.entities.User;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.TreeMap;

@Slf4j
@Getter
@Repository
public class InMemoryLedger {

    private TreeMap<String, User> users;
    private TreeMap<String, Account> accounts;
    private ArrayList<Transaction> transactions;

    public InMemoryLedger() {
        users = new TreeMap<>();
        accounts = new TreeMap<>();
        transactions = new ArrayList<>();
        transactions.add(new Transaction(0, null, null, 0)); //TODO
    }

}
