package com.csd.blockneat.benchmark;

import com.csd.blockneat.client.BlockNeatAPI;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class FullBenchmark implements Benchmark {

    Benchmark miningBenchmark;

    Benchmark operationBenchmark;

    protected ExecutorService executorService;

    public FullBenchmark(List<BlockNeatAPI> clients, int threads, float readPercentage, int seconds, int miners) {
        this.miningBenchmark = new MiningBenchmark(clients, 1, miners, seconds);
        this.operationBenchmark = new OperationBenchmark(clients, threads, readPercentage, seconds);
        this.executorService = Executors.newFixedThreadPool(2);
    }


    @Override
    public void benchmark() {
        List<Callable<Object>> benchmarks = new LinkedList<>();
        benchmarks.add(() -> {
            miningBenchmark.benchmark();
            return null;
        });

        benchmarks.add(() -> {
            operationBenchmark.benchmark();
            return null;
        });

        try {
            executorService.invokeAll(IntStream.range(0, 2)
                    .mapToObj(benchmarks::get)
                    .toList());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processStatistics() {
        miningBenchmark.processStatistics();
        operationBenchmark.processStatistics();
    }

    @Override
    public void writeResultsToFile(String extension) {
        miningBenchmark.writeResultsToFile(extension);
        operationBenchmark.writeResultsToFile(extension);
    }

    public Benchmark getMiningBenchmark() {
        return miningBenchmark;
    }

    public Benchmark getOperationBenchmark() {
        return operationBenchmark;
    }

}
