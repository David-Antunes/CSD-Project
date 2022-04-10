package com.csd.bftsmart.rest.controllers;

import an.awesome.pipelinr.Pipeline;
import com.csd.bftsmart.application.accounts.commands.CreateAccountCommand;
import com.csd.bftsmart.application.accounts.commands.LoadMoneyCommand;
import com.csd.bftsmart.application.accounts.commands.SendTransactionCommand;
import com.csd.bftsmart.infrastructure.pipelinr.PipelinrConfig;
import com.csd.bftsmart.rest.requests.AccountRequest;
import com.csd.bftsmart.rest.requests.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

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
}
