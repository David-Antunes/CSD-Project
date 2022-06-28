package com.csd.blockneat.client;

import com.csd.blockneat.application.entities.Account;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.List;

public interface BlockNeatAPI {

    InternalUser getInternalUser();

    String createUser() throws IOException, InterruptedException, SignatureException, InvalidKeyException;

    String getAllUsers() throws IOException, InterruptedException;

    String createAccount(String accountId) throws SignatureException, InvalidKeyException, IOException, InterruptedException;

    String getAllAccounts() throws IOException, InterruptedException;

    String getBalance(String accountId) throws IOException, InterruptedException;

    String loadMoney(String accountId, int value) throws SignatureException, InvalidKeyException, IOException, InterruptedException;

    String sendTransaction(String from, String to, int value) throws SignatureException, InvalidKeyException, IOException, InterruptedException;

    String getExtract(String accountId) throws IOException, InterruptedException;

    String getTotalValue(List<String> accounts) throws IOException, InterruptedException;

    String getGlobalValue() throws IOException, InterruptedException;

    String getAllTransactions() throws IOException, InterruptedException;

    String getLedger() throws IOException, InterruptedException;

    byte[] getNextBlock() throws IOException, InterruptedException;

    void proposeBlock(byte[] block) throws IOException, InterruptedException;
}
