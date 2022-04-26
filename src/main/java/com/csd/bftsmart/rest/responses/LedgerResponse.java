package com.csd.bftsmart.rest.responses;

import com.csd.bftsmart.application.commands.WriteCommand;
import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.application.entities.Transaction;
import com.csd.bftsmart.application.entities.User;

import java.util.List;
import java.util.Map;

public record LedgerResponse(Map<String, User> users, Map<String, Account> accounts, List<Transaction> transactions, List<WriteCommand> commands) {
}
