package com.csd.blockneat.application.ledger;

import com.csd.blockneat.application.commands.WriteCommand;
import com.csd.blockneat.application.entities.Block;
import com.csd.blockneat.application.entities.ValidatedBlock;

import java.util.List;
import java.util.stream.Stream;

public interface LedgerRepository {
    boolean append(WriteCommand command);

    Stream<WriteCommand> getUnconfirmedCommandsStream();

    Stream<WriteCommand> getConfirmedCommandsStream();

    Block getNextBlock();

    boolean receiveBlock(ValidatedBlock block);

    List<ValidatedBlock> getBlocks();
}
