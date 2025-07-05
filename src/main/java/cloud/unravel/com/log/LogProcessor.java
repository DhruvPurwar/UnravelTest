package cloud.unravel.com.log;



import java.util.concurrent.PriorityBlockingQueue;

public class LogProcessor {
    private final PriorityBlockingQueue<LogTask> logQueue = new PriorityBlockingQueue<>();

    public void produceLog(String log, int priority) {
        logQueue.put(new LogTask(log, priority));
    }

    public LogTask consumeLog() throws InterruptedException {
        return logQueue.take(); // Blocking call
    }
}
