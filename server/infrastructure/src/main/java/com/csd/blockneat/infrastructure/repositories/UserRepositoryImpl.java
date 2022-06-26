package com.csd.blockneat.infrastructure.repositories;

import com.csd.blockneat.application.accounts.commands.CreateAccountCommand;
import com.csd.blockneat.application.entities.Account;
import com.csd.blockneat.application.entities.User;
import com.csd.blockneat.application.users.UserRepository;
import com.csd.blockneat.application.users.commands.CreateUserCommand;
import com.csd.blockneat.infrastructure.persistence.InMemoryLedger;
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
        return ledger.getUnconfirmedCommandsStream()
                .filter(CreateUserCommand.class::isInstance)
                .map(CreateUserCommand.class::cast)
                .map(CreateUserCommand::userId)
                .map(User.Id::email)
                .anyMatch(userId::equals);
    }

    @Override
    public List<User> getAll() {
        var createAccountCommands = ledger.getUnconfirmedCommandsStream()
                .filter(CreateAccountCommand.class::isInstance)
                .map(CreateAccountCommand.class::cast)
                .toList();

        return ledger.getUnconfirmedCommandsStream()
                .filter(CreateUserCommand.class::isInstance)
                .map(CreateUserCommand.class::cast)
                .map(userCommand -> {
                    var id = userCommand.userId();
                    var userAccounts = createAccountCommands.stream()
                            .filter(accountCommand -> accountCommand.userId().equals(id))
                            .map(accountCommand -> new Account(accountCommand.accountId(), id))
                            .toList();
                    return new User(id, userAccounts);
                })
                .toList();
    }

}
