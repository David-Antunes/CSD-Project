package com.csd.blockneat.application.accounts;

import com.csd.blockneat.application.entities.Account;
import com.csd.blockneat.application.entities.Transaction;

import java.util.List;
import java.util.Map;

public interface AccountRepository {

    List<Transaction> getAllTransactions();

    List<Transaction> getExtract(String accountId);

    int getUnconfirmedBalance(String accountId);

    int getGlobalValue();

    Map<String, Integer> getTotalValue(List<String> accounts);

    List<Account> getAll();

    boolean containsUnconfirmed(String accountId);

    boolean containsConfirmed(String accountId);

    Account getUnconfirmed(String accountId);

    int getConfirmedBalance(String accountId);
}
