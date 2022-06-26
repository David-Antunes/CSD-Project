import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class Miner implements Runnable {
    User user;
    public static AtomicInteger blocksMined;
    InetSocketAddress ip;

    private int blocksToMine;

    public Miner(User user, InetSocketAddress ip) {
        this.user = user;
        this.ip = ip;
    }

    public void setBlocksToMine(int blocks) {
        this.blocksToMine = blocks;
    }

    public void mineGenesisBlock() {

    }

    public int getBlocksToMine() {
        return blocksToMine;
    }

    public int getBlocksMined() {
        return blocksMined.get();
    }

    private void requestBlock() {

    }

    private void proposeBlock() {

    }

    private void mineBlock() {

    }

    @Override
    public void run() {
        while (true) {
            requestBlock();
            mineBlock();
            blocksMined.incrementAndGet();
            proposeBlock();
            if(blocksToMine > 0 && blocksToMine <= blocksMined.get())
                break;
        }
    }
}
