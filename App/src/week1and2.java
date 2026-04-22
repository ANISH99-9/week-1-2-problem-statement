import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class week1and2 {

    static class UsernameChecker {

        // username → userId
        private ConcurrentHashMap<String, Integer> userMap;

        // username → attempt count
        private ConcurrentHashMap<String, AtomicInteger> attemptMap;

        public UsernameChecker() {
            userMap = new ConcurrentHashMap<>();
            attemptMap = new ConcurrentHashMap<>();
        }

        // Check availability in O(1)
        public boolean checkAvailability(String username) {
            // Track attempts (thread-safe)
            attemptMap.putIfAbsent(username, new AtomicInteger(0));
            attemptMap.get(username).incrementAndGet();

            return !userMap.containsKey(username);
        }

        // Register username
        public void registerUser(String username, int userId) {
            userMap.put(username, userId);
        }

        // Suggest alternatives
        public List<String> suggestAlternatives(String username) {
            List<String> suggestions = new ArrayList<>();

            // Add numbers
            for (int i = 1; i <= 5; i++) {
                String candidate = username + i;
                if (!userMap.containsKey(candidate)) {
                    suggestions.add(candidate);
                }
            }

            // Replace underscore with dot
            if (username.contains("_")) {
                String alt = username.replace("_", ".");
                if (!userMap.containsKey(alt)) {
                    suggestions.add(alt);
                }
            }

            // Add random suffix
            suggestions.add(username + new Random().nextInt(1000));

            return suggestions;
        }

        // Get most attempted username
        public String getMostAttempted() {
            String maxUser = null;
            int maxCount = 0;

            for (Map.Entry<String, AtomicInteger> entry : attemptMap.entrySet()) {
                int count = entry.getValue().get();
                if (count > maxCount) {
                    maxCount = count;
                    maxUser = entry.getKey();
                }
            }

            return maxUser + " (" + maxCount + " attempts)";
        }
    }

    // =========================
    // Main Method (Testing)
    // =========================
    public static void main(String[] args) {
        UsernameChecker checker = new UsernameChecker();

        // Pre-register users
        checker.registerUser("john_doe", 1);
        checker.registerUser("admin", 2);

        // Availability checks
        System.out.println(checker.checkAvailability("john_doe"));   // false
        System.out.println(checker.checkAvailability("jane_smith")); // true

        // Suggestions
        System.out.println(checker.suggestAlternatives("john_doe"));

        // Simulate attempts
        for (int i = 0; i < 5; i++) checker.checkAvailability("admin");
        for (int i = 0; i < 3; i++) checker.checkAvailability("john_doe");

        // Most attempted
        System.out.println(checker.getMostAttempted());
    }
}