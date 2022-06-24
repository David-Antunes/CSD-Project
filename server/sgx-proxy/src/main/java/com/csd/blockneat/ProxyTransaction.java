package com.csd.blockneat;

import com.csd.blockneat.rest.requests.TransactionRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/proxy")
public class ProxyTransaction {

    @Autowired
    public ProxyTransaction() {

        System.getProperties().setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
    }

    @PostMapping()
    public void sendTransaction(@RequestBody TransactionRequest transactionRequest, @RequestHeader("signature") String signBase64) throws IOException, URISyntaxException, InterruptedException {
        String url = "https://172.20.0.2:8443/transactions";
        URI uri;
        ObjectMapper om = new ObjectMapper();
        String body;

        uri = new URI(url);
        body = om.writeValueAsString(transactionRequest);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .headers("signature", signBase64)
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
