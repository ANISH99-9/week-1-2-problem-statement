import java.util.*;

public class week1and2 {

    // Trie Node
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        List<String> words = new ArrayList<>(); // store words with this prefix
    }

    static class AutocompleteSystem {

        private TrieNode root;
        private Map<String, Integer> frequencyMap;
        private static final int TOP_K = 10;

        public AutocompleteSystem() {
            root = new TrieNode();
            frequencyMap = new HashMap<>();
        }

        // Insert query
        public void addQuery(String query) {
            frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);

            TrieNode node = root;
            for (char c : query.toCharArray()) {
                node.children.putIfAbsent(c, new TrieNode());
                node = node.children.get(c);

                // store query for this prefix
                if (!node.words.contains(query)) {
                    node.words.add(query);
                }
            }
        }

        // Search top 10 suggestions
        public List<String> search(String prefix) {
            TrieNode node = root;

            for (char c : prefix.toCharArray()) {
                if (!node.children.containsKey(c)) {
                    return new ArrayList<>();
                }
                node = node.children.get(c);
            }

            // Min-heap for top K
            PriorityQueue<String> pq = new PriorityQueue<>(
                    (a, b) -> frequencyMap.get(a) - frequencyMap.get(b)
            );

            for (String word : node.words) {
                pq.offer(word);
                if (pq.size() > TOP_K) {
                    pq.poll();
                }
            }

            List<String> result = new ArrayList<>();
            while (!pq.isEmpty()) {
                result.add(pq.poll());
            }

            Collections.reverse(result); // highest freq first
            return result;
        }

        // Update frequency (when user searches)
        public void updateFrequency(String query) {
            addQuery(query); // same as insert
        }
    }

    // =========================
    // MAIN METHOD (TEST)
    // =========================
    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        // Add queries
        system.addQuery("java tutorial");
        system.addQuery("javascript");
        system.addQuery("java download");
        system.addQuery("java tutorial");
        system.addQuery("java tutorial");

        // Search
        System.out.println(system.search("jav"));

        // Update frequency
        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        System.out.println(system.search("java"));
    }
}