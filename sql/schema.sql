ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'MANAGER'
    CHECK (role IN ('ADMIN','MANAGER'));

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
CREATE TABLE IF NOT EXISTS passengers (
                                          id SERIAL PRIMARY KEY,
                                          full_name VARCHAR(100) NOT NULL,
    passport_number VARCHAR(30) NOT NULL UNIQUE,
    birth_date DATE NOT NULL,
    nationality VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- удобный индекс для поиска
CREATE INDEX IF NOT EXISTS ix_passengers_passport ON passengers(passport_number);
CREATE TABLE IF NOT EXISTS loyalty_accounts (
                                                passenger_id INT PRIMARY KEY,
                                                total_trips INT NOT NULL DEFAULT 0 CHECK (total_trips >= 0),
    discount_percent INT NOT NULL DEFAULT 0 CHECK (discount_percent BETWEEN 0 AND 30),
    tier VARCHAR(20) NOT NULL DEFAULT 'BRONZE'
    CHECK (tier IN ('BRONZE','SILVER','GOLD','VIP')),

    CONSTRAINT fk_loyalty_passenger
    FOREIGN KEY (passenger_id)
    REFERENCES passengers(id)
    ON DELETE CASCADE
    );
CREATE TABLE IF NOT EXISTS airlines (
                                        id SERIAL PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL UNIQUE,
    country VARCHAR(50) NOT NULL
    );
CREATE TABLE IF NOT EXISTS airlines (
                                        id SERIAL PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL UNIQUE,
    country VARCHAR(50) NOT NULL
    );
CREATE TABLE IF NOT EXISTS flights (
                                       id SERIAL PRIMARY KEY,
                                       airline_id INT NOT NULL,
                                       flight_code VARCHAR(10) NOT NULL UNIQUE,     -- например KC123
    from_city VARCHAR(50) NOT NULL,
    to_city VARCHAR(50) NOT NULL,
    departure_time TIMESTAMP NOT NULL,
    arrival_time TIMESTAMP NOT NULL,
    base_price NUMERIC(10,2) NOT NULL CHECK (base_price >= 0),
    class_type VARCHAR(20) NOT NULL CHECK (class_type IN ('ECONOMY', 'BUSINESS')),
    available_seats INT NOT NULL CHECK (available_seats >= 0),

    CONSTRAINT fk_flight_airline
    FOREIGN KEY (airline_id)
    REFERENCES airlines(id),

    CONSTRAINT chk_flight_times
    CHECK (arrival_time > departure_time),

    CONSTRAINT chk_flight_route
    CHECK (from_city <> to_city)
    );

CREATE INDEX IF NOT EXISTS ix_flights_route ON flights(from_city, to_city);
CREATE INDEX IF NOT EXISTS ix_flights_departure ON flights(departure_time);


CREATE TABLE IF NOT EXISTS hotels (
                                      id SERIAL PRIMARY KEY,
                                      name VARCHAR(120) NOT NULL,
    stars INT NOT NULL CHECK (stars BETWEEN 1 AND 5),
    city VARCHAR(50) NOT NULL,
    address VARCHAR(120) NOT NULL,
    price_per_night NUMERIC(10,2) NOT NULL CHECK (price_per_night >= 0),
    available_rooms INT NOT NULL CHECK (available_rooms >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS ix_hotels_city ON hotels(city);
CREATE INDEX IF NOT EXISTS ix_hotels_stars ON hotels(stars);

CREATE TABLE IF NOT EXISTS bookings (
                                        id SERIAL PRIMARY KEY,
                                        passenger_id INT NOT NULL,
                                        flight_id INT NOT NULL,
                                        hotel_id INT NOT NULL,
                                        nights INT NOT NULL CHECK (nights BETWEEN 1 AND 30),

    -- calculated fields
    total_price NUMERIC(10,2) NOT NULL CHECK (total_price >= 0),
    status VARCHAR(20) NOT NULL
    CHECK (status IN ('CREATED','CONFIRMED','CANCELLED')),

    -- who created booking (manager/admin)
    created_by_user_id INT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_booking_passenger
    FOREIGN KEY (passenger_id)
    REFERENCES passengers(id),

    CONSTRAINT fk_booking_flight
    FOREIGN KEY (flight_id)
    REFERENCES flights(id),

    CONSTRAINT fk_booking_hotel
    FOREIGN KEY (hotel_id)
    REFERENCES hotels(id),

    CONSTRAINT fk_booking_created_by
    FOREIGN KEY (created_by_user_id)
    REFERENCES users(id)
    ON DELETE SET NULL
    );

CREATE INDEX IF NOT EXISTS ix_bookings_passenger ON bookings(passenger_id);
CREATE INDEX IF NOT EXISTS ix_bookings_status ON bookings(status);
CREATE INDEX IF NOT EXISTS ix_bookings_created_at ON bookings(created_at);
CREATE TABLE IF NOT EXISTS payments (
                                        id SERIAL PRIMARY KEY,
                                        booking_id INT NOT NULL UNIQUE,
                                        amount NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
    method VARCHAR(30) NOT NULL CHECK (method IN ('CARD','CASH','TRANSFER')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PAID','REFUNDED')),
    paid_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_booking
    FOREIGN KEY (booking_id)
    REFERENCES bookings(id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS ix_payments_status ON payments(status);
CREATE TABLE IF NOT EXISTS payments (
                                        id SERIAL PRIMARY KEY,
                                        booking_id INT NOT NULL UNIQUE,
                                        amount NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
    method VARCHAR(30) NOT NULL CHECK (method IN ('CARD','CASH','TRANSFER')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PAID','REFUNDED')),
    paid_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_booking
    FOREIGN KEY (booking_id)
    REFERENCES bookings(id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS ix_payments_status ON payments(status);

CREATE TABLE IF NOT EXISTS cancellations (
                                             id SERIAL PRIMARY KEY,
                                             booking_id INT NOT NULL UNIQUE,
                                             cancel_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             refund_amount NUMERIC(10,2) NOT NULL CHECK (refund_amount >= 0),
    reason VARCHAR(200),

    CONSTRAINT fk_cancel_booking
    FOREIGN KEY (booking_id)
    REFERENCES bookings(id)
    ON DELETE CASCADE
    );
CREATE TABLE IF NOT EXISTS booking_history (
                                               id SERIAL PRIMARY KEY,
                                               booking_id INT NOT NULL,
                                               action VARCHAR(20) NOT NULL CHECK (action IN ('CREATED','CONFIRMED','CANCELLED','REFUNDED')),
    action_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details VARCHAR(300),

    CONSTRAINT fk_history_booking
    FOREIGN KEY (booking_id)
    REFERENCES bookings(id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS ix_history_booking ON booking_history(booking_id);
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'MANAGER'
    CHECK (role IN ('ADMIN','MANAGER'));

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;