import java.util.*;

public class week1and2 {

    // =========================
    // LRU Cache (Generic)
    // =========================
    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private int capacity;

        public LRUCache(int capacity) {
            super(capacity, 0.75f, true); // access-order
            this.capacity = capacity;
        }

        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    // =========================
    // Multi-Level Cache System
    // =========================
    static class MultiLevelCache {

        private LRUCache<String, String> L1;
        private LRUCache<String, String> L2;

        // Simulated database
        private Map<String, String> database;

        // Access count for promotion
        private Map<String, Integer> accessCount;

        // Stats
        private int l1Hits = 0, l2Hits = 0, l3Hits = 0;

        public MultiLevelCache() {
            L1 = new LRUCache<>(10000);
            L2 = new LRUCache<>(100000);
            database = new HashMap<>();
            accessCount = new HashMap<>();

            // preload database
            for (int i = 1; i <= 1000; i++) {
                database.put("video_" + i, "VideoData_" + i);
            }
        }

        // Get video
        public String getVideo(String videoId) {

            long start = System.nanoTime();

            // L1 Check
            if (L1.containsKey(videoId)) {
                l1Hits++;
                long time = (System.nanoTime() - start) / 1_000_000;
                return "L1 HIT (" + time + " ms)";
            }

            // L2 Check
            if (L2.containsKey(videoId)) {
                l2Hits++;

                String data = L2.get(videoId);

                // Promote to L1
                L1.put(videoId, data);

                long time = (System.nanoTime() - start) / 1_000_000;
                return "L2 HIT → promoted to L1 (" + time + " ms)";
            }

            // L3 (Database)
            l3Hits++;

            String data = database.getOrDefault(videoId, "NOT FOUND");

            // Add to L2
            L2.put(videoId, data);

            accessCount.put(videoId, accessCount.getOrDefault(videoId, 0) + 1);

            long time = (System.nanoTime() - start) / 1_000_000;
            return "L3 HIT → added to L2 (" + time + " ms)";
        }

        // Invalidate cache
        public void invalidate(String videoId) {
            L1.remove(videoId);
            L2.remove(videoId);
        }

        // Stats
        public void getStatistics() {
            int total = l1Hits + l2Hits + l3Hits;

            System.out.println("\n===== CACHE STATS =====");

            System.out.println("L1 Hit Rate: " + (l1Hits * 100.0 / total) + "%");
            System.out.println("L2 Hit Rate: " + (l2Hits * 100.0 / total) + "%");
            System.out.println("L3 Hit Rate: " + (l3Hits * 100.0 / total) + "%");

            System.out.println("Total Requests: " + total);
            System.out.println("=======================\n");
        }
    }

    // =========================
    // MAIN METHOD (TEST)
    // =========================
    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        // First access → L3
        System.out.println(cache.getVideo("video_10"));

        // Second → L2
        System.out.println(cache.getVideo("video_10"));

        // Third → L1
        System.out.println(cache.getVideo("video_10"));

        // New video
        System.out.println(cache.getVideo("video_999"));

        // Stats
        cache.getStatistics();

        // Invalidate
        cache.invalidate("video_10");

        System.out.println(cache.getVideo("video_10"));
    }
}