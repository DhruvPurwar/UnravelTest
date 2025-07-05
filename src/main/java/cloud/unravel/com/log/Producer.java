package cloud.unravel.com.log;



public class Producer extends Thread {
    private final LogProcessor processor;

    public Producer(LogProcessor processor) {
        this.processor = processor;
    }

    public void run() {
        for (int i = 0; i < 100; i++) {
            int priority = (i % 5 == 0) ? 10 : 1; // Assign higher priority to every 5th log
            processor.produceLog("Log " + i, priority);
        }
    }
}