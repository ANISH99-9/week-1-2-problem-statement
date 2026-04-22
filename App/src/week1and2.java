import java.util.*;

public class week1and2 {

    static class ParkingLot {

        static class Spot {
            String plate;
            long entryTime;
            boolean isDeleted;

            Spot(String plate) {
                this.plate = plate;
                this.entryTime = System.currentTimeMillis();
                this.isDeleted = false;
            }
        }

        private Spot[] table;
        private int capacity;
        private int size;
        private int totalProbes;

        public ParkingLot(int capacity) {
            this.capacity = capacity;
            this.table = new Spot[capacity];
            this.size = 0;
            this.totalProbes = 0;
        }

        // Hash function
        private int hash(String plate) {
            return Math.abs(plate.hashCode()) % capacity;
        }

        // Park vehicle (linear probing)
        public String parkVehicle(String plate) {
            int index = hash(plate);
            int probes = 0;

            for (int i = 0; i < capacity; i++) {
                int pos = (index + i) % capacity;

                if (table[pos] == null || table[pos].isDeleted) {
                    table[pos] = new Spot(plate);
                    size++;
                    totalProbes += probes;

                    return "Assigned spot #" + pos + " (" + probes + " probes)";
                }

                probes++;
            }

            return "Parking Full";
        }

        // Exit vehicle
        public String exitVehicle(String plate) {
            int index = hash(plate);

            for (int i = 0; i < capacity; i++) {
                int pos = (index + i) % capacity;

                if (table[pos] != null && !table[pos].isDeleted &&
                        table[pos].plate.equals(plate)) {

                    long durationMillis = System.currentTimeMillis() - table[pos].entryTime;
                    double hours = durationMillis / (1000.0 * 60 * 60);

                    double fee = hours * 5; // $5 per hour

                    table[pos].isDeleted = true;
                    size--;

                    return "Spot #" + pos + " freed, Duration: " +
                            String.format("%.2f", hours) +
                            "h, Fee: $" + String.format("%.2f", fee);
                }
            }

            return "Vehicle not found";
        }

        // Find nearest available spot
        public int findNearest() {
            for (int i = 0; i < capacity; i++) {
                if (table[i] == null || table[i].isDeleted) {
                    return i;
                }
            }
            return -1;
        }

        // Statistics
        public String getStatistics() {
            double occupancy = (size * 100.0) / capacity;
            double avgProbes = size == 0 ? 0 : (double) totalProbes / size;

            return "Occupancy: " + String.format("%.2f", occupancy) +
                    "%, Avg Probes: " + String.format("%.2f", avgProbes);
        }
    }

    // =========================
    // MAIN METHOD (TEST)
    // =========================
    public static void main(String[] args) throws Exception {

        ParkingLot lot = new ParkingLot(10);

        System.out.println(lot.parkVehicle("ABC-1234"));
        System.out.println(lot.parkVehicle("ABC-1235"));
        System.out.println(lot.parkVehicle("XYZ-9999"));

        Thread.sleep(2000); // simulate time

        System.out.println(lot.exitVehicle("ABC-1234"));

        System.out.println("Nearest spot: " + lot.findNearest());
        System.out.println(lot.getStatistics());
    }
}