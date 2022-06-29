package com.csd.blockneat.benchmark;

import com.csd.blockneat.Testers.MiningTester;
import com.csd.blockneat.Testers.OperationTester;
import com.csd.blockneat.client.BlockNeatAPI;
import com.csd.blockneat.rest.requests.BlockneatStatistic;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class MiningBenchmark extends GenericBenchmark implements Benchmark {

    private final int miners;
    private final int seconds;
    private float operationThroughput;


    private float avgTransactionLatency;

    private List<Long> transactionLatency;

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
    }

    @Override
    public void processStatistics() {
        BlockneatStatistic response = null;
        try {
            response = clients.get(0).getBlockneatStatistic();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (response == null)
            throw new RuntimeException();

        mineBlockLatency = response.minedBlockLatency();
        transactionLatency = response.transactionLatency();
        avgMinedBlock = response.avgMinedBlock();
        avgTransactionLatency = response.avgTransactionLatency();
        operationThroughput = response.transactions() != 0 ? (float) response.transactions() / seconds : 0.0f;
    }

    public void writeResultsToFile(String extension) {
        try {
            if (!Files.exists(Paths.get("results")))
                Files.createDirectory(Paths.get("results"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileWriter myWriter = new FileWriter("results/statistics-" + extension + ".csv")) {
            myWriter.write("seconds,");
            myWriter.write("transactions,");
            myWriter.write("threads,");
            myWriter.write("blocksMined,");
            myWriter.write("avgMinedBlock,");
            myWriter.write("avgTransactionBlock,");
            myWriter.write("operationThroughput\n");
            myWriter.write(seconds + ",");
            myWriter.write(transactionLatency.size() + ",");
            myWriter.write(threadNumber + ",");
            myWriter.write((mineBlockLatency.size() + 1) + ",");
            myWriter.write(avgMinedBlock + ",");
            myWriter.write(avgTransactionLatency + ",");
            myWriter.write(operationThroughput + "\n");
            myWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileWriter myWriter = new FileWriter("results/transactionLatency-" + extension + ".txt")) {
            for (long value : transactionLatency)
                myWriter.write(value + "\n");
            myWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileWriter myWriter = new FileWriter("results/mineBlockLatency-" + extension + ".txt")) {
            for (long value : mineBlockLatency)
                myWriter.write(value + "\n");
            myWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
