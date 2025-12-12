package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import model.bean.Booking;
import model.bean.BookingGuest;
import model.bean.BookingService;
import model.bean.RevenueStats;

public class BookingDAO extends BaseDAO {

    /**
     * Creates a booking with full transaction management and concurrency control.
     * Fetches BOTH room and service prices from DB (security), re-verifies availability,
     * and inserts booking with CORRECT total calculation: (Room Price * Nights) + Services.
     * 
     * @return booking_id on success, 0 on failure
     * @throws SQLException if room is already booked or transaction fails
     */
    public int createBooking(int userId, Date checkIn, Date checkOut, 
                            List<Integer> roomIds, List<Integer> serviceIds, 
                            List<String> guestNames) throws SQLException {
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int bookingId = 0;

        try {
            // Get connection and setup transaction
            conn = getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            // =============================================
            // STEP 0: Calculate Number of Nights
            // =============================================
            long diffInMillies = Math.abs(checkOut.getTime() - checkIn.getTime());
            long nights = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if (nights < 1) nights = 1; // Minimum 1 night

            System.out.println("DEBUG: Check-in: " + checkIn + ", Check-out: " + checkOut + ", Nights: " + nights);

            // =============================================
            // STEP 1: Fetch Room Prices from DB (Security)
            // =============================================
            Map<Integer, Double> roomPrices = new HashMap<>();
            double totalRoomPerNight = 0.0;
            
            if (roomIds != null && !roomIds.isEmpty()) {
                StringBuilder sql = new StringBuilder("SELECT room_id, price FROM Rooms WHERE room_id IN (");
                for (int i = 0; i < roomIds.size(); i++) {
                    sql.append(i == 0 ? "?" : ", ?");
                }
                sql.append(")");
                
                ps = conn.prepareStatement(sql.toString());
                for (int i = 0; i < roomIds.size(); i++) {
                    ps.setInt(i + 1, roomIds.get(i));
                }
                
                rs = ps.executeQuery();
                while (rs.next()) {
                    int rId = rs.getInt("room_id");
                    double price = rs.getDouble("price");
                    roomPrices.put(rId, price);
                    totalRoomPerNight += price;
                }
                rs.close();
                ps.close();

                // Validate all rooms exist
                if (roomPrices.size() != roomIds.size()) {
                    conn.rollback();
                    throw new SQLException("Some rooms are invalid or not found");
                }
            }

            // =============================================
            // STEP 2: Fetch Service Prices from DB (Security)
            // =============================================
            Map<Integer, Double> servicePrices = new HashMap<>();
            double totalServiceCost = 0.0;
            
            if (serviceIds != null && !serviceIds.isEmpty()) {
                StringBuilder sql = new StringBuilder("SELECT service_id, price FROM Services WHERE service_id IN (");
                for (int i = 0; i < serviceIds.size(); i++) {
                    sql.append(i == 0 ? "?" : ", ?");
                }
                sql.append(")");
                
                ps = conn.prepareStatement(sql.toString());
                for (int i = 0; i < serviceIds.size(); i++) {
                    ps.setInt(i + 1, serviceIds.get(i));
                }
                
                rs = ps.executeQuery();
                while (rs.next()) {
                    int sId = rs.getInt("service_id");
                    double price = rs.getDouble("price");
                    servicePrices.put(sId, price);
                    totalServiceCost += price;
                }
                rs.close();
                ps.close();
            }

            // =============================================
            // STEP 3: Calculate Grand Total (CORRECTED)
            // Formula: (RoomPrice_PerNight * Nights) + Services
            // =============================================
            double grandTotal = (totalRoomPerNight * nights) + totalServiceCost;
            
            System.out.println("DEBUG: RoomTotal/Night: " + totalRoomPerNight + " * " + nights + " nights + ServiceTotal: " + totalServiceCost + " = Grand Total: " + grandTotal);

            // =============================================
            // STEP 4: Re-Verify Room Availability (Crucial)
            // =============================================
            if (roomIds != null && !roomIds.isEmpty()) {
                String availabilitySQL = "SELECT COUNT(*) as conflict_count " +
                                       "FROM BookingRooms br " +
                                       "JOIN Bookings b ON br.booking_id = b.booking_id " +
                                       "WHERE br.room_id IN (" + buildPlaceholders(roomIds.size()) + ") " +
                                       "AND b.checkin_date <= ? " +
                                       "AND b.checkout_date >= ?";

                ps = conn.prepareStatement(availabilitySQL);
                int paramIndex = 1;
                for (Integer roomId : roomIds) {
                    ps.setInt(paramIndex++, roomId);
                }
                ps.setDate(paramIndex++, checkOut);  // b.checkin_date < checkOut
                ps.setDate(paramIndex, checkIn);     // b.checkout_date > checkIn

                rs = ps.executeQuery();
                if (rs.next() && rs.getInt("conflict_count") > 0) {
                    conn.rollback();
                    throw new SQLException("One or more rooms are already booked for the selected dates");
                }
                rs.close();
                ps.close();
            }

            // =============================================
            // STEP 5: Insert Master Booking Record
            // =============================================
            String insertBooking = "INSERT INTO Bookings (user_id, total_amount, checkin_date, checkout_date, payment_status) " +
                                 "VALUES (?, ?, ?, ?, 'paid')";
            ps = conn.prepareStatement(insertBooking, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setDouble(2, grandTotal);  // Use correct grand total
            ps.setDate(3, checkIn);
            ps.setDate(4, checkOut);
            ps.executeUpdate();
            
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                bookingId = rs.getInt(1);
            } else {
                conn.rollback();
                throw new SQLException("Failed to create booking ID");
            }
            rs.close();
            ps.close();

            // =============================================
            // STEP 6: Insert BookingRooms WITH price_per_night
            // =============================================
            if (roomIds != null && !roomIds.isEmpty()) {
                String insertRoom = "INSERT INTO BookingRooms (booking_id, room_id, price_per_night) VALUES (?, ?, ?)";
                ps = conn.prepareStatement(insertRoom);
                for (Integer rId : roomIds) {
                    if (roomPrices.containsKey(rId)) {
                        ps.setInt(1, bookingId);
                        ps.setInt(2, rId);
                        ps.setDouble(3, roomPrices.get(rId));  // Store base price per night
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
                ps.close();
            }

            // =============================================
            // STEP 7: Insert BookingServices WITH price
            // =============================================
            if (serviceIds != null && !serviceIds.isEmpty()) {
                String insertService = "INSERT INTO BookingServices (booking_id, service_id, price, quantity) VALUES (?, ?, ?, 1)";
                ps = conn.prepareStatement(insertService);
                for (Integer sId : serviceIds) {
                    if (servicePrices.containsKey(sId)) {
                        ps.setInt(1, bookingId);
                        ps.setInt(2, sId);
                        ps.setDouble(3, servicePrices.get(sId));
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
                ps.close();
            }

            // =============================================
            // STEP 8: Insert BookingGuests with UUID Codes
            // =============================================
            if (guestNames != null && !guestNames.isEmpty()) {
                String insertGuest = "INSERT INTO BookingGuests (booking_id, full_name, checkin_code, checkin_status) VALUES (?, ?, ?, 'pending')";
                ps = conn.prepareStatement(insertGuest);
                for (String name : guestNames) {
                    String checkinCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    ps.setInt(1, bookingId);
                    ps.setString(2, name);
                    ps.setString(3, checkinCode);
                    ps.addBatch();
                }
                ps.executeBatch();
                ps.close();
            }

            // =============================================
            // STEP 9: Commit Transaction
            // =============================================
            conn.commit();
            return bookingId;

        } catch (SQLException e) {
            // Rollback on any error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            throw e;

        } finally {
            // Clean up resources
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get all bookings for a specific user with hotel name (summary list only, no guests).
     * 
     * @param userId The user ID to fetch bookings for
     * @return List of Booking objects with hotelName populated
     */
    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            String bookingSql = "SELECT b.booking_id, b.user_id, b.checkin_date, b.checkout_date, " +
                              "b.total_amount, b.payment_status, b.created_at, h.name as hotel_name " +
                              "FROM Bookings b " +
                              "JOIN BookingRooms br ON b.booking_id = br.booking_id " +
                              "JOIN Rooms r ON br.room_id = r.room_id " +
                              "JOIN Hotels h ON r.hotel_id = h.hotel_id " +
                              "WHERE b.user_id = ? " +
                              "GROUP BY b.booking_id " +
                              "ORDER BY b.created_at DESC";

            ps = conn.prepareStatement(bookingSql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setCheckinDate(rs.getDate("checkin_date"));
                booking.setCheckoutDate(rs.getDate("checkout_date"));
                booking.setTotalAmount(rs.getDouble("total_amount"));
                booking.setPaymentStatus(rs.getString("payment_status"));
                booking.setCreatedAt(rs.getTimestamp("created_at"));
                booking.setHotelName(rs.getString("hotel_name"));
                bookings.add(booking);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return bookings;
    }

    /**
     * Get a single booking by ID with hotel name.
     * 
     * @param bookingId The booking ID
     * @return Booking object or null if not found
     */
    public Booking getBookingById(int bookingId) {
        Booking booking = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            String bookingSql = "SELECT b.booking_id, b.user_id, b.checkin_date, b.checkout_date, " +
                              "b.total_amount, b.payment_status, b.created_at, h.name as hotel_name " +
                              "FROM Bookings b " +
                              "JOIN BookingRooms br ON b.booking_id = br.booking_id " +
                              "JOIN Rooms r ON br.room_id = r.room_id " +
                              "JOIN Hotels h ON r.hotel_id = h.hotel_id " +
                              "WHERE b.booking_id = ? " +
                              "GROUP BY b.booking_id";

            ps = conn.prepareStatement(bookingSql);
            ps.setInt(1, bookingId);
            rs = ps.executeQuery();

            if (rs.next()) {
                booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setCheckinDate(rs.getDate("checkin_date"));
                booking.setCheckoutDate(rs.getDate("checkout_date"));
                booking.setTotalAmount(rs.getDouble("total_amount"));
                booking.setPaymentStatus(rs.getString("payment_status"));
                booking.setCreatedAt(rs.getTimestamp("created_at"));
                booking.setHotelName(rs.getString("hotel_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return booking;
    }

    /**
     * Get all guests for a specific booking.
     * 
     * @param bookingId The booking ID
     * @return List of BookingGuest objects
     */
    public List<BookingGuest> getGuestsByBookingId(int bookingId) {
        List<BookingGuest> guests = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            String guestSql = "SELECT guest_id, booking_id, full_name, checkin_code, checkin_status " +
                            "FROM BookingGuests WHERE booking_id = ?";

            ps = conn.prepareStatement(guestSql);
            ps.setInt(1, bookingId);
            rs = ps.executeQuery();

            while (rs.next()) {
                BookingGuest guest = new BookingGuest();
                guest.setGuestId(rs.getInt("guest_id"));
                guest.setBookingId(rs.getInt("booking_id"));
                guest.setFullName(rs.getString("full_name"));
                guest.setCheckinCode(rs.getString("checkin_code"));
                guest.setCheckinStatus(rs.getString("checkin_status"));
                guests.add(guest);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return guests;
    }

    /**
     * Get guest information by check-in code (for admin check-in module).
     * 
     * @param code The check-in code
     * @return BookingGuest with hotel name and dates, or null if not found
     */
    public BookingGuest getGuestByCode(String code) {
        BookingGuest guest = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            String sql = "SELECT bg.guest_id, bg.booking_id, bg.full_name, bg.checkin_code, bg.checkin_status, " +
                        "       b.checkin_date, b.checkout_date, " +
                        "       h.name as hotel_name " +
                        "FROM BookingGuests bg " +
                        "JOIN Bookings b ON bg.booking_id = b.booking_id " +
                        "JOIN BookingRooms br ON b.booking_id = br.booking_id " +
                        "JOIN Rooms r ON br.room_id = r.room_id " +
                        "JOIN Hotels h ON r.hotel_id = h.hotel_id " +
                        "WHERE bg.checkin_code = ? " +
                        "LIMIT 1";

            ps = conn.prepareStatement(sql);
            ps.setString(1, code);
            rs = ps.executeQuery();

            if (rs.next()) {
                guest = new BookingGuest();
                guest.setGuestId(rs.getInt("guest_id"));
                guest.setBookingId(rs.getInt("booking_id"));
                guest.setFullName(rs.getString("full_name"));
                guest.setCheckinCode(rs.getString("checkin_code"));
                guest.setCheckinStatus(rs.getString("checkin_status"));
                
                // Set transient fields
                guest.setHotelName(rs.getString("hotel_name"));
                guest.setCheckInDate(rs.getDate("checkin_date"));
                guest.setCheckOutDate(rs.getDate("checkout_date"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return guest;
    }

    /**
     * Get all services for a specific booking with service names.
     * 
     * @param bookingId The booking ID
     * @return List of BookingService objects with serviceName populated
     */
    public List<BookingService> getServicesByBookingId(int bookingId) {
        List<BookingService> services = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            String sql = "SELECT bs.booking_id, bs.service_id, bs.price, bs.quantity, s.name as service_name " +
                       "FROM BookingServices bs " +
                       "JOIN Services s ON bs.service_id = s.service_id " +
                       "WHERE bs.booking_id = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, bookingId);
            rs = ps.executeQuery();

            while (rs.next()) {
                BookingService service = new BookingService();
                service.setBookingId(rs.getInt("booking_id"));
                service.setServiceId(rs.getInt("service_id"));
                service.setPrice(rs.getDouble("price"));
                service.setQuantity(rs.getInt("quantity"));
                service.setServiceName(rs.getString("service_name"));
                services.add(service);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return services;
    }

    /**
     * Update guest check-in status by check-in code.
     * 
     * @param code The check-in code
     * @param status The new status (e.g., "checked_in", "checked_out")
     * @return true if update was successful, false otherwise
     */
    public boolean updateGuestStatus(String code, String status) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();

            String sql = "UPDATE BookingGuests SET checkin_status = ? WHERE checkin_code = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, code);

            int rowsAffected = ps.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    /**
     * Helper method to build SQL placeholders for IN clause
     * Example: buildPlaceholders(3) returns "?, ?, ?"
     */
    private String buildPlaceholders(int count) {
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("?");
            if (i < count - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * Get revenue statistics by day
     * Groups bookings by check-in date and sums paid bookings
     */
    public List<RevenueStats> getRevenueByDay(int year, int month) throws SQLException {
        List<RevenueStats> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT DATE_FORMAT(checkin_date, '%Y-%m-%d') as period, " +
                        "SUM(total_amount) as total_revenue, COUNT(*) as booking_count " +
                        "FROM Bookings " +
                        "WHERE YEAR(checkin_date) = ? AND MONTH(checkin_date) = ? " +
                        "AND payment_status = 'paid' " +
                        "GROUP BY DATE(checkin_date) " +
                        "ORDER BY checkin_date ASC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, year);
            ps.setInt(2, month);

            rs = ps.executeQuery();
            while (rs.next()) {
                String period = rs.getString("period");
                double totalRevenue = rs.getDouble("total_revenue");
                int bookingCount = rs.getInt("booking_count");
                results.add(new RevenueStats(period, totalRevenue, bookingCount));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    /**
     * Get revenue statistics by month
     * Groups bookings by month and sums paid bookings
     */
    public List<RevenueStats> getRevenueByMonth(int year) throws SQLException {
        List<RevenueStats> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT DATE_FORMAT(checkin_date, '%Y-%m') as period, " +
                        "SUM(total_amount) as total_revenue, COUNT(*) as booking_count " +
                        "FROM Bookings " +
                        "WHERE YEAR(checkin_date) = ? " +
                        "AND payment_status = 'paid' " +
                        "GROUP BY YEAR(checkin_date), MONTH(checkin_date) " +
                        "ORDER BY checkin_date ASC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, year);

            rs = ps.executeQuery();
            while (rs.next()) {
                String period = rs.getString("period");
                double totalRevenue = rs.getDouble("total_revenue");
                int bookingCount = rs.getInt("booking_count");
                results.add(new RevenueStats(period, totalRevenue, bookingCount));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    /**
     * Get revenue statistics by year
     * Groups bookings by year and sums paid bookings
     */
    public List<RevenueStats> getRevenueByYear() throws SQLException {
        List<RevenueStats> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT YEAR(checkin_date) as period, " +
                        "SUM(total_amount) as total_revenue, COUNT(*) as booking_count " +
                        "FROM Bookings " +
                        "WHERE payment_status = 'paid' " +
                        "GROUP BY YEAR(checkin_date) " +
                        "ORDER BY checkin_date ASC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String period = rs.getString("period");
                double totalRevenue = rs.getDouble("total_revenue");
                int bookingCount = rs.getInt("booking_count");
                results.add(new RevenueStats(period, totalRevenue, bookingCount));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    /**
     * Get total revenue for a specific date range
     */
    public double getTotalRevenue(Date startDate, Date endDate) throws SQLException {
        double total = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT SUM(total_amount) as total FROM Bookings " +
                        "WHERE checkin_date >= ? AND checkin_date <= ? " +
                        "AND payment_status = 'paid'";

            ps = conn.prepareStatement(sql);
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);

            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return total;
    }

    /**
     * Get revenue statistics for a specific month and year
     * Used for monthly view when a specific month is selected
     */
    public RevenueStats getRevenueByMonthAndYear(int year, int month) throws SQLException {
        RevenueStats result = new RevenueStats();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT DATE_FORMAT(checkin_date, '%Y-%m') as period, " +
                        "SUM(total_amount) as total_revenue, COUNT(*) as booking_count " +
                        "FROM Bookings " +
                        "WHERE YEAR(checkin_date) = ? AND MONTH(checkin_date) = ? " +
                        "AND payment_status = 'paid' " +
                        "GROUP BY YEAR(checkin_date), MONTH(checkin_date)";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, year);
            ps.setInt(2, month);

            rs = ps.executeQuery();
            if (rs.next()) {
                String period = rs.getString("period");
                double totalRevenue = rs.getDouble("total_revenue");
                int bookingCount = rs.getInt("booking_count");
                result = new RevenueStats(period, totalRevenue, bookingCount);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public List<Booking> getBookingsByDate(String dateStr) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String bookingSql = "SELECT b.booking_id, b.user_id, b.checkin_date, b.checkout_date, " +
                    "b.total_amount, b.payment_status, b.created_at, u.username, u.email " +
                    "FROM Bookings b " +
                    "JOIN Users u ON b.user_id = u.user_id " +
                    "WHERE DATE(b.checkin_date) >= ? " +
                    "AND b.payment_status = 'paid' " +
                    "ORDER BY b.checkin_date ASC";

            ps = conn.prepareStatement(bookingSql);
            ps.setString(1, dateStr);

            rs = ps.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setCheckinDate(rs.getDate("checkin_date"));
                booking.setCheckoutDate(rs.getDate("checkout_date"));
                booking.setTotalAmount(rs.getDouble("total_amount"));
                booking.setPaymentStatus(rs.getString("payment_status"));
                booking.setCreatedAt(rs.getTimestamp("created_at"));
                bookings.add(booking);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return bookings;
    }
}
