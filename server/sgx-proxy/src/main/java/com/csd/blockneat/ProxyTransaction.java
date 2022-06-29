package com.csd.blockneat;

import com.csd.blockneat.crypto.BlockneatSignatures;
import com.csd.blockneat.requests.Response;
import com.csd.blockneat.requests.TransactionRequest;
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

    String url;
    URI uri;
    ObjectMapper om;
    BlockneatSignatures replicaSignatures;
    @Autowired
    public ProxyTransaction() throws URISyntaxException {
        url = "https://172.20.0.2:8443/transactions";
        uri = new URI(url);
        om = new ObjectMapper();
        System.getProperties().setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
        replicaSignatures = new BlockneatSignatures(4);
    }

    @PostMapping()
    public Response sendTransaction(@RequestBody TransactionRequest transactionRequest, @RequestHeader("signature") String signBase64) throws IOException, InterruptedException {

        String body;
        body = om.writeValueAsString(transactionRequest);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .headers("signature", signBase64)
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

       var response =  HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        Response signatures = om.readValue(response.body(), Response.class);

        if(!replicaSignatures.verifySignatures(signatures))
            return null;
        else {
            return new Response(replicaSignatures.getSign().of(signatures.response().object()));
        }

    }
}
