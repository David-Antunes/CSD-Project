package com.csd.blockneat;

import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.csd.blockneat.Testers.OperationTester;
import com.csd.blockneat.benchmark.Benchmark;
import com.csd.blockneat.benchmark.FullBenchmark;
import com.csd.blockneat.benchmark.MiningBenchmark;
import com.csd.blockneat.benchmark.OperationBenchmark;
import com.csd.blockneat.client.BlockNeatAPI;
import com.csd.blockneat.workload.Fill;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class Main {

    public static void main(String[] args) throws Exception {

        String benchmarkConfigurationFile = null;
        System.getProperties().setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());

        if (args.length > 2) {
            System.out.println("Too many Arguments");
            System.exit(1);
        }
        if (args.length == 2) {
            benchmarkConfigurationFile = args[1];
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

        switch (operation) {
            case "api" -> readPercentage = Float.parseFloat(config.getProperty("readPercentage"));
            case "mining" -> miners = Integer.parseInt(config.getProperty("minerNumber"));
            case "full" -> {
                    miners = Integer.parseInt(config.getProperty("minerNumber"));
                readPercentage = Float.parseFloat(config.getProperty("readPercentage"));
            }
            default -> {
                System.out.println("Invalid operation.");
                System.exit(1);
            }
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


        if (System.getenv("PROCESS_BLOCKNEAT") != null) {
            MiningBenchmark bm = new MiningBenchmark(clients, 10, 0, 0);
            bm.processStatistics();
            processMiningStatistics(bm);
            System.exit(0);
        }

        System.out.println("Starting benchmark...");
        if (operation.equals("api")) {
            OperationBenchmark bm = new OperationBenchmark(clients, threads, readPercentage, seconds);
            bm.benchmark();
            bm.processStatistics();
            processOperationStatistics(bm);
            bm.writeResultsToFile(extension + "-" + UUID.randomUUID());
        } else if (operation.equals("mining")) {
            MiningBenchmark bm = new MiningBenchmark(clients, threads, miners, seconds);
            bm.benchmark();
            bm.processStatistics();
            processMiningStatistics(bm);
            bm.writeResultsToFile(extension + "-" + UUID.randomUUID());
        } else {
            FullBenchmark fb = new FullBenchmark(clients,threads,readPercentage, seconds, miners);
            fb.benchmark();
            fb.processStatistics();
            processFullStatistics(fb);
            String fileID = extension + "-" + UUID.randomUUID();
//            fb.writeResultsToFile(fileID);
            generateExcel(fb, fileID);

        }
        System.out.println("Benchmark done.");
        System.exit(0);
    }

    private static void processOperationStatistics(OperationBenchmark bm) {
        System.out.println("========================================");
        System.out.println();
        System.out.println("Writes:");
        for (Long value : bm.getWrites().values())
            System.out.println(value);

        System.out.println();
        System.out.println("Reads:");
        for (Long value : bm.getReads().values())
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
        for (Long value : bm.getMineBlockLatency())
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

    private static void processFullStatistics(FullBenchmark fb) {
        OperationBenchmark bm = (OperationBenchmark) fb.getOperationBenchmark();
        MiningBenchmark mb = (MiningBenchmark) fb.getMiningBenchmark();
        System.out.println("================OPERATION==================");
        System.out.println();
        System.out.println("Write Operations: " + bm.getWrites().size());
        System.out.println("Avg Write Latency (ms): " + bm.getAvgWrites());
        System.out.println("Read Operations: " + bm.getReads().size());
        System.out.println("Avg Read Latency (ms): " + bm.getAvgReads());
        System.out.println("Operation Throughput (Tx/s): " + bm.getOperationThroughput());
        System.out.println("Execution time: " + bm.getSeconds() + " s");
        System.out.println();
        System.out.println("===========================================");
        System.out.println("==================MINING===================");
        System.out.println();
        System.out.println("Blocks mined: " + mb.getBlocksMined());
        System.out.println("Avg PoW (ms): " + mb.getAvgMinedBlock());
        System.out.println("Transaction Operations: " + mb.getTransactionLatency().size());
        System.out.println("Avg Transaction Latency (ms): " + mb.getAvgTransactionLatency());
        System.out.println("Operation Throughput (Tx/s): " + mb.getOperationThroughput());
        System.out.println("Execution time: " + mb.getExecutionTime() + " s");
        System.out.println();
        System.out.println("===========================================");
    }

    private static void generateExcel(FullBenchmark fb, String extension) throws Exception {
        OperationBenchmark bm = (OperationBenchmark) fb.getOperationBenchmark();
        MiningBenchmark mb = (MiningBenchmark) fb.getMiningBenchmark();
        Workbook workbook = new Workbook();
        Worksheet worksheet = workbook.getWorksheets().get(0);

        String[][] data = {
                {"Write Operations", String.valueOf(bm.getWrites().size())},
                {"Avg Write Latency (ms)", String.valueOf(bm.getAvgWrites())},
                {"Read Operations" , String.valueOf(bm.getReads().size())},
                {"Avg Transaction Latency (ms)", String.valueOf(bm.getAvgReads())},
                {"Operation Throughput (Tx/s)", String.valueOf(bm.getOperationThroughput())},
                {"Execution time (s)", String.valueOf(bm.getSeconds())},
                {"Blocks mined", String.valueOf(mb.getBlocksMined())},
                {"Avg PoW (ms)", String.valueOf(mb.getAvgMinedBlock())},
                {"Transaction Operations", String.valueOf(mb.getTransactionLatency().size())},
                {"Avg Transaction Latency (ms)", String.valueOf(mb.getAvgTransactionLatency())},
                {"Operation Throughput (Tx/s)", String.valueOf(mb.getOperationThroughput())}
        };
        worksheet.getCells().importArray(data, 0, 0);
        int size = Math.max(bm.getWrites().size(), bm.getReads().size());
        String[][] RWs = new String[size][2];

        RWs[0] = new String[]{"Reads", "Writes"};
        Iterator<Long> reads = bm.getReads().values().iterator();
        Iterator<Long> writes = bm.getWrites().values().iterator();
        for(int i = 1; i < size; i++) {
            RWs[i] = new String[] {
                    reads.hasNext() ? String.valueOf(reads.next()) : "",
                    writes.hasNext() ? String.valueOf(writes.next()) : ""
            };
        }

        worksheet.getCells().importArray(RWs, 0, 3);
        workbook.save("results/workload-" + extension + ".xlsx");
    }
}
