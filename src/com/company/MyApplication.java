package com.company;

import com.company.controllers.AuthController;
import com.company.controllers.interfaces.*;
import com.company.models.ExtraSelection;

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
    private final IReportController reportController;


    private final IAdminController adminController;
    private final IPromoController promoController;

    private Integer currentUserId = null;
    private String currentUserRole = null;


    private static final boolean USE_COLORS = true;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private static String c(String s, String color) {
        if (!USE_COLORS) return s;
        return color + s + ANSI_RESET;
    }



    public MyApplication(
            IUserController userController,
            IBookingController bookingController,
            ICancellationController cancellationController,
            ISearchController searchController,
            AuthController authController
    ) {
        this(userController, bookingController, cancellationController, searchController, authController, null, null, null);
    }

    public MyApplication(
            IUserController userController,
            IBookingController bookingController,
            ICancellationController cancellationController,
            ISearchController searchController,
            AuthController authController,
            IReportController reportController
    ) {
        this(userController, bookingController, cancellationController, searchController, authController, reportController, null, null);
    }

    public MyApplication(
            IUserController userController,
            IBookingController bookingController,
            ICancellationController cancellationController,
            ISearchController searchController,
            AuthController authController,
            IReportController reportController,
            IAdminController adminController,
            IPromoController promoController
    ) {
        this.userController = userController;
        this.bookingController = bookingController;
        this.cancellationController = cancellationController;
        this.searchController = searchController;
        this.authController = authController;
        this.reportController = reportController;
        this.adminController = adminController;
        this.promoController = promoController;
    }



    public void start() {
        System.out.println("\n" + c("✈️  Welcome to Smart Travel Agency System!", ANSI_CYAN));

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
            System.out.println("\n" + c("🔐 --- AUTH MENU ---", ANSI_PURPLE));
            System.out.println("1) 👤 Login as USER");
            System.out.println("2) 👑 Login as ADMIN");
            System.out.println("3) 📝 Register (creates USER)");
            System.out.println("0) ❌ Exit");
            System.out.print("👉 Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> loginAsUser();
                case 2 -> loginAsAdmin();
                case 3 -> registerFlow();
                case 0 -> { return false; }
                default -> System.out.println(c("⚠️ Enter 0..3", ANSI_YELLOW));
            }
        }
        return true;
    }

    private void loginAsUser() {
        System.out.print("👤 Username: ");
        String username = readLine();
        System.out.print("🔑 Password: ");
        String password = readLine();

        Integer id = authController.login(username, password);
        if (id == null) {
            System.out.println(c("❌ Login failed.", ANSI_RED));
            return;
        }

        currentUserId = id;
        currentUserRole = "MANAGER"; // обычный пользователь
        System.out.println(c("✅ Logged in as USER | id=" + currentUserId, ANSI_GREEN));
    }

    private void loginAsAdmin() {
        System.out.print("👑 Admin username: ");
        String username = readLine();
        System.out.print("🔑 Password: ");
        String password = readLine();

        Integer id = authController.login(username, password);
        if (id == null) {
            System.out.println(c("❌ Login failed.", ANSI_RED));
            return;
        }


        String role = authController.getRoleByUsername(username);

        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            System.out.println(c("⛔ Access denied. This account is not ADMIN.", ANSI_RED));
            currentUserId = null;
            currentUserRole = null;
            return;
        }

        currentUserId = id;
        currentUserRole = "ADMIN";
        System.out.println(c("✅ Logged in as ADMIN | id=" + currentUserId, ANSI_GREEN));
    }

    private void registerFlow() {
        System.out.print("👤 Username: ");
        String username = readLine();
        System.out.print("🔑 Password: ");
        String password = readLine();

        String resp = authController.register(username, password);
        if (resp == null) System.out.println(c("❌ Register failed.", ANSI_RED));
        else System.out.println(c("✅ " + resp, ANSI_GREEN));


        if (resp != null && resp.toLowerCase().contains("success")) {
            Integer id = authController.login(username, password);
            if (id != null) {
                currentUserId = id;
                currentUserRole = "MANAGER";
                System.out.println(c("✅ Auto-login as USER | id=" + currentUserId, ANSI_GREEN));
            }
        }
    }



    private boolean mainMenu() {
        System.out.println("\n" + c("🌍 ========= SMART TRAVEL SYSTEM =========", ANSI_CYAN));

        while (currentUserId != null) {
            System.out.println("\n" + c("📌 --- MAIN MENU ---", ANSI_PURPLE));
            System.out.println("1) 👥 Users");
            System.out.println("2) 🧭 Browse");
            System.out.println("3) 🧳 Booking wizard (single)");
            System.out.println("4) 👨‍👩‍👧‍👦 Booking wizard (family/group)");
            System.out.println("5) 🔎 Search");
            System.out.println("6) ❌ Cancellation");
            System.out.println("7) 📊 Reports");

            if (promoController != null) {
                System.out.println("8) 🏷️ Apply promo code");
            }

            if ("ADMIN".equals(currentUserRole) && adminController != null) {
                System.out.println("10) 👑 Admin Panel");
            }
            if (bookingController != null && ("ADMIN".equals(currentUserRole) )) {
                System.out.println("11) Full booking description (JOIN)");
            }



            System.out.println("9) 🚪 Logout");
            System.out.println("0) ❌ Exit");
            System.out.print("👉 Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> usersMenu();
                case 2 -> browseMenu();
                case 3 -> bookingWizardSingle();
                case 4 -> bookingWizardGroup();
                case 5 -> searchMenu();
                case 6 -> cancellationMenu();
                case 7 -> reportsMenu();
                case 8 -> {
                    if (promoController != null) applyPromoFlow();
                    else System.out.println(c("⚠️ Option not available.", ANSI_YELLOW));
                }

                case 9 -> {
                    currentUserId = null;
                    currentUserRole = null;
                    System.out.println(c("👋 Logged out.", ANSI_YELLOW));
                }
                case 10 -> {
                    if ("ADMIN".equals(currentUserRole)) adminMenu();
                    else System.out.println(c("⛔ Access denied.", ANSI_RED));
                }
                case 11 -> {
                    if (!("ADMIN".equals(currentUserRole) || "MANAGER".equals(currentUserRole))) {
                        System.out.println(c("⛔ Access denied.", ANSI_RED));
                        break;
                    }

                    if (bookingController == null) {
                        System.out.println(c("⚠️ Feature not available.", ANSI_YELLOW));
                        break;
                    }

                    System.out.print("Enter booking id: ");
                    int id = readInt();

                    if (id <= 0) {
                        System.out.println(c("⚠️ booking id must be positive.", ANSI_YELLOW));
                        break;
                    }

                    System.out.println(bookingController.getFullBookingDescription(id));
                }


                case 0 -> { return false; }
                default -> System.out.println(c("⚠️ Enter valid menu number.", ANSI_YELLOW));
            }
        }
        return true;
    }



    private void usersMenu() {
        while (true) {
            System.out.println("\n" + c("👥 --- USERS ---", ANSI_PURPLE));
            System.out.println("1) 📋 Get all users");
            System.out.println("2) 🔍 Get user by id");
            System.out.println("3) ➕ Create user");
            System.out.println("0) 🔙 Back");
            System.out.print("👉 Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> System.out.println(userController.getAllUsers());
                case 2 -> {
                    System.out.print("🆔 User id: ");
                    int id = readInt();
                    System.out.println(userController.getUser(id));
                }
                case 3 -> {
                    System.out.print("👤 Name: ");
                    String name = readLine();
                    System.out.print("👤 Surname: ");
                    String surname = readLine();
                    System.out.print("⚧️ Gender (male/female): ");
                    String gender = readLine();
                    System.out.println(userController.createUser(name, surname, gender));
                }
                case 0 -> { return; }
                default -> System.out.println(c("⚠️ Enter 0..3", ANSI_YELLOW));
            }
        }
    }



    private void browseMenu() {
        while (true) {
            System.out.println("\n" + c("🧭 --- BROWSE ---", ANSI_PURPLE));
            System.out.println("1) 🧑‍🤝‍🧑 List passengers");
            System.out.println("2) ✈️ List flights");
            System.out.println("3) 🏨 List hotels");
            System.out.println("4) 🧾 Booking details");
            System.out.println("0) 🔙 Back");
            System.out.print("👉 Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> System.out.println(bookingController.listPassengers(50));
                case 2 -> System.out.println(bookingController.listFlights(50));
                case 3 -> System.out.println(bookingController.listHotels(50));
                case 4 -> {
                    System.out.print("🧾 Booking id: ");
                    int bookingId = readInt();
                    System.out.println(bookingController.getBookingDetails(bookingId));
                }
                case 0 -> { return; }
                default -> System.out.println(c("⚠️ Enter 0..4", ANSI_YELLOW));
            }
        }
    }



    private void bookingWizardSingle() {
        System.out.println("\n" + c("🧳 ========= BOOKING WIZARD (SINGLE) =========", ANSI_CYAN));

        System.out.println(c("1️⃣ Choose passenger", ANSI_BLUE));
        System.out.println(bookingController.listPassengers(30));
        System.out.print("🆔 Passenger id: ");
        int passengerId = readInt();

        System.out.println("\n" + c("2️⃣ Choose hotel", ANSI_BLUE));
        System.out.println(bookingController.listHotels(30));
        System.out.print("🏨 Hotel id: ");
        int hotelId = readInt();

        System.out.println("\n" + c("3️⃣ Choose flight", ANSI_BLUE));
        System.out.println(bookingController.listFlights(30));
        System.out.print("✈️ Flight id: ");
        int flightId = readInt();

        System.out.println("\n" + c("4️⃣ Nights", ANSI_BLUE));
        System.out.print("🌙 Nights (1..30): ");
        int nights = readIntInRange(1, 30);

        System.out.println("\n" + c("5️⃣ Extras package", ANSI_BLUE));
        ExtraSelection extras = extrasPackageFlow();

        System.out.println("\n" + c("6️⃣ Payment method", ANSI_BLUE));
        System.out.print("💳 Method (CARD/CASH/TRANSFER): ");
        String method = readPaymentMethod();

        System.out.println("\n" + c("⏳ Creating booking...", ANSI_YELLOW));

        String result = bookingController.createBooking(passengerId, flightId, hotelId, nights, method, currentUserId, extras);



        Integer bookingId = tryExtractBookingId(result);
        if (bookingId != null) {
            seatSelectionFlow(bookingId, flightId, 1);

            System.out.println("\n" + c("🧾 --- BOOKING DETAILS ---", ANSI_CYAN));
            System.out.println(bookingController.getBookingDetails(bookingId));
        }
    }

    private void bookingWizardGroup() {
        System.out.println("\n" + c("👨‍👩‍👧‍👦 ========= BOOKING WIZARD (FAMILY/GROUP) =========", ANSI_CYAN));

        System.out.print("👥 How many travelers: ");
        int n = readIntInRange(1, 6);

        List<Integer> passengerIds = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            System.out.println("\n" + c("🧑 Traveler #" + i, ANSI_BLUE));

            System.out.print("👤 Name: ");
            String name = readLine();

            System.out.print("👤 Surname: ");
            String surname = readLine();

            System.out.print("⚧️ Gender (male/female): ");
            String gender = readLine();

            System.out.print("🎂 Age: ");
            int age = readIntInRange(0, 120);

            System.out.print("🛂 Passport number: ");
            String passport = readLine();

            int pid = bookingController.createPassenger(name, surname, gender, age, passport);
            passengerIds.add(pid);

            System.out.println(c("✅ Passenger created. ID=" + pid, ANSI_GREEN));





        }

        System.out.println("\n" + c("2️⃣ Choose hotel", ANSI_BLUE));
        System.out.println(bookingController.listHotels(30));
        System.out.print("🏨 Hotel id: ");
        int hotelId = readInt();

        System.out.println("\n" + c("3️⃣ Choose flight", ANSI_BLUE));
        System.out.println(bookingController.listFlights(30));
        System.out.print("✈️ Flight id: ");
        int flightId = readInt();

        System.out.println("\n" + c("4️⃣ Nights", ANSI_BLUE));
        System.out.print("🌙 Nights (1..30): ");
        int nights = readIntInRange(1, 30);

        System.out.println("\n" + c("5️⃣ Extras package", ANSI_BLUE));
        ExtraSelection extras = extrasPackageFlow();




        System.out.println("\n" + c("5️⃣ Payment method", ANSI_BLUE));
        System.out.print("💳 Method (CARD/CASH/TRANSFER): ");
        String method = readPaymentMethod();

        System.out.println("\n" + c("⏳ Creating group booking...", ANSI_YELLOW));


        int bookingId = bookingController.createGroupBooking(passengerIds, flightId, hotelId, nights, method, currentUserId, extras);

        System.out.println(c("✅ Group booking created! ID=" + bookingId, ANSI_GREEN));
        seatSelectionFlow(bookingId, flightId, passengerIds.size());

        System.out.println("\n" + c("🧾 --- BOOKING DETAILS ---", ANSI_CYAN));
        System.out.println(bookingController.getBookingDetails(bookingId));




    }

    private ExtraSelection extrasPackageFlow() {
        while (true) {
            System.out.println("1) 🟦 BASIC (no extras)");
            System.out.println("2) 🟨 COMFORT (20kg + meal)");
            System.out.println("3) 🟪 PREMIUM (30kg + meal + insurance + priority)");
            System.out.print("👉 Choose package: ");

            int x = readInt();
            switch (x) {
                case 1: return ExtraSelection.basic();
                case 2: return ExtraSelection.comfort();
                case 3: return ExtraSelection.premium();
                default: System.out.println(c("⚠️ Enter 1/2/3.", ANSI_YELLOW));
            }
        }
    }



    private void searchMenu() {
        while (true) {
            System.out.println("\n" + c("🔎 --- SEARCH ---", ANSI_PURPLE));
            System.out.println("1) ✈️ Search flights");
            System.out.println("2) 🏨 Search hotels");
            System.out.println("0) 🔙 Back");
            System.out.print("👉 Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> searchFlightsFlow();
                case 2 -> searchHotelsFlow();
                case 0 -> { return; }
                default -> System.out.println(c("⚠️ Enter 0..2", ANSI_YELLOW));
            }
        }
    }

    private void searchFlightsFlow() {
        System.out.println("\n" + c("✈️ --- SEARCH FLIGHTS ---", ANSI_PURPLE));

        System.out.println("🏙️ FROM cities:");
        System.out.println(searchController.getFlightsFromCities());
        System.out.print("➡️ From city: ");
        String fromCity = readLine();

        System.out.println("\n🏙️ TO cities:");
        System.out.println(searchController.getFlightsToCities());
        System.out.print("➡️ To city: ");
        String toCity = readLine();

        System.out.print("\n📅 From date (yyyy-mm-dd or -): ");
        String fromDate = readLine();
        System.out.print("📅 To date (yyyy-mm-dd or -): ");
        String toDate = readLine();

        System.out.print("\n🪑 Flight type (1-ECONOMY, 2-BUSINESS): ");
        String type = readLine();

        System.out.print("⬇️ Sort (1-CHEAPEST, 2-EARLIEST): ");
        String sort = readLine();

        System.out.println("\n" + c("✅ RESULT:", ANSI_GREEN));
        System.out.println(searchController.getFlightsByFilter(fromCity, toCity, fromDate, toDate, type, sort));
    }

    private void searchHotelsFlow() {
        System.out.println("\n" + c("🏨 --- SEARCH HOTELS ---", ANSI_PURPLE));

        System.out.println("🏙️ Cities:");
        System.out.println(searchController.getHotelsCities());
        System.out.print("➡️ City: ");
        String city = readLine();

        System.out.print("⭐ Min stars (1..5): ");
        int minStars = readIntInRange(1, 5);

        System.out.print("💰 Max price per night: ");
        int maxPrice = readInt();

        System.out.print("⬇️ Sort (1-CHEAPEST, 2-HIGHEST STARS): ");
        String sort = readLine();

        System.out.println("\n" + c("✅ RESULT:", ANSI_GREEN));
        System.out.println(searchController.getHotelsByFilter(city, minStars, maxPrice, sort));
    }


    private void cancellationMenu() {
        while (true) {
            System.out.println("\n" + c("❌ --- CANCELLATION ---", ANSI_PURPLE));
            System.out.println("1) 🧾 Cancel booking");
            System.out.println("0) 🔙 Back");
            System.out.print("👉 Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> {
                    System.out.print("🧾 Booking id: ");
                    int bookingId = readInt();
                    System.out.println(cancellationController.cancel(bookingId));
                }
                case 0 -> { return; }
                default -> System.out.println(c("⚠️ Enter 0..1", ANSI_YELLOW));
            }
        }
    }


    private void reportsMenu() {
        if (reportController == null) {
            System.out.println(c("⚠️ Reports module is not connected yet.", ANSI_YELLOW));
            return;
        }

        while (true) {
            System.out.println("\n" + c("📊 --- REPORTS ---", ANSI_PURPLE));
            System.out.println("1) 💼 Revenue by Airline");
            System.out.println("2) 🗺️ Top Routes");
            System.out.println("3) 🏨 Revenue by Hotel City");
            System.out.println("4) ❌ Cancellation Statistics");
            System.out.println("5) 🌙 Average Stay by Hotel City");
            System.out.println("0) 🔙 Back");
            System.out.print("👉 Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> System.out.println(reportController.revenueByAirline());
                case 2 -> System.out.println(reportController.topRoutes());
                case 3 -> System.out.println(reportController.revenueByHotelCity());
                case 4 -> System.out.println(reportController.cancellationStats());
                case 5 -> System.out.println(reportController.averageStayByHotelCity());
                case 0 -> { return; }
                default -> System.out.println(c("⚠️ Enter 0..5", ANSI_YELLOW));
            }
        }
    }


    private void applyPromoFlow() {
        if (promoController == null) {
            System.out.println(c("⚠️ Promo module is not connected yet.", ANSI_YELLOW));
            return;
        }

        System.out.print("🧾 Booking id: ");
        int bookingId = readInt();

        System.out.print("🏷️ Promo code: ");
        String code = readLine();

        System.out.println(promoController.applyPromo(bookingId, code));

        System.out.println("\n" + c("🧾 --- BOOKING DETAILS ---", ANSI_CYAN));
        System.out.println(bookingController.getBookingDetails(bookingId));
    }



    private void adminMenu() {
        if (adminController == null) {
            System.out.println(c("⚠️ Admin module is not connected.", ANSI_YELLOW));
            return;
        }

        while (true) {
            System.out.println("\n" + c("👑 --- ADMIN PANEL ---", ANSI_PURPLE));
            System.out.println("1) ➕ Add airline");
            System.out.println("2) ➕ Add hotel");
            System.out.println("3) ➕ Add flight");
            System.out.println("4) 📋 View all bookings");
            System.out.println("5) 📊 Revenue reports");
            System.out.println("0) 🔙 Back");
            System.out.print("👉 Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> {
                    System.out.print("✈️ Airline name: ");
                    String name = readLine();
                    System.out.println(adminController.addAirline(name));
                }
                case 2 -> {
                    System.out.print("🏨 Hotel name: ");
                    String name = readLine();
                    System.out.print("🏙️ City: ");
                    String city = readLine();
                    System.out.print("⭐ Stars (1..5): ");
                    int stars = readIntInRange(1, 5);
                    System.out.print("💰 Price per night: ");
                    double price = readDouble();
                    System.out.print("🚪 Available rooms: ");
                    int rooms = readIntInRange(0, 9999);

                    System.out.println(adminController.addHotel(name, city, stars, price, rooms));
                }
                case 3 -> {
                    System.out.print("🆔 Airline id: ");
                    int airlineId = readInt();
                    System.out.print("✈️ Flight code: ");
                    String code = readLine();
                    System.out.print("🏙️ From city: ");
                    String from = readLine();
                    System.out.print("🏙️ To city: ");
                    String to = readLine();

                    System.out.print("🕒 Departure (yyyy-mm-dd hh:mm:ss): ");
                    String dep = readLine();
                    System.out.print("🕒 Arrival (yyyy-mm-dd hh:mm:ss): ");
                    String arr = readLine();

                    System.out.print("🪑 Class type (ECONOMY/BUSINESS): ");
                    String classType = readLine().toUpperCase();

                    System.out.print("💵 Base price: ");
                    double base = readDouble();

                    System.out.print("💺 Available seats: ");
                    int seats = readIntInRange(0, 9999);

                    System.out.println(adminController.addFlight(airlineId, code, from, to, dep, arr, classType, base, seats));
                }
                case 4 -> System.out.println(adminController.listAllBookings());
                case 5 -> System.out.println(adminController.revenueReports());
                case 0 -> { return; }
                default -> System.out.println(c("⚠️ Enter 0..5", ANSI_YELLOW));
            }
        }
    }



    private void seatSelectionFlow(int bookingId, int flightId, int travelersCount) {
        System.out.println("\n" + c("💺 --- SEAT SELECTION ---", ANSI_PURPLE));
        System.out.println("🗺️ Seat map (XX = occupied):");
        System.out.println(bookingController.getSeatMap(flightId));

        List<String> seats = readSeatCodes(travelersCount);

        String resp = bookingController.chooseSeats(bookingId, flightId, seats);
        System.out.println(resp);

        System.out.println("\n🗺️ Updated seat map:");
        System.out.println(bookingController.getSeatMap(flightId));
    }

    private List<String> readSeatCodes(int expectedCount) {
        while (true) {
            System.out.print("💺 Enter " + expectedCount + " seat codes (A1,B2,F30): ");
            String s = readLine().toUpperCase().replace(" ", "");
            if (s.isEmpty()) continue;

            String[] parts = s.split(",");
            if (parts.length != expectedCount) {
                System.out.println(c("⚠️ You must enter exactly " + expectedCount + " seats.", ANSI_YELLOW));
                continue;
            }

            List<String> result = new ArrayList<>();
            boolean ok = true;

            for (String code : parts) {
                if (!isValidSeatCode(code)) {
                    System.out.println(c("❌ Invalid seat: " + code + " (format A1..F30)", ANSI_RED));
                    ok = false;
                    break;
                }
                result.add(code);
            }

            if (ok) return result;
        }
    }

    private boolean isValidSeatCode(String code) {
        if (code == null || code.length() < 2 || code.length() > 3) return false;

        char letter = code.charAt(0);
        if (letter < 'A' || letter > 'F') return false;

        String numPart = code.substring(1);
        int row;
        try {
            row = Integer.parseInt(numPart);
        } catch (Exception e) {
            return false;
        }
        return row >= 1 && row <= 30;
    }


    private int readInt() {
        while (true) {
            String s = scanner.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                System.out.print(c("🔢 Enter a number: ", ANSI_YELLOW));
            }
        }
    }

    private double readDouble() {
        while (true) {
            String s = scanner.nextLine().trim().replace(",", ".");
            try {
                return Double.parseDouble(s);
            } catch (Exception e) {
                System.out.print(c("🔢 Enter a number: ", ANSI_YELLOW));
            }
        }
    }

    private int readIntInRange(int min, int max) {
        while (true) {
            int x = readInt();
            if (x >= min && x <= max) return x;
            System.out.print(c("🔢 Enter number in range " + min + ".." + max + ": ", ANSI_YELLOW));
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
            System.out.print(c("💳 Enter CARD/CASH/TRANSFER: ", ANSI_YELLOW));
        }
    }

    private Integer tryExtractBookingId(String text) {
        if (text == null) return null;
        int idx = text.indexOf("ID=");
        if (idx < 0) return null;
        idx += 3;
        StringBuilder sb = new StringBuilder();
        while (idx < text.length()) {
            char ch = text.charAt(idx);
            if (!Character.isDigit(ch)) break;
            sb.append(ch);
            idx++;
        }
        if (sb.length() == 0) return null;
        try { return Integer.parseInt(sb.toString()); } catch (Exception e) { return null; }
    }
}
