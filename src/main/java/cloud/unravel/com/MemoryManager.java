package cloud.unravel.com;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MemoryManager {
    // Use a ConcurrentHashMap for storing session data directly
    private static final Map<String, byte[]> sessionDataMap = new ConcurrentHashMap<>();

    // Use Caffeine for caching session data with eviction policies
    private static final Cache<String, byte[]> sessionCache = Caffeine.newBuilder()
            .maximumSize(100) // Maximum number of sessions to cache
            .expireAfterAccess(10, TimeUnit.MINUTES) // Expire entries after 10 minutes of inactivity
            .build();

    /**
     * Adds session data for a given session ID.
     *
     * @param sessionId The session ID to add data for.
     */
    public static void addSessionData(String sessionId) {
        byte[] data = new byte[10 * 1024 * 1024]; // 10MB of data per session
        sessionDataMap.put(sessionId, data); // Store in ConcurrentHashMap
        sessionCache.put(sessionId, data);   // Cache using Caffeine
    }

    /**
     * Removes session data for a given session ID.
     *
     * @param sessionId The session ID to remove data for.
     */
    public static void removeSessionData(String sessionId) {
        sessionDataMap.remove(sessionId);
        sessionCache.invalidate(sessionId);
    }

    /**
     * Retrieves session data for a given session ID.
     * First checks the cache, then falls back to the map if necessary.
     *
     * @param sessionId The session ID to retrieve data for.
     * @return The session data or null if not present.
     */
    public static byte[] getSessionData(String sessionId) {
        // Attempt to get the data from the cache first
        byte[] cachedData = sessionCache.getIfPresent(sessionId);
        if (cachedData != null) {
            return cachedData;
        }

        // Fall back to the map if not present in the cache
        byte[] data = sessionDataMap.get(sessionId);
        if (data != null) {
            sessionCache.put(sessionId, data); // Cache it for future access
        }
        return data;
    }
}