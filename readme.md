Project Title

Smart Travel Booking System (Console + PostgreSQL + JDBC)

Topic / Definition

This project is a console-based travel booking application developed in Java with PostgreSQL database integration using JDBC.
The system simulates a real travel platform where users can book trips that include:
• Flight selection
• Hotel selection
• Passenger booking
• Payment recording
• Cancellation with refund policy
• Analytics reports
• Search and recommendations

The main goal is to demonstrate not only CRUD operations but also business logic, including transactions, seat/room availability control, and reporting.

⸻

Core Features (Business Logic)

Booking (Transaction-Based)
• Create booking by selecting:
• passenger
• flight
• hotel
• number of nights
• payment method
• Uses DB transaction
• Automatically creates:
• booking record
• payment record
• booking history record
• Decreases:
• flight available seats
• hotel available rooms
• Includes price calculation logic (discounts, markups)

Cancellation (Refund Policy)
• Cancel booking by booking ID
• Applies refund rules
• Updates cancellation info in database

Reports (Analytics)
• Revenue by airline
• Top routes
• Revenue by hotel city
• Cancellation statistics
• Average stay by hotel city

Search / Recommendation
• Search flights with filters
• Search hotels with filters
• Find best travel combinations (recommendation logic)

⸻

Database Connection & Usage
• PostgreSQL is used as main DB
• Connected via JDBC
• Code is structured so DB can be changed without rewriting business logic (DI pattern with IDB)
• Multiple relational tables with real connections and constraints

⸻

Project Structure

Project follows a clean layered architecture:
• controllers — application interface for console
• services — business logic (transactions, validations, calculations)
• repositories — SQL queries and DB access
• models — entities
• data — DB connection layer (PostgresDB, IDB)
• console UI — MyApplication

⸻

Team Contribution

Dilnaz — Project Lead / Core Developer
• Designed overall project architecture and structure
• Created and managed database schema (PostgreSQL, pgAdmin4)
• Implemented Booking module (transaction-based logic)
• Integrated all modules into the console application
• Managed repository, merging branches, project coordination

Gaziza — Cancellation Module
• Implemented Cancellation Service + Controller
• Added refund/cancellation business logic
• Integrated with database operations

Dias — Reports Module
• Implemented Reports/Analytics
• Created SQL-based statistics and reporting:
• revenue analytics
• route popularity
• cancellations stats

Zharas — Search Module
• Implemented Search & Recommendation
• Developed logic for searching:
• flights by filters
• hotels by filters
• Added recommendation option (best combination)