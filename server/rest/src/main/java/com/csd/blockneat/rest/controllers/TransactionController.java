package com.csd.blockneat.rest.controllers;

import an.awesome.pipelinr.Pipeline;
import com.csd.blockneat.application.entities.Transaction;
import com.csd.blockneat.application.transactions.commands.*;
import com.csd.blockneat.rest.exceptions.HandleWebExceptions;
import com.csd.blockneat.infrastructure.pipelinr.PipelinrConfig;
import com.csd.blockneat.rest.requests.TransactionRequest;
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
    public void sendTransaction(@RequestBody TransactionRequest transactionRequest, @RequestHeader("signature") String signBase64) {
        HandleWebExceptions.resultOrException(new SendTransactionCommand(
                        transactionRequest.from(),
                        transactionRequest.to(),
                        transactionRequest.value(),
                        signBase64, System.currentTimeMillis()
                ).execute(pipeline)
        );
    }

    @PostMapping("/loadMoney/{accountId}")
    public void loadMoney(@PathVariable String accountId, @RequestParam int value, @RequestHeader("signature") String signBase64) {
        HandleWebExceptions.resultOrException(
                new LoadMoneyCommand(accountId, value, signBase64, System.currentTimeMillis()).execute(pipeline)
        );
    }


    @GetMapping()
    public List<Transaction> getAllTransactions() {
        return new GetAllTransactionsQuery().execute(pipeline);
    }

    @GetMapping("/extract/{accountId}")
    public List<Transaction> getExtract(@PathVariable("accountId") String accountId) {
        return HandleWebExceptions.resultOrException(
                new GetExtractQuery(accountId).execute(pipeline)
        );
    }

    @GetMapping("/global")
    public int getGlobalValue() {
        return new GetGlobalValueQuery().execute(pipeline);
    }

    @GetMapping("/total")
    public Map<String, Integer> getTotalValue(@RequestBody List<String> accounts) {
        return new GetTotalValueQuery(accounts).execute(pipeline);
    }
}
