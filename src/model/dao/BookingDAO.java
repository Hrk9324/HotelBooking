package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BookingDAO extends BaseDAO {

    /**
     * Creates a booking with full transaction management and concurrency control.
     * Fetches BOTH room and service prices from DB (security), re-verifies availability,
     * and inserts booking with proper price columns in BookingRooms and BookingServices.
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

            double totalAmount = 0.0;

            // =============================================
            // STEP 1: Fetch Room Prices from DB (Security)
            // =============================================
            Map<Integer, Double> roomPrices = new HashMap<>();
            
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
                    totalAmount += price;
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
                    totalAmount += price;
                }
                rs.close();
                ps.close();
            }

            // =============================================
            // STEP 3: Re-Verify Room Availability (Crucial)
            // =============================================
            if (roomIds != null && !roomIds.isEmpty()) {
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
            }

            // =============================================
            // STEP 4: Insert Master Booking Record
            // =============================================
            String insertBooking = "INSERT INTO Bookings (user_id, total_amount, checkin_date, checkout_date, payment_status) " +
                                 "VALUES (?, ?, ?, ?, 'paid')";
            ps = conn.prepareStatement(insertBooking, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setDouble(2, totalAmount);
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
            // STEP 5: Insert BookingRooms WITH price_per_night
            // =============================================
            if (roomIds != null && !roomIds.isEmpty()) {
                String insertRoom = "INSERT INTO BookingRooms (booking_id, room_id, price_per_night) VALUES (?, ?, ?)";
                ps = conn.prepareStatement(insertRoom);
                for (Integer rId : roomIds) {
                    if (roomPrices.containsKey(rId)) {
                        ps.setInt(1, bookingId);
                        ps.setInt(2, rId);
                        ps.setDouble(3, roomPrices.get(rId));
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
                ps.close();
            }

            // =============================================
            // STEP 6: Insert BookingServices WITH price
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
            // STEP 7: Insert BookingGuests with UUID Codes
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
            // STEP 8: Commit Transaction
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
