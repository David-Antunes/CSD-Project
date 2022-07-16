package com.csd.blockneat.Testers;

import com.csd.blockneat.client.BlockNeatAPI;
import com.csd.blockneat.workload.Fill;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

public class OperationTester extends Tester implements Callable<Object> {

    private final BlockNeatAPI client;
    private final int userNumber;

    private final SortedMap<Long, Long> writeLatency;
    private final SortedMap<Long, Long> readLatency;

    private final float readPercentage;

    private final long executionTime;
    private final String id;

    public OperationTester(BlockNeatAPI client, int userNumber, float readPercentage, long executionTime) {
        this.client = client;
        this.userNumber = userNumber;
        this.readPercentage = readPercentage;
        this.writeLatency = new TreeMap<>();
        this.readLatency = new TreeMap<>();
        this.executionTime = executionTime;
        this.id = client.getInternalUser().getUsername().split("user")[1];
    }

    public SortedMap<Long, Long> getReadLatency() {
        return readLatency;
    }

    public SortedMap<Long, Long> getWriteLatency() {
        return writeLatency;
    }

    @Override
    public Object call() {
//        System.out.println("Thread " + id + " has started");
        long startTime = System.currentTimeMillis();
        long response = 0;
        while (true) {

            float nextOperation = ThreadLocalRandom.current().nextFloat();
            if (nextOperation < readPercentage) {
                response = readOperation();
                if(response != -1)
                    readLatency.put(System.currentTimeMillis(), readOperation());
            } else {
                response = writeOperation();
                if(response != -1)
                    writeLatency.put(System.currentTimeMillis(), writeOperation());
            }
            if (startTime + executionTime < System.currentTimeMillis())
                break;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
//        System.out.println("Thread " + id + " has ended");
        return null;
    }

    private long readOperation() {
        int randomAccount = ThreadLocalRandom.current().nextInt(0, Fill.ACCOUNT_NUMBER);
        int randUser = ThreadLocalRandom.current().nextInt(0, userNumber);
        try {
            long startOperation = System.currentTimeMillis();
            client.getBalance(Fill.ACCOUNT_ID + randUser + randomAccount);
            long endOperation = System.currentTimeMillis();
            return endOperation - startOperation;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private long writeOperation() {
        int randomAccount = ThreadLocalRandom.current().nextInt(0, Fill.ACCOUNT_NUMBER);
        int randomUserDestination = ThreadLocalRandom.current().nextInt(0, userNumber);
        int randomAccountDestination = ThreadLocalRandom.current().nextInt(0, Fill.ACCOUNT_NUMBER);
        int value = ThreadLocalRandom.current().nextInt(0, 100);
        try {
            long startOperation = System.currentTimeMillis();
            int response = client.sendTransactionWithResult(Fill.ACCOUNT_ID + id + randomAccount, Fill.ACCOUNT_ID + randomUserDestination + randomAccountDestination, value);
            long endOperation = System.currentTimeMillis();
            if(response == 200 || response == 408)
                    return endOperation - startOperation;
            else
                return -1;
        } catch (SignatureException | InterruptedException | IOException | InvalidKeyException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
