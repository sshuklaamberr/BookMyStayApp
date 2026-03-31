import java.util.*;

public class BookMyStayApp {

    // UC2 - Enums
    enum RoomType   { SINGLE, DOUBLE, SUITE }
    enum RoomStatus { AVAILABLE, BOOKED, MAINTENANCE }
    enum AddOnService {
        BREAKFAST(200), PARKING(100), SPA(500), AIRPORT_TRANSFER(300);
        final int price;
        AddOnService(int price) { this.price = price; }
    }

    static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        showWelcomeBanner();
        runMainMenu();
    }

    static void showWelcomeBanner() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║       Welcome to  BookMyStayApp          ║");
        System.out.println("║   Your Trusted Hotel Booking Partner     ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    static void runMainMenu() {
        while (true) {
            System.out.println("\n========= MAIN MENU =========");
            System.out.println("1. View Room Types");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1":
                    System.out.println("Room Types   : " + Arrays.toString(RoomType.values()));
                    System.out.println("Room Status  : " + Arrays.toString(RoomStatus.values()));
                    System.out.println("Add-On Services:");
                    for (AddOnService a : AddOnService.values())
                        System.out.println("  " + a + " - Rs." + a.price);
                    break;
                case "0":
                    System.out.println("Thank you! Goodbye!");
                    return;
                default:
                    System.out.println("[Error] Invalid option.");
            }
        }
    }
}