package com.csd.blockneat.application.commands;

import com.csd.blockneat.application.accounts.commands.CreateAccountCommand;
import com.csd.blockneat.application.entities.Transaction;
import com.csd.blockneat.application.transactions.commands.LoadMoneyCommand;
import com.csd.blockneat.application.transactions.commands.SendTransactionCommand;
import com.csd.blockneat.application.users.commands.CreateUserCommand;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonSubTypes({
        @JsonSubTypes.Type(value= CreateAccountCommand.class, name="ProposeBlockCommand"),
        @JsonSubTypes.Type(value= CreateAccountCommand.class, name="CreateAccountCommand"),
        @JsonSubTypes.Type(value= CreateUserCommand.class, name="CreateUserCommand"),
        @JsonSubTypes.Type(value= SendTransactionCommand.class, name="transaction"),
        @JsonSubTypes.Type(value= LoadMoneyCommand.class, name="loadMoney"),
        @JsonSubTypes.Type(value= Transaction.class, name="transactions"),
})
public interface WriteCommand {
}
