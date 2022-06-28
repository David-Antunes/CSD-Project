package com.csd.blockneat.benchmark;

import com.csd.blockneat.Testers.MiningTester;
import com.csd.blockneat.Testers.OperationTester;
import com.csd.blockneat.application.commands.WriteCommand;
import com.csd.blockneat.application.entities.Block;
import com.csd.blockneat.application.entities.ValidatedBlock;
import com.csd.blockneat.application.transactions.commands.LoadMoneyCommand;
import com.csd.blockneat.application.transactions.commands.SendTransactionCommand;
import com.csd.blockneat.client.BlockNeatAPI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class MiningBenchmark extends GenericBenchmark implements Benchmark {

    private final int miners;
    private final int seconds;
    private float operationThroughput;


    private float avgTransactionLatency;

    private final List<Long> transactionLatency;

    private List<Long> mineBlockLatency;

    private float avgMinedBlock;


    public MiningBenchmark(List<BlockNeatAPI> clients, int threads, int miners, int seconds) {
        super(clients, threads, seconds);
        this.miners = miners;
        this.seconds = seconds;
        this.operationThroughput = 0.0f;
        this.avgMinedBlock = 0.0f;
        this.avgTransactionLatency = 0.0f;
        this.transactionLatency = new LinkedList<>();
        this.mineBlockLatency = new LinkedList<>();
    }

    public int getSeconds() {
        return seconds;
    }

    public float getOperationThroughput() {
        return operationThroughput;
    }

    public float getAvgMinedBlock() {
        return avgMinedBlock;
    }

    public float getAvgTransactionLatency() {
        return avgTransactionLatency;
    }

    public int getMiners() {
        return miners;
    }

    public List<Long> getMineBlockLatency() {
        return mineBlockLatency;
    }

    public int getBlocksMined() {
        return mineBlockLatency.size() + 1;
    }

    public List<Long> getTransactionLatency() {
        return transactionLatency;
    }

    @Override
    public void benchmark() {

        for (int i = 0; i < super.threadNumber - miners; i++) {
            int id = ThreadLocalRandom.current().nextInt(0, clients.size());
            testers.add(new OperationTester(clients.get(id), clients.size(), 0.0f, super.executionTime));
        }

        for (int i = 0; i < miners; i++) {
            testers.add(new MiningTester(clients.get(1), super.executionTime));
        }

        try {
            executorService.invokeAll(IntStream.range(0, super.threadNumber)
                    .mapToObj(testers::get)
                    .toList());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while(!executorService.isShutdown());

    }

    @Override
    public void processStatistics() {
        String response = "";
        List<ValidatedBlock> blockneat = null;
        try {
            response = clients.get(0).getLedger();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        ObjectMapper om = new ObjectMapper();
        try {
            blockneat = om.readValue(response, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        blockneat = blockneat.subList(1, blockneat.size());
        int transactions = 0;
        for (ValidatedBlock vBlock : blockneat) {
            long blockTimestamp = vBlock.timestamp();
            mineBlockLatency.add(blockTimestamp);
            Block block = vBlock.block();
            transactions += block.transactions().size();
            for (WriteCommand wc : block.transactions()) {
                if (wc instanceof LoadMoneyCommand loadMoneyCommand) {
                    transactionLatency.add(vBlock.timestamp() - loadMoneyCommand.timestamp());
                } else if (wc instanceof SendTransactionCommand sendTransactionCommand)
                    transactionLatency.add(vBlock.timestamp() - sendTransactionCommand.timestamp());
            }
        }

        for (Long value : transactionLatency)
            avgTransactionLatency += value;
        avgTransactionLatency = avgTransactionLatency / transactions;


        List<Long> timeTillNextBlock = new LinkedList<>();
        for (int i = 0; i < mineBlockLatency.size() - 1; i++) {
             timeTillNextBlock.add(mineBlockLatency.get(i+1) - mineBlockLatency.get(i));
        }
        mineBlockLatency = timeTillNextBlock;
        for (Long aLong : timeTillNextBlock) {
            avgMinedBlock += aLong;
        }
        avgMinedBlock = avgMinedBlock / timeTillNextBlock.size();

        operationThroughput = (float) transactions / seconds;
    }
}
