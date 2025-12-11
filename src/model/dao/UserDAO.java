package model.dao;

import model.bean.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

    // CRUD skeleton methods
    public ArrayList<User> getAllUsers() {
        // TODO: Implement logic to retrieve all users
        return null;
    }

    public User getUserById(int id) {
        // TODO: Implement logic to retrieve user by ID
        return null;
    }

    public void createUser(User user) {
        // TODO: Implement logic to insert a new user
    }

    public void updateUser(User user) {
        // TODO: Implement logic to update an existing user
    }

    public void deleteUser(int id) {
        // TODO: Implement logic to delete a user by ID
    }
}
