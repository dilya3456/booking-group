package com.company;

import com.company.controllers.*;
import com.company.controllers.interfaces.*;

import com.company.data.PostgresDB;
import com.company.data.interfaces.IDB;

import com.company.repositories.*;
import com.company.repositories.interfaces.*;

import com.company.services.*;

public class Main {
    public static void main(String[] args) {

        IDB db = PostgresDB.getInstance(
                "jdbc:postgresql://localhost:5432",
                "postgres",
                "0000",
                "somedb"
        );



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


        ReportRepository reportRepo = new ReportRepository(db);
        IReportController reportController = new ReportController(reportRepo);


        IAdminRepository adminRepo = new AdminRepository(db);
        AdminService adminService = new AdminService(adminRepo);
        IAdminController adminController = new AdminController(adminService);


        IPromoController promoController = null;


        MyApplication app = new MyApplication(
                userController,
                bookingController,
                cancellationController,
                searchController,
                authController,
                reportController,
                adminController,
                promoController
        );

        app.start();

        db.close();
    }
}
