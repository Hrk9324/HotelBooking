package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BookingDAO extends BaseDAO {

    /**
     * Creates a booking with full transaction management and concurrency control.
     * Fetches room prices from DB first (security), re-verifies availability, 
     * and inserts booking with price_per_night in BookingRooms.
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
            // STEP A: Fetch Room Prices from DB (Security)
            // =============================================
            Map<Integer, Double> roomPriceMap = new HashMap<>();
            double calculatedTotal = 0.0;

            if (roomIds != null && !roomIds.isEmpty()) {
                String roomPriceSQL = "SELECT room_id, price FROM Rooms WHERE room_id IN (" 
                                    + buildPlaceholders(roomIds.size()) + ")";
                ps = conn.prepareStatement(roomPriceSQL);
                int paramIndex = 1;
                for (Integer roomId : roomIds) {
                    ps.setInt(paramIndex++, roomId);
                }
                rs = ps.executeQuery();
                
                while (rs.next()) {
                    int rId = rs.getInt("room_id");
                    double rPrice = rs.getDouble("price");
                    roomPriceMap.put(rId, rPrice);
                    calculatedTotal += rPrice;
                }
                rs.close();
                ps.close();

                // Validate all rooms exist
                if (roomPriceMap.size() != roomIds.size()) {
                    conn.rollback();
                    throw new SQLException("Some rooms are invalid or not found");
                }
            }

            // =============================================
            // STEP B: Re-Verify Room Availability (Crucial)
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
            // STEP C: Fetch Service Prices and Add to Total
            // =============================================
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
                    calculatedTotal += rs.getDouble("total_service_price");
                }
                rs.close();
                ps.close();
            }

            // =============================================
            // STEP D: Insert Master Booking Record
            // =============================================
            String bookingSQL = "INSERT INTO Bookings (user_id, checkin_date, checkout_date, total_amount, payment_status) " +
                              "VALUES (?, ?, ?, ?, 'paid')";
            ps = conn.prepareStatement(bookingSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setDate(2, checkIn);
            ps.setDate(3, checkOut);
            ps.setDouble(4, calculatedTotal);
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
            // STEP E: Insert BookingRooms WITH PRICE
            // =============================================
            if (roomIds != null && !roomIds.isEmpty()) {
                String roomSQL = "INSERT INTO BookingRooms (booking_id, room_id, price_per_night) VALUES (?, ?, ?)";
                ps = conn.prepareStatement(roomSQL);
                for (Integer roomId : roomIds) {
                    ps.setInt(1, bookingId);
                    ps.setInt(2, roomId);
                    ps.setDouble(3, roomPriceMap.get(roomId)); // Get price from map
                    ps.addBatch();
                }
                ps.executeBatch();
                ps.close();
            }

            // =============================================
            // STEP F: Insert BookingServices
            // =============================================
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

            // =============================================
            // STEP G: Insert BookingGuests with Unique Check-in Codes
            // =============================================
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
            // STEP H: Commit Transaction
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
