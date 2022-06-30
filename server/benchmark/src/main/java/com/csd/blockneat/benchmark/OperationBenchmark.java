package com.csd.blockneat.benchmark;

import com.csd.blockneat.Testers.OperationTester;
import com.csd.blockneat.Testers.Tester;
import com.csd.blockneat.client.BlockNeatAPI;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class OperationBenchmark extends GenericBenchmark implements Benchmark {

    private final float readPercentage;
    private float avgWrites;
    private float avgReads;
    private final SortedMap<Long, Long> writes;
    private final SortedMap<Long, Long> reads;
    private float operationThroughput;
    private final int seconds;


    public OperationBenchmark(List<BlockNeatAPI> clients, int threads, float readPercentage, int seconds) {
        super(clients, threads, seconds);
        this.readPercentage = readPercentage;
        this.writes = new TreeMap<>();
        this.reads = new TreeMap<>();
        avgReads = 0.0f;
        avgWrites = 0.0f;
        operationThroughput = 0.0f;
        this.seconds = seconds;
    }

    public SortedMap<Long, Long> getWrites() {
        return writes;
    }

    public SortedMap<Long, Long> getReads() {
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
            writes.putAll(op.getWriteLatency());
            reads.putAll(op.getReadLatency());
        }
        long writeLatency = 0;
        for (Long value : writes.values()) {
            writeLatency += value;
        }

        long readLatency = 0;
        for (Long value : reads.values()) {
            readLatency += value;
        }

        avgWrites = writes.size() > 0 ? (float) writeLatency / writes.size() : 0;
        avgReads = reads.size() > 0 ? (float) readLatency / reads.size() : 0;
        operationThroughput = (float) (reads.size() + writes.size()) / seconds;
    }

    public void writeResultsToFile(String extension) {
        try {
            if(!Files.exists(Paths.get("results")))
                Files.createDirectory(Paths.get("results"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try(FileWriter myWriter = new FileWriter("results/statistics-" + extension + ".csv")) {
            myWriter.write("seconds,");
            myWriter.write("threads,");
            myWriter.write("reads,");
            myWriter.write("writes,");
            myWriter.write("readPercentage,");
            myWriter.write("avgReads,");
            myWriter.write("avgWrites,");
            myWriter.write("operationThroughput\n");

            myWriter.write(seconds + ",");
            myWriter.write(threadNumber + ",");
            myWriter.write(reads.size() + ",");
            myWriter.write(writes.size() + ",");
            myWriter.write(readPercentage + ",");
            myWriter.write(avgReads + ",");
            myWriter.write(avgWrites + ",");
            myWriter.write(operationThroughput + "\n");
            myWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try(FileWriter myWriter = new FileWriter("results/reads-" + extension + ".txt")) {
            for(long value: reads.values())
                myWriter.write(value + "\n");
            myWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try(FileWriter myWriter = new FileWriter("results/writes-" + extension + ".txt")) {
            for(long value: writes.values())
                myWriter.write(value + "\n");
            myWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
