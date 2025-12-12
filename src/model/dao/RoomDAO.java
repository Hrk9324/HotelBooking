package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.bean.Room;

public class RoomDAO extends BaseDAO {

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.room_id, r.hotel_id, r.room_name, r.price, r.capacity, r.status, r.description, h.name AS hotel_name " +
                     "FROM Rooms r " +
                     "INNER JOIN Hotels h ON r.hotel_id = h.hotel_id";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("room_id"));
                room.setHotelId(rs.getInt("hotel_id"));
                room.setRoomName(rs.getString("room_name"));
                room.setPrice(rs.getDouble("price"));
                room.setCapacity(rs.getInt("capacity"));
                room.setStatus(rs.getString("status"));
                room.setDescription(rs.getString("description"));
                room.setHotelName(rs.getString("hotel_name"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public Room getRoomById(int id) {
        Room room = null;
        String sql = "SELECT r.room_id, r.hotel_id, r.room_name, r.price, r.capacity, r.status, r.description, h.name AS hotel_name " +
                     "FROM Rooms r " +
                     "INNER JOIN Hotels h ON r.hotel_id = h.hotel_id " +
                     "WHERE r.room_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    room = new Room();
                    room.setId(rs.getInt("room_id"));
                    room.setHotelId(rs.getInt("hotel_id"));
                    room.setRoomName(rs.getString("room_name"));
                    room.setPrice(rs.getDouble("price"));
                    room.setCapacity(rs.getInt("capacity"));
                    room.setStatus(rs.getString("status"));
                    room.setDescription(rs.getString("description"));
                    room.setHotelName(rs.getString("hotel_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return room;
    }

    public void addRoom(Room r) {
        String sql = "INSERT INTO Rooms (hotel_id, room_name, price, capacity, status, description) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, r.getHotelId());
            ps.setString(2, r.getRoomName());
            ps.setDouble(3, r.getPrice());
            ps.setInt(4, r.getCapacity());
            ps.setString(5, r.getStatus());
            ps.setString(6, r.getDescription());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRoom(Room r) {
        String sql = "UPDATE Rooms SET hotel_id = ?, room_name = ?, price = ?, capacity = ?, status = ?, description = ? WHERE room_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, r.getHotelId());
            ps.setString(2, r.getRoomName());
            ps.setDouble(3, r.getPrice());
            ps.setInt(4, r.getCapacity());
            ps.setString(5, r.getStatus());
            ps.setString(6, r.getDescription());
            ps.setInt(7, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRoom(int id) {
        String sql = "DELETE FROM Rooms WHERE room_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Room> searchAvailableRooms(java.sql.Date checkIn, java.sql.Date checkOut) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.room_id, r.hotel_id, r.room_name, r.price, r.capacity, r.status, r.description, h.name AS hotel_name " +
                     "FROM Rooms r " +
                     "JOIN Hotels h ON r.hotel_id = h.hotel_id " +
                     "WHERE r.status = 'available' " +
                     "AND r.room_id NOT IN (" +
                     "    SELECT br.room_id " +
                     "    FROM BookingRooms br " +
                     "    JOIN Bookings b ON br.booking_id = b.booking_id " +
                     "    WHERE b.checkin_date <= ? AND b.checkout_date >= ?" +
                     ")";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, checkOut);  // b.checkin_date <= checkOut
            ps.setDate(2, checkIn);   // b.checkout_date >= checkIn

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("room_id"));
                    room.setHotelId(rs.getInt("hotel_id"));
                    room.setRoomName(rs.getString("room_name"));
                    room.setPrice(rs.getDouble("price"));
                    room.setCapacity(rs.getInt("capacity"));
                    room.setStatus(rs.getString("status"));
                    room.setDescription(rs.getString("description"));
                    room.setHotelName(rs.getString("hotel_name"));
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public List<Room> getAvailableRoomsByHotel(int hotelId, java.sql.Date checkIn, java.sql.Date checkOut) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.room_id, r.hotel_id, r.room_name, r.price, r.capacity, r.status, r.description, h.name AS hotel_name " +
                     "FROM Rooms r " +
                     "JOIN Hotels h ON r.hotel_id = h.hotel_id " +
                     "WHERE r.status = 'available' " +
                     "AND r.hotel_id = ? " +
                     "AND r.room_id NOT IN (" +
                     "    SELECT br.room_id " +
                     "    FROM BookingRooms br " +
                     "    JOIN Bookings b ON br.booking_id = b.booking_id " +
                     "    WHERE b.checkin_date <= ? AND b.checkout_date >= ?" +
                     ")";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, hotelId);
            ps.setDate(2, checkOut);  // b.checkin_date <= checkOut
            ps.setDate(3, checkIn);   // b.checkout_date >= checkIn

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("room_id"));
                    room.setHotelId(rs.getInt("hotel_id"));
                    room.setRoomName(rs.getString("room_name"));
                    room.setPrice(rs.getDouble("price"));
                    room.setCapacity(rs.getInt("capacity"));
                    room.setStatus(rs.getString("status"));
                    room.setDescription(rs.getString("description"));
                    room.setHotelName(rs.getString("hotel_name"));
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }
}
