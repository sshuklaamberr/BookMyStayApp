import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BookMyStayApp {

    enum RoomType   { SINGLE, DOUBLE, SUITE }
    enum RoomStatus { AVAILABLE, BOOKED, MAINTENANCE }
    enum AddOnService {
        BREAKFAST(200), PARKING(100), SPA(500), AIRPORT_TRANSFER(300);
        final int price;
        AddOnService(int price) { this.price = price; }
    }

    static class Room {
        int roomNumber;
        RoomType type;
        RoomStatus status;
        double pricePerNight;

        Room(int roomNumber, RoomType type, double pricePerNight) {
            this.roomNumber    = roomNumber;
            this.type          = type;
            this.pricePerNight = pricePerNight;
            this.status        = RoomStatus.AVAILABLE;
        }

        public String toString() {
            return String.format("Room %-4d | %-7s | %-12s | Rs.%.2f/night",
                    roomNumber, type, status, pricePerNight);
        }
    }

    static class Booking {
        static AtomicInteger counter = new AtomicInteger(1000);
        String bookingId, guestName;
        Room room;
        LocalDate checkIn, checkOut;
        double totalAmount;
        boolean isCancelled = false;
        List<AddOnService> addOns = new ArrayList<>();

        Booking(String guestName, Room room, LocalDate checkIn, LocalDate checkOut) {
            this.bookingId = "BMS" + counter.incrementAndGet();
            this.guestName = guestName;
            this.room      = room;
            this.checkIn   = checkIn;
            this.checkOut  = checkOut;
            calculateTotal();
        }

        void calculateTotal() {
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            totalAmount  = nights * room.pricePerNight;
            for (AddOnService s : addOns) totalAmount += s.price;
        }

        public String toString() {
            return String.format("ID: %-8s | Guest: %-15s | Room: %d | %s to %s | Rs.%.2f | %s",
                    bookingId, guestName, room.roomNumber, checkIn, checkOut,
                    totalAmount, isCancelled ? "CANCELLED" : "CONFIRMED");
        }
    }

    static final List<Room>    rooms    = new ArrayList<>();
    static final List<Booking> bookings = new ArrayList<>();
    static final Scanner       sc       = new Scanner(System.in);

    public static void main(String[] args) {
        initializeRooms();
        showWelcomeBanner();
        runMainMenu();
    }

    static void showWelcomeBanner() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║       Welcome to  BookMyStayApp          ║");
        System.out.println("║   Your Trusted Hotel Booking Partner     ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    static void initializeRooms() {
        rooms.add(new Room(101, RoomType.SINGLE, 1500));
        rooms.add(new Room(102, RoomType.SINGLE, 1500));
        rooms.add(new Room(103, RoomType.SINGLE, 1500));
        rooms.add(new Room(201, RoomType.DOUBLE, 2500));
        rooms.add(new Room(202, RoomType.DOUBLE, 2500));
        rooms.add(new Room(203, RoomType.DOUBLE, 2500));
        rooms.add(new Room(301, RoomType.SUITE,  5000));
        rooms.add(new Room(302, RoomType.SUITE,  5000));
    }

    static void viewAllRooms() {
        System.out.println("\n===== ROOM INVENTORY =====");
        for (Room r : rooms) System.out.println(r);
    }

    static void searchAvailableRooms() {
        System.out.println("\n===== SEARCH AVAILABLE ROOMS =====");
        System.out.print("Filter [SINGLE / DOUBLE / SUITE] or Enter for all: ");
        String filter = sc.nextLine().trim().toUpperCase();
        boolean found = false;
        for (Room r : rooms) {
            if (r.status != RoomStatus.AVAILABLE) continue;
            if (!filter.isEmpty() && !r.type.name().equals(filter)) continue;
            System.out.println(r);
            found = true;
        }
        if (!found) System.out.println("[Info] No available rooms found.");
    }

    static boolean validateName(String name) {
        return name != null && !name.isBlank() && name.matches("[a-zA-Z ]+");
    }

    static boolean isInteger(String s) {
        try { Integer.parseInt(s.trim()); return true; }
        catch (NumberFormatException e)  { return false; }
    }

    static LocalDate parseDate(String input) {
        try { return LocalDate.parse(input.trim()); }
        catch (Exception e) { return null; }
    }

    static void selectAddOns(Booking booking) {
        System.out.println("\n--- Add-On Services ---");
        AddOnService[] list = AddOnService.values();
        for (int i = 0; i < list.length; i++)
            System.out.printf("  %d. %-20s Rs.%d%n", i + 1, list[i], list[i].price);
        System.out.print("Select (e.g. 1,3) or Enter to skip: ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()) return;
        for (String t : input.split(",")) {
            try {
                int idx = Integer.parseInt(t.trim()) - 1;
                if (idx >= 0 && idx < list.length && !booking.addOns.contains(list[idx])) {
                    booking.addOns.add(list[idx]);
                    System.out.println("[Added] " + list[idx] + " Rs." + list[idx].price);
                }
            } catch (NumberFormatException e) {
                System.out.println("[Warning] Invalid: " + t.trim());
            }
        }
        booking.calculateTotal();
    }

    static void printConfirmation(Booking b) {
        long nights = ChronoUnit.DAYS.between(b.checkIn, b.checkOut);
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║      BOOKING CONFIRMED       ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println("  Booking ID  : " + b.bookingId);
        System.out.println("  Guest       : " + b.guestName);
        System.out.println("  Room        : " + b.room.roomNumber + " (" + b.room.type + ")");
        System.out.println("  Check-In    : " + b.checkIn);
        System.out.println("  Check-Out   : " + b.checkOut);
        System.out.println("  Nights      : " + nights);
        System.out.printf ("  Room Cost   : Rs.%.2f x %d = Rs.%.2f%n",
                b.room.pricePerNight, nights, nights * b.room.pricePerNight);
        if (!b.addOns.isEmpty()) {
            double addOnTotal = b.addOns.stream().mapToInt(a -> a.price).sum();
            System.out.println("  Add-Ons     : " + b.addOns);
            System.out.printf ("  Add-On Cost : Rs.%.2f%n", addOnTotal);
        }
        System.out.printf ("  TOTAL       : Rs.%.2f%n", b.totalAmount);
        System.out.println("  Status      : CONFIRMED");
        System.out.println("══════════════════════════════════");
    }

    static void makeBooking() {
        System.out.println("\n===== MAKE A BOOKING =====");
        System.out.print("Guest Name: ");
        String name = sc.nextLine().trim();
        if (!validateName(name)) { System.out.println("[Error] Letters and spaces only."); return; }

        System.out.print("Room Number: ");
        String rInput = sc.nextLine().trim();
        if (!isInteger(rInput)) { System.out.println("[Error] Must be numeric."); return; }
        int rNo = Integer.parseInt(rInput);
        Room room = rooms.stream().filter(r -> r.roomNumber == rNo).findFirst().orElse(null);
        if (room == null) { System.out.println("[Error] Room not found."); return; }
        if (room.status != RoomStatus.AVAILABLE) { System.out.println("[Error] Room " + rNo + " is " + room.status); return; }

        System.out.print("Check-In  (YYYY-MM-DD): ");
        LocalDate ci = parseDate(sc.nextLine().trim());
        if (ci == null) { System.out.println("[Error] Invalid date."); return; }

        System.out.print("Check-Out (YYYY-MM-DD): ");
        LocalDate co = parseDate(sc.nextLine().trim());
        if (co == null) { System.out.println("[Error] Invalid date."); return; }
        if (!co.isAfter(ci)) { System.out.println("[Error] Check-out must be after check-in."); return; }

        Booking booking = new Booking(name, room, ci, co);
        selectAddOns(booking);
        room.status = RoomStatus.BOOKED;
        bookings.add(booking);
        printConfirmation(booking);
    }

    static void viewBookingHistory() {
        System.out.println("\n===== BOOKING HISTORY =====");
        if (bookings.isEmpty()) { System.out.println("[Info] No bookings yet."); return; }
        for (Booking b : bookings) System.out.println(b);
        long active    = bookings.stream().filter(b -> !b.isCancelled).count();
        long cancelled = bookings.stream().filter(b ->  b.isCancelled).count();
        double revenue = bookings.stream().filter(b -> !b.isCancelled)
                                          .mapToDouble(b -> b.totalAmount).sum();
        System.out.println("\n------- Report -------");
        System.out.println("Total Bookings : " + bookings.size());
        System.out.println("Active         : " + active);
        System.out.println("Cancelled      : " + cancelled);
        System.out.printf ("Total Revenue  : Rs.%.2f%n", revenue);
    }

    static void cancelBooking() {
        System.out.println("\n===== CANCEL BOOKING =====");
        System.out.print("Enter Booking ID (e.g. BMS1001): ");
        String id = sc.nextLine().trim();
        if (!id.startsWith("BMS") || id.length() < 4) {
            System.out.println("[Error] Invalid Booking ID format."); return;
        }
        for (Booking b : bookings) {
            if (b.bookingId.equalsIgnoreCase(id)) {
                if (b.isCancelled) { System.out.println("[Info] Already cancelled."); return; }
                b.isCancelled = true;
                b.room.status = RoomStatus.AVAILABLE;
                System.out.println("[Success] Booking " + id + " cancelled.");
                System.out.println("[Info] Room " + b.room.roomNumber + " is now AVAILABLE.");
                return;
            }
        }
        System.out.println("[Error] Booking ID not found.");
    }

    static void simulateConcurrentBooking() {
        System.out.println("\n===== CONCURRENT BOOKING SIMULATION =====");
        Room target = rooms.stream()
                           .filter(r -> r.status == RoomStatus.AVAILABLE)
                           .findFirst().orElse(null);
        if (target == null) { System.out.println("[Info] No available room."); return; }
        System.out.println("3 users trying to book Room " + target.roomNumber + " simultaneously...\n");
        String[] users = {"Alice", "Bob", "Charlie"};
        List<Thread> threads = new ArrayList<>();
        Object lock = new Object();
        for (String user : users) {
            threads.add(new Thread(() -> {
                synchronized (lock) {
                    if (target.status == RoomStatus.AVAILABLE) {
                        target.status = RoomStatus.BOOKED;
                        Booking b = new Booking(user, target,
                                LocalDate.now(), LocalDate.now().plusDays(2));
                        bookings.add(b);
                        System.out.println("[SUCCESS] " + user + " booked Room "
                                + target.roomNumber + " -> ID: " + b.bookingId);
                    } else {
                        System.out.println("[FAILED]  " + user + " - Room already taken.");
                    }
                }
            }));
        }
        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try { t.join(); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        System.out.println("\n[Done] Only 1 booking succeeded. Thread-safe confirmed.");
    }

    static void runMainMenu() {
        while (true) {
            System.out.println("\n========= MAIN MENU =========");
            System.out.println("1. View All Rooms");
            System.out.println("2. Search Available Rooms");
            System.out.println("3. Make a Booking");
            System.out.println("4. Booking History & Report");
            System.out.println("5. Cancel a Booking");
            System.out.println("6. Concurrent Booking Simulation");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1": viewAllRooms();                break;
                case "2": searchAvailableRooms();        break;
                case "3": makeBooking();                 break;
                case "4": viewBookingHistory();          break;
                case "5": cancelBooking();               break;
                case "6": simulateConcurrentBooking();   break;
                case "0":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("[Error] Invalid option.");
            }
        }
    }
}