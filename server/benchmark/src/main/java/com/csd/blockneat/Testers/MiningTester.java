package com.csd.blockneat.Testers;

import com.csd.blockneat.client.BlockNeatAPI;
import com.csd.blockneat.miner.Miner;

import java.util.concurrent.Callable;

public class MiningTester extends Tester implements Callable<Object> {

    BlockNeatAPI client;
    long executionTime;

    public MiningTester(BlockNeatAPI client, int seconds) {
        this.client = client;
        executionTime = seconds * 1000L;
    }

    @Override
    public Object call() {
        System.out.println("Started Miner.");
        Miner miner = new Miner(client, client.getInternalUser());
//        System.out.println("Thread " + id + " has started");
        long startTime = System.currentTimeMillis();
        while (true) {
            miner.mineBlock();

            if (startTime + executionTime < System.currentTimeMillis())
                break;
        }
        System.out.println("Ended Miner.");
//        System.out.println("Thread " + id + " has ended");
        return null;
    }
}
