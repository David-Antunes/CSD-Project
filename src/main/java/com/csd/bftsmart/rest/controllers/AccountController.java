package com.csd.bftsmart.rest.controllers;

import an.awesome.pipelinr.Pipeline;
import com.csd.bftsmart.application.commands.accounts.CreateAccountCommand;
import com.csd.bftsmart.application.commands.accounts.LoadMoneyCommand;
import com.csd.bftsmart.rest.requests.AccountRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final Pipeline pipeline;

    @Autowired
    public AccountController(Pipeline pipeline) {
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
}
