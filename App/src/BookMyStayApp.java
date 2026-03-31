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
            totalAmount = nights * room.pricePerNight;
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
        System.out.print("Filter by type [SINGLE / DOUBLE / SUITE] or Enter for all: ");
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

    // UC6 - Confirmation Slip
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
        System.out.printf ("  TOTAL       : Rs.%.2f%n", b.totalAmount);
        System.out.println("  Status      : CONFIRMED");
        System.out.println("══════════════════════════════════");
    }

    static void makeBooking() {
        System.out.println("\n===== MAKE A BOOKING =====");
        System.out.print("Guest Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Room Number: ");
        int rNo = Integer.parseInt(sc.nextLine().trim());
        Room room = rooms.stream().filter(r -> r.roomNumber == rNo).findFirst().orElse(null);

        if (room == null) { System.out.println("[Error] Room not found."); return; }
        if (room.status != RoomStatus.AVAILABLE) {
            System.out.println("[Error] Room not available."); return;
        }

        System.out.print("Check-In  (YYYY-MM-DD): ");
        LocalDate ci = LocalDate.parse(sc.nextLine().trim());

        System.out.print("Check-Out (YYYY-MM-DD): ");
        LocalDate co = LocalDate.parse(sc.nextLine().trim());

        Booking booking = new Booking(name, room, ci, co);
        room.status = RoomStatus.BOOKED;
        bookings.add(booking);
        printConfirmation(booking); // UC6
    }

    static void runMainMenu() {
        while (true) {
            System.out.println("\n========= MAIN MENU =========");
            System.out.println("1. View All Rooms");
            System.out.println("2. Search Available Rooms");
            System.out.println("3. Make a Booking");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1": viewAllRooms();         break;
                case "2": searchAvailableRooms(); break;
                case "3": makeBooking();          break;
                case "0":
                    System.out.println("Thank you! Goodbye!");
                    return;
                default:
                    System.out.println("[Error] Invalid option.");
            }
        }
    }
}