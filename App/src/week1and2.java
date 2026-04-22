import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class week1and2 {

    // Token Bucket Class
    static class TokenBucket {
        private final int maxTokens;
        private final double refillRatePerSec;

        private double tokens;
        private long lastRefillTime;

        public TokenBucket(int maxTokens, double refillRatePerSec) {
            this.maxTokens = maxTokens;
            this.refillRatePerSec = refillRatePerSec;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        // Thread-safe method
        public synchronized boolean allowRequest() {
            refill();

            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }

        // Refill tokens based on time passed
        private void refill() {
            long now = System.currentTimeMillis();
            double seconds = (now - lastRefillTime) / 1000.0;

            double tokensToAdd = seconds * refillRatePerSec;
            tokens = Math.min(maxTokens, tokens + tokensToAdd);

            lastRefillTime = now;
        }

        public int getRemainingTokens() {
            return (int) tokens;
        }

        public long getRetryAfterSeconds() {
            if (tokens >= 1) return 0;

            double needed = 1 - tokens;
            return (long) Math.ceil(needed / refillRatePerSec);
        }
    }

    // Rate Limiter
    static class RateLimiter {

        private ConcurrentHashMap<String, TokenBucket> clientMap;

        private static final int MAX_REQUESTS = 1000;
        private static final double REFILL_RATE = MAX_REQUESTS / 3600.0; // per second

        public RateLimiter() {
            clientMap = new ConcurrentHashMap<>();
        }

        public String checkRateLimit(String clientId) {

            clientMap.putIfAbsent(clientId,
                    new TokenBucket(MAX_REQUESTS, REFILL_RATE));

            TokenBucket bucket = clientMap.get(clientId);

            if (bucket.allowRequest()) {
                return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
            } else {
                return "Denied (0 requests remaining, retry after "
                        + bucket.getRetryAfterSeconds() + "s)";
            }
        }

        public String getRateLimitStatus(String clientId) {
            TokenBucket bucket = clientMap.get(clientId);

            if (bucket == null) return "Client not found";

            int used = MAX_REQUESTS - bucket.getRemainingTokens();

            return "{used: " + used +
                    ", limit: " + MAX_REQUESTS +
                    ", remaining: " + bucket.getRemainingTokens() + "}";
        }
    }

    // =========================
    // MAIN METHOD (TEST)
    // =========================
    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        String client = "abc123";

        // Simulate requests
        for (int i = 0; i < 5; i++) {
            System.out.println(limiter.checkRateLimit(client));
        }

        // Force exhaustion quickly
        for (int i = 0; i < 1000; i++) {
            limiter.checkRateLimit(client);
        }

        System.out.println(limiter.checkRateLimit(client)); // should deny
        System.out.println(limiter.getRateLimitStatus(client));
    }
}