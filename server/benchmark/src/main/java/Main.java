
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

public class Main {

    private static InetSocketAddress parseSocketAddress(String socketAddress) {
        String[] split = socketAddress.split(":");
        String host = split[0];
        int port = Integer.parseInt(split[1]);
        return new InetSocketAddress(host, port);
    }


    public static void main(String[] args) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        if (args.length != 1) {
            System.out.println("bench <workload_file>");
            System.exit(1000);
        }
        // Load Configuration to execute benchmark
        InputStream workload = new FileInputStream(args[0]);
        Properties config = new Properties();
        config.load(workload);
        workload.close();

        // Load Threads PoW miners and IP
        int threads = Integer.parseInt(config.getProperty("threads"));
        int PoW = Integer.parseInt(config.getProperty("difficulty"));
        int miners = Integer.parseInt(config.getProperty("miners"));
        InetSocketAddress ip = parseSocketAddress(config.getProperty("ip"));
        int users = Integer.parseInt(config.getProperty("users"));
        String jks_password = config.getProperty("jks_password");
        String filename = config.getProperty("filename");

        BenchmarkManager bm = new BenchmarkManager(threads, PoW, miners, ip);
        bm.Benchmark();
    }
}
