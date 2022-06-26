package com.csd.blockneat.client;

import com.csd.blockneat.application.entities.Account;
import com.csd.blockneat.application.entities.Transaction;
import com.csd.blockneat.application.entities.User;
import com.csd.blockneat.rest.requests.UserRequest;
import com.csd.blockneat.rest.responses.LedgerResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;

public class BlockNeatAPIClient implements BlockNeatAPI{

    private final InternalUser internalUser;
    String endpoint;

    HttpClient httpClient;

    ObjectMapper om = new ObjectMapper();

    public BlockNeatAPIClient(InternalUser internalUser, String endpoint) {
        this.internalUser = internalUser;
        this.endpoint = endpoint;
        this.httpClient = HttpClient.newHttpClient();
    }

    private String toJson(Object object)   {
        try {
            return om.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    private String signBody(String body) throws SignatureException, InvalidKeyException {
        return internalUser.sign(body);
    }

    @Override
    public String createUser() throws IOException, InterruptedException, SignatureException, InvalidKeyException {
        String signature = signBody(internalUser.getUsername());
        System.out.println(signature);
        UserRequest body = new UserRequest(new User.Id(internalUser.getUsername(), internalUser.getPublicKey()));

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "/users"))
                .headers("Content-Type", "application/json")
                .headers("signature", signature)
                .POST(HttpRequest.BodyPublishers.ofString(toJson(body)))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @Override
    public List<InternalUser> getAllUsers() {
        return null;
    }

    @Override
    public void createAccount(String accountId) {

    }

    @Override
    public List<Account> getAllAccounts() {
        return null;
    }

    @Override
    public void getBalance(String accountId) {

    }

    @Override
    public void loadMoney(String accountId, int value) {

    }

    @Override
    public void sendTransaction(String from, String to, int value) {

    }

    @Override
    public List<Transaction> getExtract(String accountId) {
        return null;
    }

    @Override
    public Map<String, Integer> getTotalValue(List<Account> accounts) {
        return null;
    }

    @Override
    public int getGlobalValue() {
        return 0;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return null;
    }

    @Override
    public LedgerResponse getLedger() {
        return null;
    }
}
