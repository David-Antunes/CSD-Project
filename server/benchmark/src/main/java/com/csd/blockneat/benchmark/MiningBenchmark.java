package com.csd.blockneat.benchmark;

import com.csd.blockneat.Testers.MiningTester;
import com.csd.blockneat.Testers.OperationTester;
import com.csd.blockneat.client.BlockNeatAPI;
import com.csd.blockneat.workload.Fill;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class MiningBenchmark extends GenericBenchmark implements Benchmark {

    private int miners;
    private int seconds;
    private int blocksToMine;
    private float operationThroughput;

    private List<Long> writes;

    public MiningBenchmark(List<BlockNeatAPI> clients, int threads, int miners, int seconds) {
        super(clients, threads, seconds);
    }

    @Override
    public void benchmark() {

        for (int i = 0; i < threadNumber - miners; i++) {
            int id = ThreadLocalRandom.current().nextInt(0, clients.size());
            testers.add(new OperationTester(clients.get(id), clients.size(), 0.0f, seconds));
        }

        for(int i = 0; i < miners; i++) {
            testers.add(new MiningTester(clients.get(1), seconds));
        }

        try {
            executorService.invokeAll(IntStream.range(0, threadNumber)
                    .mapToObj(testers::get)
                    .toList());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processStatistics() {

    }
}
