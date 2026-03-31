import java.util.*;

public class BookMyStayApp {

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
        System.out.println("\n========= MAIN MENU =========");
        System.out.println("0. Exit");
        System.out.print("Choice: ");
        String ch = sc.nextLine().trim();
        if (ch.equals("0")) System.out.println("Goodbye!");
    }
}