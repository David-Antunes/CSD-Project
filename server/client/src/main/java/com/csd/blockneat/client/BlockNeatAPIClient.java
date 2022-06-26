package com.csd.blockneat.client;

import com.csd.blockneat.application.entities.Account;
import com.csd.blockneat.application.entities.Transaction;
import com.csd.blockneat.application.entities.User;
import com.csd.blockneat.rest.requests.AccountRequest;
import com.csd.blockneat.rest.requests.UserRequest;
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

public class BlockNeatAPIClient implements BlockNeatAPI {

    public static final String USERS = "/users";
    public static final String ACCOUNTS = "/accounts";
    public static final String TRANSACTIONS = "/transactions";
    public static final String LEDGER = "/ledger";
    private final InternalUser internalUser;
    String endpoint;

    HttpClient httpClient;

    ObjectMapper om = new ObjectMapper();

    public BlockNeatAPIClient(InternalUser internalUser, String endpoint) {
        this.internalUser = internalUser;
        this.endpoint = endpoint;
        this.httpClient = HttpClient.newHttpClient();
    }

    private String toJson(Object object) {
        try {
            return om.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    private String signBody(String body) throws SignatureException, InvalidKeyException {
        return internalUser.sign(body);
    }


    private HttpRequest generatePostRequest(String path, HttpRequest.BodyPublisher body, String signature) {
        return HttpRequest.newBuilder()
                .uri(URI.create(endpoint + path))
                .headers("Content-Type", "application/json")
                .headers("signature", signature)
                .POST(body)
                .build();
    }

    private HttpRequest generateEmptyGetRequest(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(endpoint + path))
                .headers("Content-Type", "application/json")
                .GET()
                .build();
    }

    @Override
    public String createUser() throws IOException, InterruptedException, SignatureException, InvalidKeyException {
        String signature = signBody(internalUser.getUsername());
        UserRequest body = new UserRequest(new User.Id(internalUser.getUsername(), internalUser.getPublicKey()));

        HttpRequest httpRequest = generatePostRequest(USERS, HttpRequest.BodyPublishers.ofString(toJson(body)), signature);

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @Override
    public String getAllUsers() throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(generateEmptyGetRequest("/users"), HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @Override
    public String createAccount(String accountId) throws SignatureException, InvalidKeyException, IOException, InterruptedException {
        String signature = signBody(accountId);
        AccountRequest body = new AccountRequest(new User.Id(internalUser.getUsername(), internalUser.getPublicKey()), accountId);

        HttpRequest httpRequest = generatePostRequest(ACCOUNTS, HttpRequest.BodyPublishers.ofString(toJson(body)), signature);

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @Override
    public String getAllAccounts() throws IOException, InterruptedException {

        HttpResponse<String> response = httpClient.send(generateEmptyGetRequest("/accounts"), HttpResponse.BodyHandlers.ofString());
        return response.body();

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
    public String getAllTransactions() throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(generateEmptyGetRequest(TRANSACTIONS), HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @Override
    public String getLedger() throws IOException, InterruptedException {

        HttpResponse<String> response = httpClient.send(generateEmptyGetRequest(LEDGER), HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
