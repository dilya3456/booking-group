package com.company;

import com.company.controllers.UserController;
import com.company.controllers.interfaces.IUserController;
import com.company.data.PostgresDB;
import com.company.data.interfaces.IDB;
import com.company.repositories.UserRepository;
import com.company.repositories.interfaces.IUserRepository;

// cancellation
import com.company.controllers.CancellationController;
import com.company.services.CancellationService;

// booking
import com.company.controllers.BookingController;
import com.company.repositories.BookingRepository;
import com.company.services.BookingService;
import com.company.services.PriceCalculatorService;

public class Main {
    public static void main(String[] args) {

        // ✅ оставь somedb если так называется твоя база
        IDB db = new PostgresDB("jdbc:postgresql://localhost:5432", "postgres", "0000", "somedb");

        // users module
        IUserRepository userRepo = new UserRepository(db);
        IUserController userController = new UserController(userRepo);

        // booking module
        BookingRepository bookingRepo = new BookingRepository(db);
        PriceCalculatorService priceCalc = new PriceCalculatorService();
        BookingService bookingService = new BookingService(bookingRepo, priceCalc);
        BookingController bookingController = new BookingController(bookingService);

        // cancellation module
        CancellationService cancellationService = new CancellationService(db);
        CancellationController cancellationController = new CancellationController(cancellationService);

        // app
        MyApplication app = new MyApplication(userController, bookingController, cancellationController);
        app.start();

        db.close();
    }
}
