package com.csd.blockneat.benchmark;

public interface Benchmark {

    void benchmark();

    void processStatistics();

    void writeResultsToFile(String extension);

}
