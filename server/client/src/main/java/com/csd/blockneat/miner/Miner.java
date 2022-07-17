package com.csd.blockneat.miner;

import com.csd.blockneat.application.crypto.SHA512;
import com.csd.blockneat.application.entities.Block;
import com.csd.blockneat.application.entities.ValidatedBlock;
import com.csd.blockneat.application.transactions.commands.LoadMoneyCommand;
import com.csd.blockneat.client.BlockNeatAPI;
import com.csd.blockneat.client.InternalUser;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.SignatureException;

public class Miner {

    private final BlockNeatAPI blockNeatAPI;
    private final InternalUser user;
    private Thread current;

    public Miner(BlockNeatAPI blockNeatAPI, InternalUser user) {
        this.blockNeatAPI = blockNeatAPI;
        this.user = user;
    }

    public void start() {
        if (current != null) {
            return;
        }
        current = new Thread(() -> {
            while (mineBlock());
        });
        current.start();
    }

    public boolean mineBlock() {
        try {
            Block nextBlock;
            do {
                try (var byteIn = new ByteArrayInputStream(blockNeatAPI.getNextBlock());
                     var objIn = new ObjectInputStream(byteIn)) {
                    nextBlock = (Block) objIn.readObject();
                    if (nextBlock == null) {
                        Thread.sleep(1000);
                    }
                }
            } while (nextBlock == null);

            ValidatedBlock validatedBlock = validateBlock(nextBlock);
            byte[] minedBlock;
            try (var byteOut = new ByteArrayOutputStream();
                 var objOut = new ObjectOutputStream(byteOut)) {
                objOut.writeObject(validatedBlock);
                objOut.flush();
                byteOut.flush();
                minedBlock = byteOut.toByteArray();
            }
            if(blockNeatAPI.proposeBlock(minedBlock))
                System.out.println("Block Mined.");
            return true;
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            return false;
        }
    }

    private ValidatedBlock validateBlock(Block nextBlock) {
        String accountId = "account11";
        int value = 1000;
        nextBlock.transactions().add(0, new LoadMoneyCommand(accountId, value, signReward(accountId, value), System.currentTimeMillis()));
        String hash;
        do {
            nextBlock = new Block(
                    nextBlock.id(),
                    nextBlock.nonce() + 1,
                    nextBlock.previousBlockHash(),
                    nextBlock.transactions()
            );
            hash = SHA512.hexHash(nextBlock.toString());
        } while (!hash.startsWith("000"));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new ValidatedBlock(nextBlock, hash, System.currentTimeMillis());
    }

    private String signReward(String accountId, int value) {
        try {
            return user.sign(accountId + value);
        } catch (SignatureException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (current != null) {
            current.interrupt();
            current = null;
        }
    }

    public void toggle() {
        if (current == null) {
            start();
        } else {
            stop();
        }
    }
}
