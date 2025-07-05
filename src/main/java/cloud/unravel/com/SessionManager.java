package cloud.unravel.com;
import java.util.concurrent.ConcurrentHashMap;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Map;
import java.util.UUID;

import java.util.concurrent.TimeUnit;

/*
We can also use ReadWriteLock lock = new ReentrantReadWriteLock();
But this wont help in distributed microservices .
The current redis based lock will store the state at centralised location via which other microservices
can communicate using kafka in event driven approach.This can further help with scalability.
 */

public class SessionManager {
    private Map<String, String> sessions = new
            ConcurrentHashMap<>();

    private final RedissonClient redissonClient;

    public SessionManager() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        this.redissonClient = Redisson.create(config);
    }


    public String logout(String userId) {
        // Redis lock for handling distributed microservices
        RLock lock = redissonClient.getLock("session-lock-" + userId);
        try{
            if(lock.tryLock(5, TimeUnit.SECONDS)){
                try{
                    if (!sessions.containsKey(userId)) {
                        return "User not logged in.";
                    }
                    sessions.remove(userId);
                    return "Logout successful.";
                }finally {
                    lock.unlock();
                }
            }else{
                return "Could not acquire lock for login.";
            }
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Logout process interrupted.";
        }
    }


    public String login(String userId) {
        //  Redis lock for handling distributed microservices
        RLock lock = redissonClient.getLock("session-lock-" + userId);
        try {
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                try {
                    if (sessions.containsKey(userId)) {
                        return "User already logged in.";
                    }
                    String sessionId = "SESSION_" + UUID.randomUUID().toString();
                    sessions.put(userId, sessionId);
                    return "Login successful. Session ID: " + sessionId;
                } finally {
                    lock.unlock();
                }
            } else {
                return "Could not acquire lock for login.";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Login process interrupted.";
        }
    }

    public String getSessionDetails(String userId) {
        // Read operation: typically doesn't require a lock
        if (!sessions.containsKey(userId)) {
            throw new IllegalArgumentException("Session not found for user " + userId);
        }
        return "Session ID for user " + userId + ": " + sessions.get(userId);
    }
}