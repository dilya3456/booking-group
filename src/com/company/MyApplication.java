package com.company;

import com.company.controllers.BookingController;
import com.company.controllers.CancellationController;
import com.company.controllers.SearchController;
import com.company.controllers.interfaces.ISearchController;
import com.company.controllers.interfaces.IUserController;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MyApplication {
    private final IUserController userController;
    private final BookingController bookingController;
    private final CancellationController cancellationController;
    private final ISearchController searchController;

    private final Scanner scanner = new Scanner(System.in);


    public MyApplication(IUserController userController) {
        this(userController, null, null, null);
    }


    public MyApplication(IUserController userController, CancellationController cancellationController) {
        this(userController, null, cancellationController, null);
    }


    public MyApplication(IUserController userController, BookingController bookingController, CancellationController cancellationController, ISearchController searchController) {
        this.userController = userController;
        this.bookingController = bookingController;
        this.cancellationController = cancellationController;
        this.searchController = searchController;
    }

    private void mainMenu() {
        System.out.println();
        System.out.println("Welcome to Smart Travel Booking");
        System.out.println("Select option:");

        System.out.println("1. Get all users");
        System.out.println("2. Get user by id");
        System.out.println("3. Create user");

        System.out.println("4. List passengers");
        System.out.println("5. List flights");
        System.out.println("6. List hotels");

        System.out.println("7. Create booking (TRANSACTION)");
        System.out.println("8. View booking details");

        System.out.println("9. Cancel booking (refund policy)");

        System.out.println("10. Search flights");
        System.out.println("11. Search hotels");

        System.out.println("0. Exit");

        System.out.println();
        System.out.print("Enter option: ");
    }

    public void start() {
        while (true) {
            mainMenu();
            try {
                int option = scanner.nextInt();

                switch (option) {
                    case 1:
                        getAllUsersMenu();
                        break;
                    case 2:
                        getUserByIdMenu();
                        break;
                    case 3:
                        createUserMenu();
                        break;

                    case 4:
                        listPassengersMenu();
                        break;
                    case 5:
                        listFlightsMenu();
                        break;
                    case 6:
                        listHotelsMenu();
                        break;

                    case 7:
                        createBookingMenu();
                        break;
                    case 8:
                        bookingDetailsMenu();
                        break;

                    case 9:
                        cancelBookingMenu();
                        break;

                    case 10:
                        searchFlightsMenu();
                        break;
                    case 11:
                        searchHotelsMenu();
                        break;
                    default:
                        return;
                }

            } catch (InputMismatchException e) {
                System.out.println("Input must be integer.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.println("*************************");
        }
    }



    public void getAllUsersMenu() {
        String response = userController.getAllUsers();
        System.out.println(response);
    }

    public void getUserByIdMenu() {
        System.out.println("Please enter id:");
        int id = scanner.nextInt();
        String response = userController.getUser(id);
        System.out.println(response);
    }

    public void createUserMenu() {
        System.out.println("Please enter name:");
        String name = scanner.next();
        System.out.println("Please enter surname:");
        String surname = scanner.next();
        System.out.println("Please enter gender (male/female):");
        String gender = scanner.next();

        String response = userController.createUser(name, surname, gender);
        System.out.println(response);
    }



    private void listPassengersMenu() {
        if (bookingController == null) {
            System.out.println("Booking module is not connected yet.");
            return;
        }
        System.out.println(bookingController.listPassengers(30));
    }

    private void listFlightsMenu() {
        if (bookingController == null) {
            System.out.println("Booking module is not connected yet.");
            return;
        }
        System.out.println(bookingController.listFlights(30));
    }

    private void listHotelsMenu() {
        if (bookingController == null) {
            System.out.println("Booking module is not connected yet.");
            return;
        }
        System.out.println(bookingController.listHotels(30));
    }

    private void createBookingMenu() {
        if (bookingController == null) {
            System.out.println("Booking module is not connected yet.");
            return;
        }

        System.out.println("Passenger id:");
        int passengerId = scanner.nextInt();

        System.out.println("Flight id:");
        int flightId = scanner.nextInt();

        System.out.println("Hotel id:");
        int hotelId = scanner.nextInt();

        System.out.println("Nights (1..30):");
        int nights = scanner.nextInt();

        System.out.println("Payment method (CARD/CASH/TRANSFER):");
        String method = scanner.next();


        Integer createdByUserId = 2;

        String resp = bookingController.createBooking(passengerId, flightId, hotelId, nights, method, createdByUserId);
        System.out.println(resp);
    }

    private void bookingDetailsMenu() {
        if (bookingController == null) {
            System.out.println("Booking module is not connected yet.");
            return;
        }

        System.out.println("Booking id:");
        int bookingId = scanner.nextInt();

        System.out.println(bookingController.getBookingDetails(bookingId));
    }



    public void cancelBookingMenu() {
        if (cancellationController == null) {
            System.out.println("Cancellation module is not connected yet.");
            return;
        }

        System.out.println("Enter booking id to cancel:");
        int bookingId = scanner.nextInt();

        String response = cancellationController.cancel(bookingId);
        System.out.println(response);
    }



    public void searchFlightsMenu() {
        if (searchController == null) {
            System.out.println("Search module is not connected yet.");
            return;
        }

        System.out.println("Select FROM city:");
        String fromCities = searchController.getFlightsFromCities();
        System.out.println(fromCities);
        String fromCity = scanner.next();

        System.out.println("Select TO city:");
        String toCities = searchController.getFlightsToCities();
        System.out.println(toCities);
        String toCity = scanner.next();

        System.out.println("Enter FROM date (yyyy-mm-dd | -):");
        String fromDate = scanner.next();

        System.out.println("Enter TO date (yyyy-mm-dd | -):");
        String toDate = scanner.next();

        System.out.println("Select flight type (1 - ECONOMY | 2 - BUSINESS):");
        String type = scanner.next();

        System.out.println("Select sort type (1 - CHEAPEST | 2 - EARLIEST):");
        String sort = scanner.next();

        String response = searchController.getFlightsByFilter(fromCity, toCity, fromDate, toDate, type, sort);
        System.out.println(response);
    }

    public void searchHotelsMenu() {
        if (searchController == null) {
            System.out.println("Search module is not connected yet.");
            return;
        }

        System.out.println("Select FROM city:");
        String cities = searchController.getHotelsCities();
        System.out.println(cities);
        String city = scanner.next();

        System.out.println("Enter min stars:");
        int minStars = scanner.nextInt();

        System.out.println("Enter max price:");
        int maxPrice = scanner.nextInt();

        System.out.println("Select sort type (1 - CHEAPEST | 2 - HIGHEST STARS):");
        String sort = scanner.next();

        String response = searchController.getHotelsByFilter(city, minStars, maxPrice, sort);
        System.out.println(response);
    }
}
