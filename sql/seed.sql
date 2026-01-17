INSERT INTO users(name, surname, gender, role)
VALUES
    ('Admin','One', true, 'ADMIN'),
    ('Manager','A', false, 'MANAGER'),
    ('Manager','B', true, 'MANAGER')
    ON CONFLICT DO NOTHING;
INSERT INTO airlines(name, country)
VALUES
    ('Air Astana','Kazakhstan'),
    ('SCAT Airlines','Kazakhstan'),
    ('FlyDubai','UAE'),
    ('Emirates','UAE'),
    ('Turkish Airlines','Turkey'),
    ('Qatar Airways','Qatar'),
    ('Lufthansa','Germany'),
    ('Wizz Air','Hungary'),
    ('Air Arabia','UAE'),
    ('Azerbaijan Airlines','Azerbaijan')
    ON CONFLICT (name) DO NOTHING;
INSERT INTO flights(airline_id, flight_code, from_city, to_city, departure_time, arrival_time, base_price, class_type, available_seats)
VALUES
    (1,'KC101','Almaty','Dubai', NOW()+INTERVAL '72 hours', NOW()+INTERVAL '76 hours', 200,'ECONOMY',20),
    (1,'KC102','Almaty','Istanbul', NOW()+INTERVAL '48 hours', NOW()+INTERVAL '54 hours', 180,'ECONOMY',18),
    (1,'KC103','Astana','Dubai', NOW()+INTERVAL '96 hours', NOW()+INTERVAL '100 hours', 210,'BUSINESS',10),

    (2,'DV201','Karaganda','Almaty', NOW()+INTERVAL '36 hours', NOW()+INTERVAL '37 hours', 60,'ECONOMY',40),
    (2,'DV202','Almaty','Aktau', NOW()+INTERVAL '80 hours', NOW()+INTERVAL '82 hours', 75,'ECONOMY',35),
    (2,'DV203','Astana','Almaty', NOW()+INTERVAL '110 hours', NOW()+INTERVAL '111 hours', 55,'ECONOMY',50),

    (3,'FZ301','Dubai','Almaty', NOW()+INTERVAL '70 hours', NOW()+INTERVAL '74 hours', 220,'ECONOMY',22),
    (3,'FZ302','Dubai','Astana', NOW()+INTERVAL '90 hours', NOW()+INTERVAL '94 hours', 240,'BUSINESS',8),
    (3,'FZ303','Dubai','Istanbul', NOW()+INTERVAL '50 hours', NOW()+INTERVAL '54 hours', 160,'ECONOMY',30),

    (4,'EK401','Dubai','Istanbul', NOW()+INTERVAL '120 hours', NOW()+INTERVAL '124 hours', 260,'BUSINESS',12),
    (4,'EK402','Dubai','Berlin', NOW()+INTERVAL '140 hours', NOW()+INTERVAL '146 hours', 320,'BUSINESS',15),
    (4,'EK403','Dubai','Doha', NOW()+INTERVAL '44 hours', NOW()+INTERVAL '46 hours', 140,'ECONOMY',28),

    (5,'TK501','Istanbul','Almaty', NOW()+INTERVAL '60 hours', NOW()+INTERVAL '66 hours', 210,'ECONOMY',19),
    (5,'TK502','Istanbul','Astana', NOW()+INTERVAL '100 hours', NOW()+INTERVAL '106 hours', 230,'BUSINESS',10),
    (5,'TK503','Istanbul','Dubai', NOW()+INTERVAL '130 hours', NOW()+INTERVAL '134 hours', 190,'ECONOMY',25),

    (6,'QR601','Doha','Dubai', NOW()+INTERVAL '52 hours', NOW()+INTERVAL '54 hours', 150,'ECONOMY',32),
    (6,'QR602','Doha','Istanbul', NOW()+INTERVAL '95 hours', NOW()+INTERVAL '99 hours', 200,'BUSINESS',14),
    (6,'QR603','Doha','Berlin', NOW()+INTERVAL '160 hours', NOW()+INTERVAL '166 hours', 350,'BUSINESS',10),

    (7,'LH701','Berlin','Istanbul', NOW()+INTERVAL '75 hours', NOW()+INTERVAL '78 hours', 220,'ECONOMY',27),
    (7,'LH702','Berlin','Dubai', NOW()+INTERVAL '170 hours', NOW()+INTERVAL '176 hours', 360,'BUSINESS',9),
    (7,'LH703','Berlin','Almaty', NOW()+INTERVAL '190 hours', NOW()+INTERVAL '198 hours', 420,'BUSINESS',6),

    (8,'WZ801','Budapest','Istanbul', NOW()+INTERVAL '88 hours', NOW()+INTERVAL '92 hours', 120,'ECONOMY',45),
    (8,'WZ802','Budapest','Dubai', NOW()+INTERVAL '200 hours', NOW()+INTERVAL '206 hours', 260,'ECONOMY',33),
    (8,'WZ803','Budapest','Berlin', NOW()+INTERVAL '115 hours', NOW()+INTERVAL '116 hours', 90,'ECONOMY',55),

    (9,'G901','Sharjah','Dubai', NOW()+INTERVAL '24 hours', NOW()+INTERVAL '25 hours', 70,'ECONOMY',60),
    (9,'G902','Sharjah','Istanbul', NOW()+INTERVAL '66 hours', NOW()+INTERVAL '70 hours', 160,'ECONOMY',34),
    (9,'G903','Sharjah','Almaty', NOW()+INTERVAL '150 hours', NOW()+INTERVAL '156 hours', 280,'BUSINESS',11),

    (10,'J2101','Baku','Dubai', NOW()+INTERVAL '90 hours', NOW()+INTERVAL '94 hours', 180,'ECONOMY',29),
    (10,'J2102','Baku','Istanbul', NOW()+INTERVAL '125 hours', NOW()+INTERVAL '128 hours', 130,'ECONOMY',40),
    (10,'J2103','Baku','Almaty', NOW()+INTERVAL '210 hours', NOW()+INTERVAL '216 hours', 320,'BUSINESS',7)
    ON CONFLICT (flight_code) DO NOTHING;
