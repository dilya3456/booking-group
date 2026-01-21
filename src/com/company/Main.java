package com.company;

import com.company.controllers.BookingController;
import com.company.controllers.CancellationController;
import com.company.controllers.SearchController;
import com.company.controllers.UserController;
import com.company.controllers.AuthController;

import com.company.controllers.interfaces.IBookingController;
import com.company.controllers.interfaces.ICancellationController;
import com.company.controllers.interfaces.ISearchController;
import com.company.controllers.interfaces.IUserController;

import com.company.data.PostgresDB;
import com.company.data.interfaces.IDB;

import com.company.repositories.BookingRepository;
import com.company.repositories.SearchRepository;
import com.company.repositories.UserRepository;
import com.company.repositories.AuthRepository;

import com.company.repositories.interfaces.IBookingRepository;
import com.company.repositories.interfaces.ISearchRepository;
import com.company.repositories.interfaces.IUserRepository;
import com.company.repositories.interfaces.IAuthRepository;

import com.company.services.BookingService;
import com.company.services.CancellationService;
import com.company.services.PriceCalculatorService;
import com.company.services.AuthService;

public class Main {
    public static void main(String[] args) {

        IDB db = new PostgresDB("jdbc:postgresql://localhost:5432", "postgres", "0000", "somedb");

        IUserRepository userRepo = new UserRepository(db);
        IUserController userController = new UserController(userRepo);

        IBookingRepository bookingRepo = new BookingRepository(db);
        PriceCalculatorService priceCalc = new PriceCalculatorService();
        BookingService bookingService = new BookingService(bookingRepo, priceCalc);
        IBookingController bookingController = new BookingController(bookingService);

        CancellationService cancellationService = new CancellationService(db);
        ICancellationController cancellationController = new CancellationController(cancellationService);

        ISearchRepository searchRepo = new SearchRepository(db);
        ISearchController searchController = new SearchController(searchRepo);

        IAuthRepository authRepo = new AuthRepository(db);
        AuthService authService = new AuthService(authRepo);
        AuthController authController = new AuthController(authService);

        MyApplication app = new MyApplication(
                userController,
                bookingController,
                cancellationController,
                searchController,
                authController
        );
        app.start();

        db.close();
    }
}
