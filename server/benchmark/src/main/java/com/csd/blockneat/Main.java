package com.csd.blockneat;

import com.csd.blockneat.benchmark.Benchmark;
import com.csd.blockneat.benchmark.MiningBenchmark;
import com.csd.blockneat.benchmark.OperationBenchmark;
import com.csd.blockneat.client.BlockNeatAPI;
import com.csd.blockneat.workload.Fill;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, SignatureException, InvalidKeyException, InterruptedException {

        String benchmarkConfigurationFile = null;
        System.getProperties().setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
        if (args.length > 1) {
            System.out.println("Too many Arguments");
            System.exit(1);
        }
        if (args.length == 1) {
            benchmarkConfigurationFile = args[0];
        }
        if (System.getenv("BENCHMARK_PATH") != null)
            benchmarkConfigurationFile = System.getenv("BENCHMARK_PATH");

        if (benchmarkConfigurationFile == null) {
            System.out.println("No configuration file provided.");
            System.exit(1);
        }

        InputStream workload = new FileInputStream(benchmarkConfigurationFile);
        Properties config = new Properties();
        config.load(workload);
        workload.close();

        String operation = config.getProperty("operation");
        int threads;
        int miners = 0;
        float readPercentage = 0.0f;
        String url;
        int userNumber = 0;
        String userKeyStoreFile = "";
        String userKeyStorePassword = "";
        int seconds = 0;

        if (operation.equals("api")) {
            readPercentage = Float.parseFloat(config.getProperty("readPercentage"));

        } else if (operation.equals("mining")) {
            miners = Integer.parseInt(config.getProperty("minerNumber"));
        }
        userNumber = Integer.parseInt(config.getProperty("userNumber"));
        userKeyStoreFile = config.getProperty("userKeyStoreFile");
        userKeyStorePassword = config.getProperty("userKeyStorePassword");
        threads = Integer.parseInt(config.getProperty("threads"));
        url = config.getProperty("url");
        seconds = Integer.parseInt(config.getProperty("seconds"));
        String extension = config.getProperty("output_file");
        List<BlockNeatAPI> clients = Fill.LoadUsers(url, userKeyStoreFile, userKeyStorePassword, "user", userNumber);
        Fill.preLoadBlockNeat(clients, clients.size());
        if (operation.equals("api")) {
            OperationBenchmark bm = new OperationBenchmark(clients, threads, readPercentage, seconds);
            bm.benchmark();
            bm.processStatistics();
            processOperationStatistics(bm);
            bm.writeResultsToFile(extension);
        } else if (operation.equals("mining")) {
            MiningBenchmark bm = new MiningBenchmark(clients, threads, miners, seconds);
            bm.benchmark();
            bm.processStatistics();
            processMiningStatistics(bm);
            bm.writeResultsToFile(extension);
        }

        System.exit(0);
    }

    private static void processOperationStatistics(OperationBenchmark bm) {
        System.out.println("========================================");
        System.out.println();
        System.out.println("Writes:");
        for(Long value: bm.getWrites())
            System.out.println(value);

        System.out.println();
        System.out.println("Reads:");
        for(Long value: bm.getReads())
            System.out.println(value);

        System.out.println();
        System.out.println();
        System.out.println("Write Operations: " + bm.getWrites().size());
        System.out.println("Avg Write Latency (ms): " + bm.getAvgWrites());
        System.out.println("Read Operations: " + bm.getReads().size());
        System.out.println("Avg Read Latency (ms): " + bm.getAvgReads());
        System.out.println("Operation Throughput (Tx/s): " + bm.getOperationThroughput());
        System.out.println("Execution time: " + bm.getSeconds() + " s");
    }

    private static void processMiningStatistics(MiningBenchmark bm) {
        System.out.println("========================================");
        System.out.println();
        System.out.println("PoW:");
        for(Long value: bm.getMineBlockLatency())
            System.out.println(value);

        System.out.println();
        System.out.println();
        System.out.println("Blocks mined: " + bm.getBlocksMined());
        System.out.println("Avg PoW (ms): " + bm.getAvgMinedBlock());
        System.out.println("Transaction Operations: " + bm.getTransactionLatency().size());
        System.out.println("Avg Transaction Latency (ms): " + bm.getAvgTransactionLatency());
        System.out.println("Operation Throughput (Tx/s): " + bm.getOperationThroughput());
        System.out.println("Execution time: " + bm.getSeconds() + " s");
    }
}
