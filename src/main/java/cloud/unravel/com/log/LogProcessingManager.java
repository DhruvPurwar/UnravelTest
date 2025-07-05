package cloud.unravel.com.log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogProcessingManager {

    private final LogProcessor processor;
    private final ExecutorService consumerPool;
    private final Producer producer;

    public LogProcessingManager(int consumerCount) {
        this.processor = new LogProcessor();
        this.consumerPool = Executors.newFixedThreadPool(consumerCount);
        this.producer = new Producer(processor);
    }

    public void start() {
        // Start producer
        producer.start();

        // Start consumers
        for (int i = 0; i < consumerPool.shutdownNow().size(); i++) {
            consumerPool.execute(new Consumer(processor));
        }
    }

    public void stop() {
        // Gracefully shut down the consumer pool
        consumerPool.shutdownNow();
    }

    public static void main(String[] args) {
        LogProcessingManager manager = new LogProcessingManager(2);

        // Add shutdown hook to ensure proper shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(manager::stop));

        manager.start();
    }
}
