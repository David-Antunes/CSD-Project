package com.csd.bftsmart.infrastructure.persistence;

import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.application.entities.Transaction;
import com.csd.bftsmart.application.entities.User;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.*;
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

    public byte[] getSnapshot() {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {
            objOut.writeObject(this);
            return byteOut.toByteArray();
        } catch (IOException e) {
            log.warn("Error while taking snapshot", e);
        }
        return new byte[0];
    }

    public void installSnapshot(byte[] state) {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(state);
             ObjectInput objIn = new ObjectInputStream(byteIn)) {
            var replicaLedger = (InMemoryLedger) objIn.readObject();
            users = replicaLedger.users;
            accounts = replicaLedger.accounts;
            transactions = replicaLedger.transactions;
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error while installing snapshot", e);
        }
    }
}
