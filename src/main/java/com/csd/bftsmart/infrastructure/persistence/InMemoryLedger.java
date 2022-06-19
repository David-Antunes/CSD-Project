package com.csd.bftsmart.infrastructure.persistence;

import com.csd.bftsmart.application.commands.WriteCommand;
import com.csd.bftsmart.application.ledger.LedgerRepository;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@EqualsAndHashCode
@Repository
public class InMemoryLedger implements LedgerRepository, Serializable {

    private List<WriteCommand> commands;

    public InMemoryLedger() {
        commands = new ArrayList<>();
    }

    @Override
    public boolean append(WriteCommand command) {
        return commands.add(command);
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
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error while installing snapshot", e);
        }
    }
}
