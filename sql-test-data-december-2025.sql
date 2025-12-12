-- ===================================
-- TEST DATA FOR REVENUE STATISTICS
-- November & December 2025 Sample Bookings
-- ===================================

-- Sample bookings for November 2025 with PAID status
INSERT INTO Bookings (user_id, total_amount, checkin_date, checkout_date, payment_status) VALUES 
(2, 1200000, '2025-11-01', '2025-11-03', 'paid'),
(3, 600000, '2025-11-05', '2025-11-06', 'paid'),
(2, 1800000, '2025-11-08', '2025-11-10', 'paid'),
(3, 900000, '2025-11-12', '2025-11-13', 'paid'),
(2, 2500000, '2025-11-15', '2025-11-18', 'paid'),
(3, 1100000, '2025-11-20', '2025-11-22', 'paid'),
(2, 2000000, '2025-11-25', '2025-11-27', 'paid');

-- Sample bookings for December 2025 with PAID status
INSERT INTO Bookings (user_id, total_amount, checkin_date, checkout_date, payment_status) VALUES 
(2, 1000000, '2025-12-01', '2025-12-02', 'paid'),
(2, 2400000, '2025-12-02', '2025-12-04', 'paid'),
(3, 500000, '2025-12-05', '2025-12-06', 'paid'),
(2, 3700000, '2025-12-08', '2025-12-11', 'paid'),
(3, 1200000, '2025-12-12', '2025-12-13', 'paid'),
(2, 2000000, '2025-12-15', '2025-12-17', 'paid'),
(3, 900000, '2025-12-18', '2025-12-19', 'paid'),
(2, 3400000, '2025-12-20', '2025-12-23', 'paid'),
(3, 1500000, '2025-12-25', '2025-12-27', 'paid'),
(2, 2500000, '2025-12-28', '2025-12-30', 'paid');

-- BookingRooms for test bookings
INSERT INTO BookingRooms (booking_id, room_id, price_per_night) VALUES 
(2, 1, 500000),     -- Dec 1-2
(3, 3, 1200000),    -- Dec 2-4
(4, 1, 500000),     -- Dec 5-6
(5, 3, 1200000), (5, 1, 500000),  -- Dec 8-11 (2 rooms)
(6, 3, 1200000),    -- Dec 12-13
(7, 1, 500000), (7, 3, 1200000),  -- Dec 15-17 (2 rooms)
(8, 1, 500000),     -- Dec 18-19
(9, 3, 1200000), (9, 1, 500000),  -- Dec 20-23 (2 rooms)
(10, 1, 500000), (10, 3, 1200000),-- Dec 25-27 (2 rooms)
(11, 3, 1200000), (11, 1, 500000);-- Dec 28-30 (2 rooms)

-- BookingGuests for test bookings
INSERT INTO BookingGuests (booking_id, full_name, checkin_code, checkin_status) VALUES 
(2, 'Nguyen Van A', 'CODE-DEC01-A', 'pending'),
(3, 'Tran Thi B', 'CODE-DEC02-A', 'pending'),
(3, 'Tran Thi C', 'CODE-DEC02-B', 'pending'),
(4, 'Nguyen Van D', 'CODE-DEC05-A', 'pending'),
(5, 'Tran Thi E', 'CODE-DEC08-A', 'pending'),
(5, 'Tran Thi F', 'CODE-DEC08-B', 'pending'),
(6, 'Nguyen Van G', 'CODE-DEC12-A', 'pending'),
(7, 'Tran Thi H', 'CODE-DEC15-A', 'pending'),
(7, 'Tran Thi I', 'CODE-DEC15-B', 'pending'),
(8, 'Nguyen Van J', 'CODE-DEC18-A', 'pending'),
(9, 'Tran Thi K', 'CODE-DEC20-A', 'pending'),
(9, 'Tran Thi L', 'CODE-DEC20-B', 'pending'),
(10, 'Nguyen Van M', 'CODE-DEC25-A', 'pending'),
(10, 'Nguyen Van N', 'CODE-DEC25-B', 'pending'),
(11, 'Tran Thi O', 'CODE-DEC28-A', 'pending'),
(11, 'Tran Thi P', 'CODE-DEC28-B', 'pending');
