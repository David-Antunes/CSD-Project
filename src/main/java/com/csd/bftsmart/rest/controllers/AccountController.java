package com.csd.bftsmart.rest.controllers;

import com.csd.bftsmart.application.services.AccountService;
import com.csd.bftsmart.rest.models.AccountRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public void createAccount(@RequestBody AccountRequestModel accountRequest) {
        accountService.createAccount(accountRequest.getUserId(), accountRequest.getAccountId());
    }

    @PostMapping("/loadMoney/{accountId}")
    public void loadMoney(@PathVariable String accountId, @RequestParam int value) {
        accountService.loadMoney(accountId, value);
    }
}
