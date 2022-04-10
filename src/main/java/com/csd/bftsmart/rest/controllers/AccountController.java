package com.csd.bftsmart.rest.controllers;

import an.awesome.pipelinr.Pipeline;
import com.csd.bftsmart.application.accounts.commands.*;
import com.csd.bftsmart.application.entities.Transaction;
import com.csd.bftsmart.infrastructure.pipelinr.PipelinrConfig;
import com.csd.bftsmart.rest.requests.AccountRequest;
import com.csd.bftsmart.rest.requests.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final Pipeline pipeline;

    @Autowired
    public AccountController(@Qualifier(PipelinrConfig.CONTROLLER_PIPELINE) Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @PostMapping
    public void createAccount(@RequestBody AccountRequest accountRequest) {
        new CreateAccountCommand(accountRequest.userId(), accountRequest.accountId()).execute(pipeline);
    }

    @PostMapping("/loadMoney/{accountId}")
    public void loadMoney(@PathVariable String accountId, @RequestParam int value) {
        new LoadMoneyCommand(accountId, value).execute(pipeline);
    }

    @PostMapping("/transaction")
    public void sendTransaction(@RequestBody TransactionRequest transactionRequest) {
        new SendTransactionCommand(
                transactionRequest.from(),
                transactionRequest.to(),
                transactionRequest.value()
        ).execute(pipeline);
    }

    @GetMapping("/transaction")
    public List<Transaction> getAllTransactions() {
        return new GetAllTransactionsQuery().execute(pipeline);
    }

    @GetMapping("/extract/{accountId}")
    public List<Transaction> getExtract(@PathParam("accountId") String accountId) {
        return new GetExtractQuery(accountId).execute(pipeline);
    }
}
