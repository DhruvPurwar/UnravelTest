package cloud.unravel.com.log;

public class Consumer implements Runnable {
    private final LogProcessor processor;

    public Consumer(LogProcessor processor) {
        this.processor = processor;
    }

    public void run() {
        try {
            while (true) {
                LogTask task = processor.consumeLog();
                System.out.println("Consumed: " + task.getMessage() + " with priority " + task.getPriority());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}