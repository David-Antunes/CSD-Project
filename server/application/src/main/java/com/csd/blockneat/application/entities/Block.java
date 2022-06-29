package com.csd.blockneat.application.entities;

import com.csd.blockneat.application.accounts.commands.CreateAccountCommand;
import com.csd.blockneat.application.commands.WriteCommand;
import com.csd.blockneat.application.transactions.commands.LoadMoneyCommand;
import com.csd.blockneat.application.transactions.commands.SendTransactionCommand;
import com.csd.blockneat.application.users.commands.CreateUserCommand;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.io.Serializable;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value= CreateAccountCommand.class, name="ProposeBlockCommand"),
        @JsonSubTypes.Type(value= CreateAccountCommand.class, name="CreateAccountCommand"),
        @JsonSubTypes.Type(value= CreateUserCommand.class, name="CreateUserCommand"),
        @JsonSubTypes.Type(value= SendTransactionCommand.class, name="transaction"),
        @JsonSubTypes.Type(value= LoadMoneyCommand.class, name="loadMoney"),
        @JsonSubTypes.Type(value= Transaction.class, name="transactions"),
})
public record Block(int id, int nonce, String previousBlockHash, List<WriteCommand> transactions) implements Serializable {
}
