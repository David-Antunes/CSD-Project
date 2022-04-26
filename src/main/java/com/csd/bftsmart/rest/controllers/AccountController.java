package com.csd.bftsmart.rest.controllers;

import an.awesome.pipelinr.Pipeline;
import com.csd.bftsmart.application.accounts.commands.*;
import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.exceptions.HandleWebExceptions;
import com.csd.bftsmart.infrastructure.pipelinr.PipelinrConfig;
import com.csd.bftsmart.rest.requests.AccountRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

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
    public void createAccount(@RequestBody AccountRequest accountRequest, @RequestHeader("signature") String signBase64) {
        HandleWebExceptions.resultOrException(
                new CreateAccountCommand(accountRequest.userId(), accountRequest.accountId(), signBase64).execute(pipeline)
        );
    }

    @GetMapping("/balance/{accountId}")
    public int getBalance(@PathVariable("accountId") String accountId)  {
        return HandleWebExceptions.resultOrException(
                new GetBalanceQuery(accountId).execute(pipeline)
        );
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return new GetAllAccountsQuery().execute(pipeline);
    }
}
