package model.dao;

import model.bean.User;
import model.bean.CustomerStats;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends BaseDAO {

    // Fully implemented checkLogin method
    public User checkLogin(String username, String password) {
        User user = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT user_id, username, password, full_name, email, role FROM Users WHERE username = ? AND password = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
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
        return user;
    }

    /**
     * Update user profile information (full_name, email).
     * Does not update password or username.
     */
    public boolean updateUser(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            String sql = "UPDATE Users SET full_name = ?, email = ? WHERE user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setInt(3, user.getId());
            
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
     * Check if the current password matches for a user.
     */
    public boolean checkPassword(int userId, String currentPassword) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean isValid = false;

        try {
            conn = getConnection();
            String sql = "SELECT password FROM Users WHERE user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");
                isValid = dbPassword.equals(currentPassword);
                System.out.print("DB password: " + dbPassword);
                System.out.print("Current password: " + currentPassword);
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
        return isValid;
    }

    /**
     * Change user password.
     */
    public boolean changePassword(int userId, String newPassword) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            String sql = "UPDATE Users SET password = ? WHERE user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            
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
     * Get user by ID (for refreshing session data).
     */
    public User getUserById(int id) {
        User user = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT user_id, username, password, full_name, email, role FROM Users WHERE user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
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
        return user;
    }

    /**
     * Create a new user account (for registration).
     * Default role is set to "customer".
     */
    public boolean createUser(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            String sql = "INSERT INTO Users (username, password, full_name, email, role) VALUES (?, ?, ?, ?, 'customer')";
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            
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
     * Get customer statistics for admin: number of bookings and rooms used per customer.
     * Counts distinct bookings and total rooms booked (BookingRooms) for each customer.
     */
    public List<CustomerStats> getCustomerRoomUsageStats() {
        List<CustomerStats> stats = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT u.user_id, u.username, u.full_name, u.email, " +
                         "       COUNT(DISTINCT b.booking_id) AS booking_count, " +
                         "       COUNT(br.room_id) AS room_count " +
                         "FROM Users u " +
                         "LEFT JOIN Bookings b ON u.user_id = b.user_id " +
                         "LEFT JOIN BookingRooms br ON b.booking_id = br.booking_id " +
                         "WHERE u.role = 'customer' " +
                         "GROUP BY u.user_id, u.username, u.full_name, u.email " +
                         "ORDER BY room_count DESC, booking_count DESC, u.full_name ASC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                CustomerStats stat = new CustomerStats();
                stat.setUserId(rs.getInt("user_id"));
                stat.setUsername(rs.getString("username"));
                stat.setFullName(rs.getString("full_name"));
                stat.setEmail(rs.getString("email"));
                stat.setBookingCount(rs.getInt("booking_count"));
                stat.setRoomCount(rs.getInt("room_count"));
                stats.add(stat);
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

        return stats;
    }
}