INSERT INTO hotels(name, stars, city, address, price_per_night, available_rooms)
VALUES
    ('Skyline Palace',5,'Dubai','Downtown 1',180,8),
    ('Blue Marina',4,'Dubai','Marina 10',95,18),
    ('Desert Rose',3,'Dubai','Al Barsha 5',70,25),
    ('Sunset Resort',5,'Dubai','JBR 2',210,6),
    ('Budget Inn',2,'Dubai','Deira 7',40,30),

    ('Istanbul Grand',5,'Istanbul','Sultanahmet 12',160,7),
    ('Golden Horn Hotel',4,'Istanbul','Fatih 5',110,14),
    ('Bosporus View',5,'Istanbul','Besiktas 3',190,5),
    ('Old City Stay',3,'Istanbul','Eminonu 8',75,22),
    ('Comfort Suites',4,'Istanbul','Taksim 4',120,12),

    ('Berlin Central',4,'Berlin','Mitte 1',140,11),
    ('Brandenburg Hotel',5,'Berlin','Tiergarten 2',220,4),
    ('Budget Berlin',2,'Berlin','Neukolln 9',55,30),
    ('Airport Lodge',3,'Berlin','Airport str 6',80,16),

    ('Doha Pearl',5,'Doha','The Pearl 9',240,6),
    ('Doha City Inn',4,'Doha','West Bay 3',150,10),
    ('Sand Hotel',3,'Doha','Old Town 2',85,18),

    ('Almaty Plaza',5,'Almaty','Dostyk 1',130,9),
    ('Astana Tower',5,'Astana','Left Bank 8',140,8),
    ('Karaganda Comfort',4,'Karaganda','Center 3',75,15)
;
INSERT INTO passengers(full_name, passport_number, birth_date, nationality)
VALUES
    ('Dilnaz D','KZ1000001','2008-04-01','Kazakhstan'),
    ('Tomi T','KZ1000002','2007-06-10','Kazakhstan'),
    ('Aluka A','KZ1000003','2006-02-15','Kazakhstan'),
    ('Medina M','KZ1000004','2007-11-03','Kazakhstan'),
    ('Asel S','KZ1000005','2006-08-20','Kazakhstan'),
    ('Tolkyn T','KZ1000006','2007-01-09','Kazakhstan'),
    ('Amina A','KZ1000007','2005-05-12','Kazakhstan'),
    ('Arman A','KZ1000008','2004-12-30','Kazakhstan'),
    ('Dias D','KZ1000009','2003-03-25','Kazakhstan'),
    ('Dana D','KZ1000010','2007-09-17','Kazakhstan'),

    ('Kamil K','KZ1000011','2002-03-11','Kazakhstan'),
    ('Aruzhan A','KZ1000012','2005-07-07','Kazakhstan'),
    ('Nursultan N','KZ1000013','2001-10-10','Kazakhstan'),
    ('Zarina Z','KZ1000014','2004-01-21','Kazakhstan'),
    ('Ilyas I','KZ1000015','2006-06-06','Kazakhstan'),

    ('Ali A','TR2000001','2000-01-01','Turkey'),
    ('Zeynep Z','TR2000002','2002-02-02','Turkey'),
    ('Emre E','TR2000003','1999-03-03','Turkey'),
    ('Aylin A','TR2000004','2001-04-04','Turkey'),
    ('Mehmet M','TR2000005','2003-05-05','Turkey'),

    ('Omar O','AE3000001','1998-06-06','UAE'),
    ('Noor N','AE3000002','2000-07-07','UAE'),
    ('Hamad H','AE3000003','1997-08-08','UAE'),
    ('Sara S','AE3000004','1999-09-09','UAE'),
    ('Mariam M','AE3000005','2002-10-10','UAE'),

    ('Ahmed A','QA4000001','1996-11-11','Qatar'),
    ('Lina L','QA4000002','2001-12-12','Qatar'),

    ('Hans H','DE5000001','1995-01-13','Germany'),
    ('Julia J','DE5000002','1996-02-14','Germany'),
    ('Mila M','DE5000003','1998-03-15','Germany'),

    ('Adam A','HU6000001','2000-04-16','Hungary'),
    ('Eva E','HU6000002','2001-05-17','Hungary'),

    ('Rashad R','AZ7000001','1999-06-18','Azerbaijan'),
    ('Leyla L','AZ7000002','2002-07-19','Azerbaijan'),
    ('Kamran K','AZ7000003','2003-08-20','Azerbaijan')
    ON CONFLICT (passport_number) DO NOTHING;
