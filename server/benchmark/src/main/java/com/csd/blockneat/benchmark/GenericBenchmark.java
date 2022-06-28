package com.csd.blockneat.benchmark;

import com.csd.blockneat.Testers.Tester;
import com.csd.blockneat.client.BlockNeatAPI;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class GenericBenchmark implements Benchmark {

    public static final long CONVERT_TO_MILLIS = 1000L;
    protected int threadNumber;
    protected List<BlockNeatAPI> clients;
    protected List<Tester> testers;
    protected ExecutorService executorService;

    protected long executionTime;

    GenericBenchmark(List<BlockNeatAPI> clients, int threadNumber, int seconds) {
        this.threadNumber = threadNumber;
        this.clients = clients;
        this.executorService = Executors.newFixedThreadPool(threadNumber);
        this.executionTime = seconds * CONVERT_TO_MILLIS;
    }

}
