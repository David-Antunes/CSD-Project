package com.csd.bftsmart.infrastructure.repositories;

import com.csd.bftsmart.application.accounts.commands.CreateAccountCommand;
import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.application.entities.User;
import com.csd.bftsmart.application.users.UserRepository;
import com.csd.bftsmart.application.users.commands.CreateUserCommand;
import com.csd.bftsmart.infrastructure.persistence.InMemoryLedger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final InMemoryLedger ledger;

    @Autowired
    public UserRepositoryImpl(InMemoryLedger ledger) {
        this.ledger = ledger;
    }

    public boolean contains(String userId) {
        return ledger.getCommands().stream()
                .filter(CreateUserCommand.class::isInstance)
                .map(CreateUserCommand.class::cast)
                .map(CreateUserCommand::userId)
                .map(User.Id::email)
                .anyMatch(userId::equals);
    }

    @Override
    public List<User> getAll() {

        return ledger.getCommands().stream()
                .filter(CreateUserCommand.class::isInstance)
                .map(CreateUserCommand.class::cast)
                .map(userCommand -> {
                    var id = userCommand.userId();
                    var userAccounts = ledger.getCommands().stream()
                            .filter(CreateAccountCommand.class::isInstance)
                            .map(CreateAccountCommand.class::cast)
                            .filter(accountCommand -> accountCommand.userId().equals(id))
                            .map(accountCommand -> new Account(accountCommand.accountId(), id))
                            .toList();
                    return new User(id, userAccounts);
                })
                .toList();
    }

}
