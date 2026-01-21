package com.company;

import com.company.controllers.AuthController;
import com.company.controllers.interfaces.IBookingController;
import com.company.controllers.interfaces.ICancellationController;
import com.company.controllers.interfaces.ISearchController;
import com.company.controllers.interfaces.IUserController;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyApplication {
    private final Scanner scanner = new Scanner(System.in);

    private final IUserController userController;
    private final IBookingController bookingController;
    private final ICancellationController cancellationController;
    private final ISearchController searchController;
    private final AuthController authController;

    private Integer currentUserId = null;

    public MyApplication(
            IUserController userController,
            IBookingController bookingController,
            ICancellationController cancellationController,
            ISearchController searchController,
            AuthController authController
    ) {
        this.userController = userController;
        this.bookingController = bookingController;
        this.cancellationController = cancellationController;
        this.searchController = searchController;
        this.authController = authController;
    }

    public void start() {
        System.out.println("\nWelcome to Smart Booking System");

        while (true) {
            if (currentUserId == null) {
                if (!authMenu()) return;
            } else {
                if (!mainMenu()) return;
            }
        }
    }

    private boolean authMenu() {
        while (currentUserId == null) {
            System.out.println("\n--- AUTH MENU ---");
            System.out.println("1) Login");
            System.out.println("2) Register");
            System.out.println("0) Exit");
            System.out.print("Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> loginFlow();
                case 2 -> registerFlow();
                default -> { return false; }
            }
        }
        return true;
    }

    private void loginFlow() {
        System.out.print("Username: ");
        String username = readLine();
        System.out.print("Password: ");
        String password = readLine();

        Integer id = authController.login(username, password);
        if (id == null) {
            System.out.println("Login failed.");
        } else {
            currentUserId = id;
            System.out.println("Logged in. currentUserId=" + currentUserId);
        }
    }

    private void registerFlow() {
        System.out.print("Username: ");
        String username = readLine();
        System.out.print("Password: ");
        String password = readLine();

        String resp = authController.register(username, password);
        System.out.println(resp);

        if (resp != null && resp.toLowerCase().contains("success")) {
            Integer id = authController.login(username, password);
            if (id != null) {
                currentUserId = id;
                System.out.println("Logged in. currentUserId=" + currentUserId);
            }
        }
    }

    private boolean mainMenu() {
        System.out.println("\n========== SMART TRAVEL SYSTEM ==========");
        while (currentUserId != null) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1) Users");
            System.out.println("2) Browse");
            System.out.println("3) Booking wizard (single)");
            System.out.println("4) Booking wizard (family/group)");
            System.out.println("5) Search");
            System.out.println("6) Cancellation");
            System.out.println("9) Logout");
            System.out.println("0) Exit");
            System.out.print("Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> usersMenu();
                case 2 -> browseMenu();
                case 3 -> bookingWizardSingle();
                case 4 -> bookingWizardGroup();
                case 5 -> searchMenu();
                case 6 -> cancellationMenu();
                case 9 -> { currentUserId = null; System.out.println("Logged out."); }
                default -> { return false; }
            }
        }
        return true;
    }

    private void usersMenu() {
        while (true) {
            System.out.println("\n--- USERS ---");
            System.out.println("1) Get all users");
            System.out.println("2) Get user by id");
            System.out.println("3) Create user");
            System.out.println("0) Back");
            System.out.print("Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> System.out.println(userController.getAllUsers());
                case 2 -> {
                    System.out.print("User id: ");
                    int id = readInt();
                    System.out.println(userController.getUser(id));
                }
                case 3 -> {
                    System.out.print("Name: ");
                    String name = readLine();
                    System.out.print("Surname: ");
                    String surname = readLine();
                    System.out.print("Gender (male/female): ");
                    String gender = readLine();
                    System.out.println(userController.createUser(name, surname, gender));
                }
                default -> { return; }
            }
        }
    }

    private void browseMenu() {
        while (true) {
            System.out.println("\n--- BROWSE ---");
            System.out.println("1) List passengers");
            System.out.println("2) List flights");
            System.out.println("3) List hotels");
            System.out.println("4) Booking details");
            System.out.println("0) Back");
            System.out.print("Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> System.out.println(bookingController.listPassengers(50));
                case 2 -> System.out.println(bookingController.listFlights(50));
                case 3 -> System.out.println(bookingController.listHotels(50));
                case 4 -> {
                    System.out.print("Booking id: ");
                    int bookingId = readInt();
                    System.out.println(bookingController.getBookingDetails(bookingId));
                }
                default -> { return; }
            }
        }
    }

    private void bookingWizardSingle() {
        System.out.println("\n========== BOOKING WIZARD (SINGLE) ==========\n");

        System.out.println("Step 1) Choose passenger");
        System.out.println(bookingController.listPassengers(30));
        System.out.print("Passenger id: ");
        int passengerId = readInt();

        System.out.println("\nStep 2) Choose hotel");
        System.out.println(bookingController.listHotels(30));
        System.out.print("Hotel id: ");
        int hotelId = readInt();

        System.out.println("\nStep 3) Choose flight");
        System.out.println(bookingController.listFlights(30));
        System.out.print("Flight id: ");
        int flightId = readInt();

        System.out.println("\nStep 4) Nights");
        System.out.print("Nights (1..30): ");
        int nights = readIntInRange(1, 30);

        System.out.println("\nStep 5) Payment method");
        System.out.print("Method (CARD/CASH/TRANSFER): ");
        String method = readPaymentMethod();

        System.out.println("\nCreating booking...");
        String result = bookingController.createBooking(passengerId, flightId, hotelId, nights, method, currentUserId);
        System.out.println(result);

        Integer bookingId = tryExtractBookingId(result);
        if (bookingId != null) {
            System.out.println("\n--- BOOKING DETAILS ---");
            System.out.println(bookingController.getBookingDetails(bookingId));
        }
    }

    private void bookingWizardGroup() {
        System.out.println("\n========== BOOKING WIZARD (FAMILY/GROUP) ==========\n");

        System.out.print("How many travelers : ");
        int n = readIntInRange(1, 6);

        List<Integer> passengerIds = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            System.out.println("\nTraveler #" + i);

            System.out.print("Name: ");
            String name = readLine();

            System.out.print("Surname: ");
            String surname = readLine();

            System.out.print("Gender (male/female): ");
            String gender = readLine();

            System.out.print("Age: ");
            int age = readIntInRange(0, 120);

            System.out.print("Passport number: ");
            String passport = readLine();

            int pid = bookingController.createPassenger(name, surname, gender, age, passport);
            passengerIds.add(pid);

            System.out.println("Passenger created. ID=" + pid);
        }

        System.out.println("\nStep 2) Choose hotel");
        System.out.println(bookingController.listHotels(30));
        System.out.print("Hotel id: ");
        int hotelId = readInt();

        System.out.println("\nStep 3) Choose flight");
        System.out.println(bookingController.listFlights(30));
        System.out.print("Flight id: ");
        int flightId = readInt();

        System.out.println("\nStep 4) Nights");
        System.out.print("Nights (1..30): ");
        int nights = readIntInRange(1, 30);

        System.out.println("\nStep 5) Payment method");
        System.out.print("Method (CARD/CASH/TRANSFER): ");
        String method = readPaymentMethod();

        System.out.println("\nCreating group booking...");
        String result = bookingController.createGroupBooking(passengerIds, flightId, hotelId, nights, method, currentUserId);
        System.out.println(result);

        Integer bookingId = tryExtractBookingId(result);
        if (bookingId != null) {
            System.out.println("\n--- BOOKING DETAILS ---");
            System.out.println(bookingController.getBookingDetails(bookingId));
        } else {
            System.out.println("Could not parse booking id from result.");
        }
    }

    private void searchMenu() {
        while (true) {
            System.out.println("\n--- SEARCH ---");
            System.out.println("1) Search flights");
            System.out.println("2) Search hotels");
            System.out.println("0) Back");
            System.out.print("Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> searchFlightsFlow();
                case 2 -> searchHotelsFlow();
                default -> { return; }
            }
        }
    }

    private void searchFlightsFlow() {
        System.out.println("\n--- SEARCH FLIGHTS ---");

        System.out.println("FROM cities:");
        System.out.println(searchController.getFlightsFromCities());
        System.out.print("From city: ");
        String fromCity = readLine();

        System.out.println("\nTO cities:");
        System.out.println(searchController.getFlightsToCities());
        System.out.print("To city: ");
        String toCity = readLine();

        System.out.print("\nFrom date (yyyy-mm-dd or -): ");
        String fromDate = readLine();
        System.out.print("To date (yyyy-mm-dd or -): ");
        String toDate = readLine();

        System.out.print("\nFlight type (1-ECONOMY, 2-BUSINESS): ");
        String type = readLine();

        System.out.print("Sort (1-CHEAPEST, 2-EARLIEST): ");
        String sort = readLine();

        System.out.println("\nRESULT:");
        System.out.println(searchController.getFlightsByFilter(fromCity, toCity, fromDate, toDate, type, sort));
    }

    private void searchHotelsFlow() {
        System.out.println("\n--- SEARCH HOTELS ---");

        System.out.println("Cities:");
        System.out.println(searchController.getHotelsCities());
        System.out.print("City: ");
        String city = readLine();

        System.out.print("Min stars: ");
        int minStars = readIntInRange(1, 5);

        System.out.print("Max price per night: ");
        int maxPrice = readInt();

        System.out.print("Sort (1-CHEAPEST, 2-HIGHEST STARS): ");
        String sort = readLine();

        System.out.println("\nRESULT:");
        System.out.println(searchController.getHotelsByFilter(city, minStars, maxPrice, sort));
    }

    private void cancellationMenu() {
        while (true) {
            System.out.println("\n--- CANCELLATION ---");
            System.out.println("1) Cancel booking");
            System.out.println("0) Back");
            System.out.print("Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> {
                    System.out.print("Booking id: ");
                    int bookingId = readInt();
                    System.out.println(cancellationController.cancel(bookingId));
                }
                default -> { return; }
            }
        }
    }

    private int readInt() {
        while (true) {
            String s = scanner.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                System.out.print("Enter a number: ");
            }
        }
    }

    private int readIntInRange(int min, int max) {
        while (true) {
            int x = readInt();
            if (x >= min && x <= max) return x;
            System.out.print("Enter number in range " + min + ".." + max + ": ");
        }
    }

    private String readLine() {
        String s = scanner.nextLine();
        while (s != null && s.trim().isEmpty()) s = scanner.nextLine();
        return s == null ? "" : s.trim();
    }

    private String readPaymentMethod() {
        while (true) {
            String m = readLine().toUpperCase();
            if (m.equals("CARD") || m.equals("CASH") || m.equals("TRANSFER")) return m;
            System.out.print("Enter CARD/CASH/TRANSFER: ");
        }
    }

    private Integer tryExtractBookingId(String text) {
        if (text == null) return null;
        int idx = text.indexOf("ID=");
        if (idx < 0) return null;
        idx += 3;
        StringBuilder sb = new StringBuilder();
        while (idx < text.length()) {
            char c = text.charAt(idx);
            if (!Character.isDigit(c)) break;
            sb.append(c);
            idx++;
        }
        if (sb.length() == 0) return null;
        try { return Integer.parseInt(sb.toString()); } catch (Exception e) { return null; }
    }
}
