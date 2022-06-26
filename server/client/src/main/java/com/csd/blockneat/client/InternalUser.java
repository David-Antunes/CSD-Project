package com.csd.blockneat.client;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.HashSet;
import java.util.Set;
import java.util.Base64;
public class InternalUser implements Runnable {
    Set<String> accounts;
    ECDSASignature signature;

    String username;
    public InternalUser(String username, ECDSASignature signature) {
        this.signature = signature;
        this.username = username;
        this.accounts = new HashSet<>();
    }

    public void addAccount(String accountId) {
        accounts.add(accountId);
    }

    public Set<String> getAccounts() {
        return accounts;
    }

    public String getUsername() {
        return username;
    }

    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(signature.getPublicKey());
    }

    @Override
    public void run() {

    }

    public String sign(String message) throws SignatureException, InvalidKeyException {
            return Base64.getEncoder().encodeToString(signature.sign(message.getBytes()));
    }
}
