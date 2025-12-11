package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

public class BookingDAO extends BaseDAO {

    /**
     * Creates a booking with full transaction management and concurrency control.
     * Re-verifies room availability and recalculates prices from DB for security.
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
            // STEP A: Re-Verify Room Availability (Crucial)
            // =============================================
            String availabilitySQL = "SELECT COUNT(*) as conflict_count " +
                                   "FROM BookingRooms br " +
                                   "JOIN Bookings b ON br.booking_id = b.booking_id " +
                                   "WHERE br.room_id IN (" + buildPlaceholders(roomIds.size()) + ") " +
                                   "AND b.checkin_date < ? " +
                                   "AND b.checkout_date > ?";

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

            // =============================================
            // STEP B: Calculate Total Price from DB (Security)
            // =============================================
            double totalPrice = 0.0;

            // Calculate room prices
            if (roomIds != null && !roomIds.isEmpty()) {
                String roomPriceSQL = "SELECT SUM(price) as total_room_price FROM Rooms WHERE room_id IN (" 
                                    + buildPlaceholders(roomIds.size()) + ")";
                ps = conn.prepareStatement(roomPriceSQL);
                paramIndex = 1;
                for (Integer roomId : roomIds) {
                    ps.setInt(paramIndex++, roomId);
                }
                rs = ps.executeQuery();
                if (rs.next()) {
                    totalPrice += rs.getDouble("total_room_price");
                }
                rs.close();
                ps.close();
            }

            // Calculate service prices
            if (serviceIds != null && !serviceIds.isEmpty()) {
                String servicePriceSQL = "SELECT SUM(price) as total_service_price FROM Services WHERE service_id IN (" 
                                       + buildPlaceholders(serviceIds.size()) + ")";
                ps = conn.prepareStatement(servicePriceSQL);
                paramIndex = 1;
                for (Integer serviceId : serviceIds) {
                    ps.setInt(paramIndex++, serviceId);
                }
                rs = ps.executeQuery();
                if (rs.next()) {
                    totalPrice += rs.getDouble("total_service_price");
                }
                rs.close();
                ps.close();
            }

            // =============================================
            // STEP C: Insert Booking Record
            // =============================================
            String bookingSQL = "INSERT INTO Bookings (user_id, checkin_date, checkout_date, total_price, payment_status) " +
                              "VALUES (?, ?, ?, ?, 'paid')";
            ps = conn.prepareStatement(bookingSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setDate(2, checkIn);
            ps.setDate(3, checkOut);
            ps.setDouble(4, totalPrice);
            ps.executeUpdate();

            // Get generated booking_id
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                bookingId = rs.getInt(1);
            }
            rs.close();
            ps.close();

            if (bookingId == 0) {
                conn.rollback();
                throw new SQLException("Failed to retrieve booking ID");
            }

            // =============================================
            // STEP D: Insert Booking Details (Batching)
            // =============================================

            // D1: Insert BookingRooms
            if (roomIds != null && !roomIds.isEmpty()) {
                String roomSQL = "INSERT INTO BookingRooms (booking_id, room_id) VALUES (?, ?)";
                ps = conn.prepareStatement(roomSQL);
                for (Integer roomId : roomIds) {
                    ps.setInt(1, bookingId);
                    ps.setInt(2, roomId);
                    ps.addBatch();
                }
                ps.executeBatch();
                ps.close();
            }

            // D2: Insert BookingServices
            if (serviceIds != null && !serviceIds.isEmpty()) {
                String serviceSQL = "INSERT INTO BookingServices (booking_id, service_id) VALUES (?, ?)";
                ps = conn.prepareStatement(serviceSQL);
                for (Integer serviceId : serviceIds) {
                    ps.setInt(1, bookingId);
                    ps.setInt(2, serviceId);
                    ps.addBatch();
                }
                ps.executeBatch();
                ps.close();
            }

            // D3: Insert BookingGuests with unique checkin codes
            if (guestNames != null && !guestNames.isEmpty()) {
                String guestSQL = "INSERT INTO BookingGuests (booking_id, guest_name, checkin_code) VALUES (?, ?, ?)";
                ps = conn.prepareStatement(guestSQL);
                for (String guestName : guestNames) {
                    String checkinCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    ps.setInt(1, bookingId);
                    ps.setString(2, guestName);
                    ps.setString(3, checkinCode);
                    ps.addBatch();
                }
                ps.executeBatch();
                ps.close();
            }

            // =============================================
            // STEP E: Commit Transaction
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
            throw e;  // Re-throw to let servlet handle

        } finally {
            // Clean up resources
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.setAutoCommit(true);  // Reset auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
}
