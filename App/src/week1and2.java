import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class week1and2 {

    static class InventoryManager {

        // productId → stock count
        private ConcurrentHashMap<String, AtomicInteger> stockMap;

        // productId → waiting queue (FIFO)
        private ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>> waitlistMap;

        public InventoryManager() {
            stockMap = new ConcurrentHashMap<>();
            waitlistMap = new ConcurrentHashMap<>();
        }

        // Add product
        public void addProduct(String productId, int stock) {
            stockMap.put(productId, new AtomicInteger(stock));
            waitlistMap.put(productId, new ConcurrentLinkedQueue<>());
        }

        // Check stock in O(1)
        public int checkStock(String productId) {
            AtomicInteger stock = stockMap.get(productId);
            return (stock != null) ? stock.get() : 0;
        }

        // Purchase item (thread-safe, no overselling)
        public String purchaseItem(String productId, int userId) {
            AtomicInteger stock = stockMap.get(productId);

            if (stock == null) return "Product not found";

            while (true) {
                int currentStock = stock.get();

                // If stock finished → add to waitlist
                if (currentStock <= 0) {
                    waitlistMap.get(productId).add(userId);
                    int position = waitlistMap.get(productId).size();
                    return "Added to waiting list, position #" + position;
                }

                // Atomic decrement (prevents overselling)
                if (stock.compareAndSet(currentStock, currentStock - 1)) {
                    return "Success, remaining: " + (currentStock - 1);
                }
                // else retry (another thread modified stock)
            }
        }

        // Get waiting list
        public List<Integer> getWaitlist(String productId) {
            return new ArrayList<>(waitlistMap.get(productId));
        }
    }

    // =========================
    // MAIN METHOD (TEST)
    // =========================
    public static void main(String[] args) {

        InventoryManager manager = new InventoryManager();

        manager.addProduct("IPHONE15_256GB", 5);

        // Simulate multiple users
        for (int i = 1; i <= 8; i++) {
            String result = manager.purchaseItem("IPHONE15_256GB", i);
            System.out.println("User " + i + ": " + result);
        }

        // Check remaining stock
        System.out.println("Stock left: " + manager.checkStock("IPHONE15_256GB"));

        // Waiting list
        System.out.println("Waitlist: " + manager.getWaitlist("IPHONE15_256GB"));
    }
}