INSERT INTO loyalty_accounts(passenger_id, total_trips, discount_percent, tier)
SELECT id,
       (RANDOM()*9)::int,
    CASE
        WHEN (RANDOM()*9)::int >= 8 THEN 15
           WHEN (RANDOM()*9)::int >= 5 THEN 10
           WHEN (RANDOM()*9)::int >= 3 THEN 5
           ELSE 0
END,
       CASE
           WHEN (RANDOM()*9)::int >= 8 THEN 'VIP'
           WHEN (RANDOM()*9)::int >= 5 THEN 'GOLD'
           WHEN (RANDOM()*9)::int >= 3 THEN 'SILVER'
           ELSE 'BRONZE'
END
FROM passengers
WHERE id <= 20
ON CONFLICT (passenger_id) DO NOTHING;
-- 25 bookings
INSERT INTO bookings(passenger_id, flight_id, hotel_id, nights, total_price, status, created_by_user_id)
SELECT
    (1 + (RANDOM()*34)::int) AS passenger_id,
    (1 + (RANDOM()*29)::int) AS flight_id,
    (1 + (RANDOM()*19)::int) AS hotel_id,
    (1 + (RANDOM()*6)::int) AS nights,
    ROUND((200 + RANDOM()*900)::numeric, 2) AS total_price,
    CASE WHEN RANDOM() < 0.75 THEN 'CONFIRMED' ELSE 'CANCELLED' END AS status,
    2
FROM generate_series(1,25);
INSERT INTO payments(booking_id, amount, method, status)
SELECT
    b.id,
    b.total_price,
    CASE
        WHEN RANDOM() < 0.6 THEN 'CARD'
        WHEN RANDOM() < 0.85 THEN 'TRANSFER'
        ELSE 'CASH'
        END AS method,
    CASE
        WHEN b.status = 'CONFIRMED' THEN 'PAID'
        ELSE 'REFUNDED'
        END AS status
FROM bookings b
WHERE NOT EXISTS (SELECT 1 FROM payments p WHERE p.booking_id = b.id);
INSERT INTO booking_history(booking_id, action, details)
SELECT b.id, 'CREATED', 'Seed data: booking created'
FROM bookings b;
INSERT INTO booking_history(booking_id, action, details)
SELECT b.id, 'CANCELLED', 'Seed data: booking cancelled'
FROM bookings b
WHERE b.status = 'CANCELLED';
INSERT INTO cancellations(booking_id, refund_amount, reason)
SELECT
    b.id,
    ROUND((b.total_price * 0.50)::numeric, 2),
    'Seed: cancelled'
FROM bookings b
WHERE b.status = 'CANCELLED'
    ON CONFLICT (booking_id) DO NOTHING;
SELECT COUNT(*) AS airlines FROM airlines;
SELECT COUNT(*) AS flights FROM flights;
SELECT COUNT(*) AS hotels FROM hotels;
SELECT COUNT(*) AS passengers FROM passengers;
SELECT COUNT(*) AS bookings FROM bookings;
SELECT COUNT(*) AS payments FROM payments;
SELECT COUNT(*) AS cancellations FROM cancellations;
SELECT COUNT(*) AS history FROM booking_history;
SELECT
    b.id,
    pa.full_name,
    f.flight_code,
    f.from_city,
    f.to_city,
    h.name AS hotel,
    h.city,
    b.nights,
    b.total_price,
    b.status
FROM bookings b
         JOIN passengers pa ON pa.id = b.passenger_id
         JOIN flights f ON f.id = b.flight_id
         JOIN hotels h ON h.id = b.hotel_id
ORDER BY b.id DESC
    LIMIT 20;