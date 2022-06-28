package com.csd.blockneat.benchmark;

import com.csd.blockneat.Testers.OperationTester;
import com.csd.blockneat.Testers.Tester;
import com.csd.blockneat.client.BlockNeatAPI;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class OperationBenchmark extends GenericBenchmark implements Benchmark {

    private final float readPercentage;
    private float avgWrites;
    private float avgReads;
    private List<Long> writes;
    private List<Long> reads;
    private float operationThroughput;
    private int seconds;


    public OperationBenchmark(List<BlockNeatAPI> clients, int threads, float readPercentage, int seconds) {
        super(clients, threads, seconds);
        this.readPercentage = readPercentage;
        this.writes = new LinkedList<>();
        this.reads = new LinkedList<>();
        avgReads = 0.0f;
        avgWrites = 0.0f;
        operationThroughput = 0.0f;
        this.seconds = seconds;
    }

    public List<Long> getWrites() {
        return writes;
    }

    public List<Long> getReads() {
        return reads;
    }

    public float getAvgReads() {
        return avgReads;
    }

    public float getAvgWrites() {
        return avgWrites;
    }

    public float getOperationThroughput() {
        return operationThroughput;
    }

    public int getSeconds() {
        return seconds;
    }

    public float getReadPercentage() {
        return readPercentage;
    }

    @Override
    public void benchmark() {

        for (int i = 0; i < super.threadNumber; i++) {
            int id = ThreadLocalRandom.current().nextInt(0, clients.size());
            testers.add(new OperationTester(clients.get(id), clients.size(), readPercentage, super.executionTime));
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

        for (Tester tester : testers) {
            OperationTester op = (OperationTester) tester;
            writes.addAll(op.getWriteLatency());
            reads.addAll(op.getReadLatency());
        }
        long writeLatency = 0;
        for (Long value : writes) {
            System.out.println(value);
            writeLatency += value;
        }

        long readLatency = 0;
        for (Long value : reads) {
            System.out.println(value);
            readLatency += value;
        }

        avgWrites = writes.size() > 0 ? (float) writeLatency / writes.size() : 0;
        avgReads = reads.size() > 0 ? (float) readLatency / reads.size() : 0;
        operationThroughput = (float) (reads.size() + writes.size()) / seconds;
    }
}
