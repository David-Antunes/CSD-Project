package com.csd.bftsmart.rest.controllers;

import an.awesome.pipelinr.Pipeline;
import com.csd.bftsmart.application.accounts.commands.*;
import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.application.entities.Transaction;
import com.csd.bftsmart.infrastructure.pipelinr.PipelinrConfig;
import com.csd.bftsmart.rest.requests.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final Pipeline pipeline;

    @Autowired
    public TransactionController(@Qualifier(PipelinrConfig.CONTROLLER_PIPELINE) Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @PostMapping()
    public void sendTransaction(@RequestBody TransactionRequest transactionRequest) {
        new SendTransactionCommand(
                transactionRequest.from(),
                transactionRequest.to(),
                transactionRequest.value()
        ).execute(pipeline);
    }

    @PostMapping("/loadMoney/{accountId}")
    public void loadMoney(@PathVariable String accountId, @RequestParam int value) {
        new LoadMoneyCommand(accountId, value).execute(pipeline);
    }


    @GetMapping()
    public List<Transaction> getAllTransactions() {
        return new GetAllTransactionsQuery().execute(pipeline);
    }

    @GetMapping("/extract/{accountId}")
    public List<Transaction> getExtract(@PathVariable("accountId") String accountId) {
        return new GetExtractQuery(accountId).execute(pipeline);
    }

    @GetMapping("/global")
    public int getGlobalValue() {
        return new GetGlobalValueQuery().execute(pipeline);
    }

    @GetMapping("/total")
    public Map<Account, Integer> getGlobalValue(@RequestBody List<Account> accounts) {
        return new GetTotalValueQuery(accounts).execute(pipeline);
    }
}
