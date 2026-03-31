import java.util.*;

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

    static final List<Room> rooms = new ArrayList<>();
    static final Scanner sc = new Scanner(System.in);

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

    // UC4 - Search Available Rooms
    static void searchAvailableRooms() {
        System.out.println("\n===== SEARCH AVAILABLE ROOMS =====");
        System.out.print("Filter by type [SINGLE / DOUBLE / SUITE] or press Enter for all: ");
        String filter = sc.nextLine().trim().toUpperCase();
        boolean found = false;
        for (Room r : rooms) {
            if (r.status != RoomStatus.AVAILABLE) continue;
            if (!filter.isEmpty() && !r.type.name().equals(filter)) continue;
            System.out.println(r);
            found = true;
        }
        if (!found) System.out.println("[Info] No available rooms match your filter.");
    }

    static void runMainMenu() {
        while (true) {
            System.out.println("\n========= MAIN MENU =========");
            System.out.println("1. View All Rooms");
            System.out.println("2. Search Available Rooms");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1": viewAllRooms();         break;
                case "2": searchAvailableRooms(); break;
                case "0":
                    System.out.println("Thank you! Goodbye!");
                    return;
                default:
                    System.out.println("[Error] Invalid option.");
            }
        }
    }
}