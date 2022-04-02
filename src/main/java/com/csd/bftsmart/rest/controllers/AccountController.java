package com.csd.bftsmart.rest.controllers;

import com.csd.bftsmart.application.services.AccountService;
import com.csd.bftsmart.domain.entities.Account;
import com.csd.bftsmart.rest.models.AccountModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public void createAccount(AccountModel account) {
        Account account1 = new Account();
        accountService.createAccount(account1);
    }
}
