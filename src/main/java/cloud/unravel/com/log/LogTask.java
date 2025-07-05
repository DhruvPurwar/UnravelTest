package cloud.unravel.com.log;


public class LogTask implements Comparable<LogTask> {
    private final String message;
    private final int priority;

    public LogTask(String message, int priority) {
        this.message = message;
        this.priority = priority;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int compareTo(LogTask other) {
        return Integer.compare(other.priority, this.priority); // Higher priority first
    }

    public int getPriority() {
        return priority;
    }
}