package com.csd.blockneat.application.ledger;

import com.csd.blockneat.application.commands.WriteCommand;

import java.util.List;

public interface LedgerRepository {
    boolean append(WriteCommand command);

    List<WriteCommand> getCommands();
}
