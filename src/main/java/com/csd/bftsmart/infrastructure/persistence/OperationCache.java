package com.csd.bftsmart.infrastructure.persistence;

import com.csd.bftsmart.application.entities.Transaction;
import org.springframework.context.annotation.Bean;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
public class OperationCache {

    Map<String, Integer> accountBalance;
    Map<String, List<Transaction>> accountTransaction;
    public OperationCache() {
        this.accountBalance = new TreeMap<>();
        this.accountTransaction = new TreeMap<>();
    }
    @Bean
    OperationCache cache() {
        return new OperationCache();
    }
    public void registerTransaction(Transaction transaction) {
        Integer fromBalance = accountBalance.get(transaction.from().id());
        Integer toBalance = accountBalance.get(transaction.to().id());

        if(fromBalance != null) {
            fromBalance -= transaction.value();
            accountTransaction.get(transaction.from().id()).add(transaction);
            accountBalance.put(transaction.from().id(), fromBalance );
        }
        List<Transaction> toTransactions = toBalance == null ? new LinkedList<>() : accountTransaction.get(toBalance);
        toBalance = toBalance == null ? transaction.value() : toBalance + transaction.value();
        accountBalance.put(transaction.to().id(), toBalance);
    }

    public Integer getBalance(String accountId) {
        return accountBalance.get(accountId);
    }

    public List<Transaction> getTransactions(String accountId) {
        return accountTransaction.get(accountId);
    }

}
