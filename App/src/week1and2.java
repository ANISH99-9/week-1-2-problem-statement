import java.util.*;

public class week1and2 {

    // Transaction class
    static class Transaction {
        int id;
        int amount;
        String merchant;
        String account;
        long time; // epoch milliseconds

        public Transaction(int id, int amount, String merchant, String account, long time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.time = time;
        }
    }

    static class TransactionAnalyzer {

        // =========================
        // 1. Classic Two-Sum
        // =========================
        public List<int[]> findTwoSum(List<Transaction> txs, int target) {
            Map<Integer, Transaction> map = new HashMap<>();
            List<int[]> result = new ArrayList<>();

            for (Transaction t : txs) {
                int complement = target - t.amount;

                if (map.containsKey(complement)) {
                    result.add(new int[]{map.get(complement).id, t.id});
                }

                map.put(t.amount, t);
            }

            return result;
        }

        // =========================
        // 2. Two-Sum with 1-hour window
        // =========================
        public List<int[]> findTwoSumWithTime(List<Transaction> txs, int target) {
            List<int[]> result = new ArrayList<>();
            Map<Integer, List<Transaction>> map = new HashMap<>();

            for (Transaction t : txs) {
                int complement = target - t.amount;

                if (map.containsKey(complement)) {
                    for (Transaction prev : map.get(complement)) {
                        if (Math.abs(t.time - prev.time) <= 3600000) { // 1 hour
                            result.add(new int[]{prev.id, t.id});
                        }
                    }
                }

                map.putIfAbsent(t.amount, new ArrayList<>());
                map.get(t.amount).add(t);
            }

            return result;
        }

        // =========================
        // 3. K-Sum (General)
        // =========================
        public List<List<Integer>> findKSum(int[] nums, int target, int k) {
            Arrays.sort(nums);
            return kSumHelper(nums, target, k, 0);
        }

        private List<List<Integer>> kSumHelper(int[] nums, int target, int k, int start) {
            List<List<Integer>> res = new ArrayList<>();

            if (k == 2) {
                Map<Integer, Integer> map = new HashMap<>();
                for (int i = start; i < nums.length; i++) {
                    int comp = target - nums[i];
                    if (map.containsKey(comp)) {
                        res.add(Arrays.asList(comp, nums[i]));
                    }
                    map.put(nums[i], i);
                }
                return res;
            }

            for (int i = start; i < nums.length; i++) {
                List<List<Integer>> sub = kSumHelper(nums, target - nums[i], k - 1, i + 1);

                for (List<Integer> list : sub) {
                    List<Integer> newList = new ArrayList<>();
                    newList.add(nums[i]);
                    newList.addAll(list);
                    res.add(newList);
                }
            }

            return res;
        }

        // =========================
        // 4. Duplicate Detection
        // =========================
        public List<String> detectDuplicates(List<Transaction> txs) {
            Map<String, List<Transaction>> map = new HashMap<>();

            for (Transaction t : txs) {
                String key = t.amount + "_" + t.merchant;

                map.putIfAbsent(key, new ArrayList<>());
                map.get(key).add(t);
            }

            List<String> result = new ArrayList<>();

            for (String key : map.keySet()) {
                List<Transaction> list = map.get(key);

                Set<String> accounts = new HashSet<>();
                for (Transaction t : list) {
                    accounts.add(t.account);
                }

                if (accounts.size() > 1) {
                    result.add("Duplicate: " + key + " accounts=" + accounts);
                }
            }

            return result;
        }
    }

    // =========================
    // MAIN METHOD (TEST)
    // =========================
    public static void main(String[] args) {

        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        List<Transaction> txs = new ArrayList<>();

        long now = System.currentTimeMillis();

        txs.add(new Transaction(1, 500, "StoreA", "acc1", now));
        txs.add(new Transaction(2, 300, "StoreB", "acc2", now + 1000));
        txs.add(new Transaction(3, 200, "StoreC", "acc3", now + 2000));
        txs.add(new Transaction(4, 500, "StoreA", "acc4", now + 3000));

        // Two Sum
        System.out.println("TwoSum:");
        for (int[] pair : analyzer.findTwoSum(txs, 500)) {
            System.out.println(Arrays.toString(pair));
        }

        // Two Sum with Time
        System.out.println("\nTwoSum (1hr window):");
        for (int[] pair : analyzer.findTwoSumWithTime(txs, 500)) {
            System.out.println(Arrays.toString(pair));
        }

        // K Sum
        System.out.println("\nKSum:");
        int[] nums = {500, 300, 200};
        System.out.println(analyzer.findKSum(nums, 1000, 3));

        // Duplicate Detection
        System.out.println("\nDuplicates:");
        System.out.println(analyzer.detectDuplicates(txs));
    }
}