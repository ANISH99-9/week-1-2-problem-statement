import java.util.*;
import java.util.concurrent.*;

public class week1and2 {

    static class DNSCache {

        // Entry class
        class DNSEntry {
            String ip;
            long expiryTime;

            DNSEntry(String ip, long ttlMillis) {
                this.ip = ip;
                this.expiryTime = System.currentTimeMillis() + ttlMillis;
            }

            boolean isExpired() {
                return System.currentTimeMillis() > expiryTime;
            }
        }

        // LRU Cache using LinkedHashMap
        private LinkedHashMap<String, DNSEntry> cache;

        private int capacity;
        private int hits = 0;
        private int misses = 0;
        private long totalLookupTime = 0;

        public DNSCache(int capacity) {
            this.capacity = capacity;

            cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                    return size() > DNSCache.this.capacity;
                }
            };

            // Start cleanup thread
            startCleanupThread();
        }

        // Resolve domain
        public synchronized String resolve(String domain) {
            long start = System.nanoTime();

            DNSEntry entry = cache.get(domain);

            // Cache HIT
            if (entry != null && !entry.isExpired()) {
                hits++;
                totalLookupTime += (System.nanoTime() - start);
                return entry.ip + " (CACHE HIT)";
            }

            // Cache MISS or expired
            misses++;

            String ip = queryUpstreamDNS(domain);
            cache.put(domain, new DNSEntry(ip, 5000)); // TTL = 5 sec

            totalLookupTime += (System.nanoTime() - start);
            return ip + " (CACHE MISS)";
        }

        // Simulate upstream DNS query
        private String queryUpstreamDNS(String domain) {
            try {
                Thread.sleep(50); // simulate delay (~100ms real-world)
            } catch (InterruptedException e) {}

            return "192.168.1." + new Random().nextInt(255);
        }

        // Background cleanup for expired entries
        private void startCleanupThread() {
            Thread cleaner = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(2000);

                        synchronized (this) {
                            Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

                            while (it.hasNext()) {
                                Map.Entry<String, DNSEntry> entry = it.next();
                                if (entry.getValue().isExpired()) {
                                    it.remove();
                                }
                            }
                        }

                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });

            cleaner.setDaemon(true);
            cleaner.start();
        }

        // Stats
        public String getCacheStats() {
            int total = hits + misses;
            double hitRate = total == 0 ? 0 : (hits * 100.0 / total);
            double avgTime = total == 0 ? 0 : (totalLookupTime / 1e6) / total;

            return "Hit Rate: " + String.format("%.2f", hitRate) +
                    "%, Avg Lookup Time: " + String.format("%.2f", avgTime) + " ms";
        }
    }

    // =========================
    // MAIN METHOD (TEST)
    // =========================
    public static void main(String[] args) throws Exception {

        DNSCache dns = new DNSCache(3);

        System.out.println(dns.resolve("google.com"));
        System.out.println(dns.resolve("google.com"));

        Thread.sleep(6000); // wait for TTL expiry

        System.out.println(dns.resolve("google.com"));

        System.out.println(dns.getCacheStats());
    }
}