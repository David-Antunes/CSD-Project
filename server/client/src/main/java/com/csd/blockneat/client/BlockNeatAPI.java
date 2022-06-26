package com.csd.blockneat.client;

import com.csd.blockneat.application.entities.Account;
import com.csd.blockneat.application.entities.Transaction;
import com.csd.blockneat.rest.responses.LedgerResponse;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;

public interface BlockNeatAPI {

    String createUser() throws IOException, InterruptedException, SignatureException, InvalidKeyException;

    String getAllUsers() throws IOException, InterruptedException;

    void createAccount(String accountId) throws SignatureException, InvalidKeyException;

    List<Account> getAllAccounts();

    void getBalance(String accountId);

    void loadMoney(String accountId, int value);

    void sendTransaction(String from, String to, int value);

    List<Transaction> getExtract(String accountId);

    Map<String, Integer> getTotalValue(List<Account> accounts);

    int getGlobalValue();

    List<Transaction> getAllTransactions();

    LedgerResponse getLedger();
}
