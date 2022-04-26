package com.csd.bftsmart.application.ledger;

import com.csd.bftsmart.application.commands.WriteCommand;

public interface LedgerRepository {
    boolean append(WriteCommand command);
}
