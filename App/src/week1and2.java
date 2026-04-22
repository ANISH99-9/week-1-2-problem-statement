import java.util.*;
import java.util.concurrent.*;

public class week1and2 {

    static class AnalyticsSystem {

        // page → total visits
        private ConcurrentHashMap<String, Integer> pageViews;

        // page → unique users
        private ConcurrentHashMap<String, Set<String>> uniqueVisitors;

        // source → count
        private ConcurrentHashMap<String, Integer> sourceCount;

        public AnalyticsSystem() {
            pageViews = new ConcurrentHashMap<>();
            uniqueVisitors = new ConcurrentHashMap<>();
            sourceCount = new ConcurrentHashMap<>();

            startDashboardUpdater(); // auto update every 5 sec
        }

        // Process incoming event
        public void processEvent(String url, String userId, String source) {

            // Update page views
            pageViews.merge(url, 1, Integer::sum);

            // Update unique users
            uniqueVisitors.putIfAbsent(url, ConcurrentHashMap.newKeySet());
            uniqueVisitors.get(url).add(userId);

            // Update traffic source
            sourceCount.merge(source, 1, Integer::sum);
        }

        // Get top 10 pages
        public List<String> getTopPages() {
            PriorityQueue<Map.Entry<String, Integer>> pq =
                    new PriorityQueue<>(Map.Entry.comparingByValue());

            for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
                pq.offer(entry);
                if (pq.size() > 10) pq.poll();
            }

            List<String> result = new ArrayList<>();
            while (!pq.isEmpty()) {
                Map.Entry<String, Integer> e = pq.poll();
                String page = e.getKey();
                int views = e.getValue();
                int unique = uniqueVisitors.get(page).size();

                result.add(page + " - " + views + " views (" + unique + " unique)");
            }

            Collections.reverse(result); // highest first
            return result;
        }

        // Get traffic sources
        public Map<String, Integer> getSourceStats() {
            return sourceCount;
        }

        // Dashboard print
        public void getDashboard() {
            System.out.println("\n===== DASHBOARD =====");

            System.out.println("Top Pages:");
            int rank = 1;
            for (String s : getTopPages()) {
                System.out.println(rank++ + ". " + s);
            }

            System.out.println("\nTraffic Sources:");
            int total = sourceCount.values().stream().mapToInt(i -> i).sum();

            for (String source : sourceCount.keySet()) {
                int count = sourceCount.get(source);
                double percent = (count * 100.0) / total;
                System.out.println(source + ": " + String.format("%.2f", percent) + "%");
            }

            System.out.println("=====================\n");
        }

        // Auto refresh dashboard every 5 sec
        private void startDashboardUpdater() {
            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(5000);
                        getDashboard();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });

            t.setDaemon(true);
            t.start();
        }
    }

    // =========================
    // MAIN METHOD (TEST)
    // =========================
    public static void main(String[] args) throws Exception {

        AnalyticsSystem system = new AnalyticsSystem();

        // Simulate real-time traffic
        String[] urls = {"/news", "/sports", "/tech"};
        String[] sources = {"google", "facebook", "direct"};

        Random rand = new Random();

        for (int i = 1; i <= 50; i++) {
            String url = urls[rand.nextInt(urls.length)];
            String user = "user_" + rand.nextInt(20);
            String source = sources[rand.nextInt(sources.length)];

            system.processEvent(url, user, source);

            Thread.sleep(100); // simulate stream
        }

        // Let dashboard run for a bit
        Thread.sleep(10000);
    }
}