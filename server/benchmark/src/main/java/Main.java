import com.csd.blockneat.benchmark.GenericBenchmark;
import com.csd.blockneat.benchmark.MiningBenchmark;
import com.csd.blockneat.benchmark.OperationBenchmark;
import com.csd.blockneat.client.BlockNeatAPI;
import com.csd.blockneat.workload.Fill;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Properties;

public class Main {

    private static InetSocketAddress parseSocketAddress(String socketAddress) {
        String[] split = socketAddress.split(":");
        String host = split[0];
        int port = Integer.parseInt(split[1]);
        return new InetSocketAddress(host, port);
    }


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
            readPercentage = Float.parseFloat(config.getProperty("reads"));
            userNumber = Integer.parseInt(config.getProperty("userNumber"));

        } else if (operation.equals("mining")) {
            miners = Integer.parseInt(config.getProperty("minerNumber"));
        }

        userKeyStoreFile = config.getProperty("userKeyStoreFile");
        userKeyStorePassword = config.getProperty("userKeyStorePassword");
        threads = Integer.parseInt(config.getProperty("threads"));
        url = config.getProperty("url");
        seconds = Integer.parseInt(config.getProperty("seconds"));

        List<BlockNeatAPI> clients = Fill.LoadUsers(url, userKeyStoreFile, userKeyStorePassword, "user", userNumber);
        Fill.preLoadBlockNeat(clients, clients.size());
        GenericBenchmark bm = null;
        if (operation.equals("api")) {
            bm = new OperationBenchmark(clients, threads, readPercentage, seconds);
        } else if (operation.equals("mining")) {
            bm = new MiningBenchmark(clients, threads, miners, seconds);
        }
        assert bm != null;
        bm.benchmark();
        bm.processStatistics();
        System.exit(0);
    }
}
