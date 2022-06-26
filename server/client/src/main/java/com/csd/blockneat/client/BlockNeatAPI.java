package com.csd.blockneat.client;

import com.csd.blockneat.application.entities.Account;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.List;

public interface BlockNeatAPI {

    String createUser() throws IOException, InterruptedException, SignatureException, InvalidKeyException;

    String getAllUsers() throws IOException, InterruptedException;

    String createAccount(String accountId) throws SignatureException, InvalidKeyException, IOException, InterruptedException;

    String getAllAccounts() throws IOException, InterruptedException;

    String getBalance(String accountId) throws IOException, InterruptedException;

    void loadMoney(String accountId, int value);

    void sendTransaction(String from, String to, int value);

    String getExtract(String accountId) throws IOException, InterruptedException;

    String getTotalValue(List<Account> accounts) throws IOException, InterruptedException;

    String getGlobalValue() throws IOException, InterruptedException;

    String getAllTransactions() throws IOException, InterruptedException;

    String getLedger() throws IOException, InterruptedException;
}
