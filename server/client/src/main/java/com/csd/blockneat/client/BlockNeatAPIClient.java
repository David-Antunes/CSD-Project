package com.csd.blockneat.client;

import com.csd.blockneat.application.entities.User;
import com.csd.blockneat.rest.requests.BlockneatStatistic;
import com.csd.blockneat.rest.requests.AccountRequest;
import com.csd.blockneat.rest.requests.TransactionRequest;
import com.csd.blockneat.rest.requests.UserRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.List;

public class BlockNeatAPIClient implements BlockNeatAPI {

    public static final String USERS = "/users";
    public static final String ACCOUNTS = "/accounts";
    public static final String BALANCE = ACCOUNTS + "/balance/";
    public static final String TRANSACTIONS = "/transactions";
    public static final String TOTAL_VALUE = TRANSACTIONS + "/total";
    public static final String GLOBAL_VALUE = TRANSACTIONS + "/global";
    public static final String EXTRACT = TRANSACTIONS + "/extract/";
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

    @Override
    public InternalUser getInternalUser() {
        return internalUser;
    }

    private String signBody(String body) throws SignatureException, InvalidKeyException {
        return internalUser.sign(body);
    }


    private HttpRequest generatePostRequest(String path, HttpRequest.BodyPublisher body, String signature) {
        return HttpRequest.newBuilder()
                .uri(URI.create(endpoint + path))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("signature", signature)
                .POST(body)
                .build();
    }

    private HttpRequest generateEmptyGetRequest(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(endpoint + path))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .GET()
                .build();
    }

    private HttpRequest generateGetRequest(String path, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(endpoint + path))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.ofString(body))
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
        HttpResponse<String> response = httpClient.send(generateEmptyGetRequest(USERS), HttpResponse.BodyHandlers.ofString());
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
        HttpResponse<String> response = httpClient.send(generateEmptyGetRequest(ACCOUNTS), HttpResponse.BodyHandlers.ofString());
        return response.body();

    }

    @Override
    public String getBalance(String accountId) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(generateEmptyGetRequest(BALANCE + accountId), HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @Override
    public String loadMoney(String accountId, int value) throws SignatureException, InvalidKeyException, IOException, InterruptedException {
        String signature = signBody(accountId + value);
        TransactionRequest body = new TransactionRequest("", accountId, value);

        HttpRequest httpRequest = generatePostRequest(TRANSACTIONS + "/loadMoney/" + accountId + "?value=" + value, HttpRequest.BodyPublishers.ofString(toJson(body)), signature);

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @Override
    public String sendTransaction(String from, String to, int value) throws SignatureException, InvalidKeyException, IOException, InterruptedException {
        String signature = signBody(from + to + value);
        TransactionRequest body = new TransactionRequest(from, to, value);

        HttpRequest httpRequest = generatePostRequest(TRANSACTIONS, HttpRequest.BodyPublishers.ofString(toJson(body)), signature);

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @Override
    public String getExtract(String accountId) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(generateEmptyGetRequest(EXTRACT + accountId), HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @Override
    public String getTotalValue(List<String> accounts) throws IOException, InterruptedException {
        ObjectMapper om = new ObjectMapper();
        String body = om.writeValueAsString(accounts);
        HttpResponse<String> response = httpClient.send(generateGetRequest(TOTAL_VALUE, body), HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @Override
    public String getGlobalValue() throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(generateEmptyGetRequest(GLOBAL_VALUE), HttpResponse.BodyHandlers.ofString());
        return response.body();
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

    @Override
    public byte[] getNextBlock() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "/mining"))
                .GET()
                .build();

        var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        return response.body();
    }

    @Override
    public void proposeBlock(byte[] block) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "/mining"))
                .header("Content-Type", "application/octet-stream")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(block))
                .build();

        httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    }

    public BlockneatStatistic getBlockneatStatistic() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "/ledger/statistics"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        return om.readValue(response.body(), BlockneatStatistic.class);
    }
}
