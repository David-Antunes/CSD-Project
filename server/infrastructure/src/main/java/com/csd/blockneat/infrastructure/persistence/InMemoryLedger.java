package com.csd.blockneat.infrastructure.persistence;

import com.csd.blockneat.application.ExceptionCode;
import com.csd.blockneat.application.commands.WriteCommand;
import com.csd.blockneat.application.crypto.SHA512;
import com.csd.blockneat.application.entities.Block;
import com.csd.blockneat.application.entities.ValidatedBlock;
import com.csd.blockneat.application.ledger.LedgerRepository;
import com.csd.blockneat.application.transactions.commands.LoadMoneyCommand;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

@Slf4j
@EqualsAndHashCode
@Repository
public class InMemoryLedger implements LedgerRepository, Serializable {

    private Queue<WriteCommand> commands;
    @Getter
    private List<ValidatedBlock> blocks;
    private final LoadMoneyCommand.Handler loadMoneyHandler;

    public InMemoryLedger(LoadMoneyCommand.Handler loadMoneyHandler) {
        this.loadMoneyHandler = loadMoneyHandler;
        commands = new ConcurrentLinkedQueue<>();
        blocks = new ArrayList<>();
    }

    @Override
    public boolean append(WriteCommand command) {
        return commands.add(command);
    }

    @Override
    public Stream<WriteCommand> getUnconfirmedCommandsStream() {
        return Stream.concat(getConfirmedCommandsStream(), commands.stream());
    }

    @Override
    public Stream<WriteCommand> getConfirmedCommandsStream() {
        return blocks.stream()
                .map(ValidatedBlock::block)
                .map(Block::transactions)
                .flatMap(Collection::stream);
    }

    @Override
    public Block getNextBlock() {
        List<WriteCommand> transactions = commands.stream()
                .limit(16)
                .toList();
        if (transactions.size() != 16) {
            return null;
        }
        return new Block(blocks.size(), 0, blocks.get(blocks.size() - 1).hash(), transactions);
    }

    @Override
    public boolean receiveBlock(ValidatedBlock validatedBlock) {
        Block minedBlock = validatedBlock.block();
        String validatedHash = SHA512.hexHash(minedBlock.toString());
        if (!validatedHash.startsWith("000") || !validatedBlock.hash().equals(validatedHash)) {
            return false;
        }
        var minedTransactions = new ArrayList<>(minedBlock.transactions());
        WriteCommand reward = minedTransactions.remove(0);
        Block nextBlock = getNextBlock();

        if (nextBlock.id() != minedBlock.id() ||
                !nextBlock.transactions().equals(minedTransactions) ||
                !nextBlock.previousBlockHash().equals(minedBlock.previousBlockHash()) ||
                !(reward instanceof LoadMoneyCommand payout) ||
                loadMoneyHandler.handle(payout).left() != ExceptionCode.SUCCESS) {
            return false;
        }
        for (int i = 0; i < 16; i++) {
            commands.remove();
        }

        return blocks.add(validatedBlock);
    }

    public byte[] getSnapshot() {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {
            objOut.writeObject(this);
            return byteOut.toByteArray();
        } catch (IOException e) {
            log.warn("Error while taking snapshot", e);
        }
        return new byte[0];
    }

    public void installSnapshot(byte[] state) {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(state);
             ObjectInput objIn = new ObjectInputStream(byteIn)) {
            var replicaLedger = (InMemoryLedger) objIn.readObject();
            commands = replicaLedger.commands;
            blocks = replicaLedger.blocks;
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error while installing snapshot", e);
        }
    }
}
