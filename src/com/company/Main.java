package com.company;

import com.company.controllers.SearchController;
import com.company.controllers.UserController;
import com.company.controllers.interfaces.ISearchController;
import com.company.controllers.interfaces.IUserController;
import com.company.data.PostgresDB;
import com.company.data.interfaces.IDB;
import com.company.repositories.SearchRepository;
import com.company.repositories.UserRepository;
import com.company.repositories.interfaces.ISearchRepository;
import com.company.repositories.interfaces.IUserRepository;


import com.company.controllers.CancellationController;
import com.company.services.CancellationService;


import com.company.controllers.BookingController;
import com.company.repositories.BookingRepository;
import com.company.services.BookingService;
import com.company.services.PriceCalculatorService;

public class Main {
    public static void main(String[] args) {


        IDB db = new PostgresDB("jdbc:postgresql://localhost:5432", "postgres", "0000", "somedb");


        IUserRepository userRepo = new UserRepository(db);
        IUserController userController = new UserController(userRepo);


        BookingRepository bookingRepo = new BookingRepository(db);
        PriceCalculatorService priceCalc = new PriceCalculatorService();
        BookingService bookingService = new BookingService(bookingRepo, priceCalc);
        BookingController bookingController = new BookingController(bookingService);


        CancellationService cancellationService = new CancellationService(db);
        CancellationController cancellationController = new CancellationController(cancellationService);


        ISearchRepository searchRepo = new SearchRepository(db);
        ISearchController searchController = new SearchController(searchRepo);


        MyApplication app = new MyApplication(userController, bookingController, cancellationController, searchController);
        app.start();

        db.close();
    }
}
