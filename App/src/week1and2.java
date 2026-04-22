import java.util.*;

public class week1and2 {

    static class PlagiarismDetector {

        // n-gram → set of document IDs
        private Map<String, Set<String>> index;

        // document → its n-grams
        private Map<String, Set<String>> documentMap;

        private int N = 5; // 5-gram

        public PlagiarismDetector() {
            index = new HashMap<>();
            documentMap = new HashMap<>();
        }

        // Add document to system
        public void addDocument(String docId, String text) {
            Set<String> ngrams = generateNGrams(text);
            documentMap.put(docId, ngrams);

            // Build inverted index
            for (String gram : ngrams) {
                index.putIfAbsent(gram, new HashSet<>());
                index.get(gram).add(docId);
            }
        }

        // Generate n-grams
        private Set<String> generateNGrams(String text) {
            String[] words = text.toLowerCase().split("\\s+");
            Set<String> grams = new HashSet<>();

            for (int i = 0; i <= words.length - N; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < N; j++) {
                    sb.append(words[i + j]).append(" ");
                }
                grams.add(sb.toString().trim());
            }

            return grams;
        }

        // Analyze new document
        public void analyzeDocument(String docId, String text) {

            Set<String> newDocGrams = generateNGrams(text);
            Map<String, Integer> matchCount = new HashMap<>();

            // Count matches with existing documents
            for (String gram : newDocGrams) {
                if (index.containsKey(gram)) {
                    for (String existingDoc : index.get(gram)) {
                        matchCount.put(existingDoc,
                                matchCount.getOrDefault(existingDoc, 0) + 1);
                    }
                }
            }

            System.out.println("Extracted " + newDocGrams.size() + " n-grams\n");

            // Calculate similarity
            for (String existingDoc : matchCount.keySet()) {
                int matches = matchCount.get(existingDoc);
                int total = newDocGrams.size();

                double similarity = (matches * 100.0) / total;

                System.out.println("Matches with " + existingDoc + ": " + matches);
                System.out.println("Similarity: " + String.format("%.2f", similarity) + "%");

                if (similarity > 50) {
                    System.out.println("⚠️ PLAGIARISM DETECTED\n");
                } else if (similarity > 10) {
                    System.out.println("⚠️ Suspicious\n");
                } else {
                    System.out.println("✅ Safe\n");
                }
            }
        }
    }

    // =========================
    // MAIN METHOD (TEST)
    // =========================
    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String doc1 = "this is a simple example of plagiarism detection system using hashing technique";
        String doc2 = "this is a simple example of plagiarism detection system using advanced hashing";
        String doc3 = "completely different content unrelated to plagiarism detection system";

        // Add documents
        detector.addDocument("essay_089", doc1);
        detector.addDocument("essay_092", doc2);

        // Analyze new document
        detector.analyzeDocument("essay_123", doc3);
    }
}