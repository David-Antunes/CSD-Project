import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BenchmarkManager {

    public static final int INITIAL_ACCOUNT_NUMBER = 10;
    public static final String ACCOUNT_IDENTIFIER = "account";
    User user;
    int threads;
    int PoW;
    int miners;
    int time;
    InetSocketAddress ip;

    public BenchmarkManager(int threads, int PoW, int miners, InetSocketAddress ip) throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, NoSuchProviderException {
        this.threads = threads;
        this.PoW = PoW;
        this.miners = miners;
        KeyPair kp = ECDSASignature.fetchKeyPair("client/users.jks", "user1", "user1", "user1");
        this.user = new User(new ECDSASignature(kp));
        this.ip = ip;
    }
    private void mineGenesisBlock() {
        Miner mine = new Miner(user, ip);
        mine.run();
    }

    public void Benchmark() {
        mineGenesisBlock();
        //Generate transactions
        user.run();
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        Miner miner = new Miner(user,ip);
        executorService.execute(miner);
        while(!executorService.isTerminated());
    }

    public void processStatistics() {

    }
}
