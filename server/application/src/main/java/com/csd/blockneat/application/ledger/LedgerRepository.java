package com.csd.blockneat.application.ledger;

import com.csd.blockneat.application.commands.WriteCommand;
import com.csd.blockneat.application.entities.Block;
import com.csd.blockneat.application.entities.ValidatedBlock;

import java.util.List;

public interface LedgerRepository {
    boolean append(WriteCommand command);

    Block getNextBlock();

    boolean receiveBlock(ValidatedBlock block);

    List<ValidatedBlock> getBlocks();
}
