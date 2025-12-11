package controller.client;

import model.bean.User;
import model.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Authentication check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login?msg=Please login to view your profile");
            return;
        }

        // Forward to profile page
        request.getRequestDispatcher("/views/client/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Authentication check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login?msg=Please login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");

        try {
            if ("update_info".equals(action)) {
                // ==========================================
                // ACTION: UPDATE PROFILE INFO
                // ==========================================
                String fullName = request.getParameter("full_name");
                String email = request.getParameter("email");

                // Validate inputs
                if (fullName == null || fullName.trim().isEmpty() || 
                    email == null || email.trim().isEmpty()) {
                    request.setAttribute("error", "Full Name and Email are required!");
                    request.getRequestDispatcher("/views/client/profile.jsp").forward(request, response);
                    return;
                }

                // Update user object
                user.setFullName(fullName.trim());
                user.setEmail(email.trim());

                // Update in database
                boolean success = userDAO.updateUser(user);

                if (success) {
                    // Refresh user object in session
                    User updatedUser = userDAO.getUserById(user.getId());
                    if (updatedUser != null) {
                        updatedUser.setPassword(user.getPassword()); // Keep password in session
                        session.setAttribute("user", updatedUser);
                    }
                    request.setAttribute("successInfo", "Profile updated successfully!");
                } else {
                    request.setAttribute("error", "Failed to update profile. Please try again.");
                }

                request.getRequestDispatcher("/views/client/profile.jsp").forward(request, response);

            } else if ("change_password".equals(action)) {
                // ==========================================
                // ACTION: CHANGE PASSWORD
                // ==========================================
                String currentPassword = request.getParameter("current_password");
                String newPassword = request.getParameter("new_password");
                String confirmPassword = request.getParameter("confirm_password");

                // Validate inputs
                if (currentPassword == null || currentPassword.trim().isEmpty() ||
                    newPassword == null || newPassword.trim().isEmpty() ||
                    confirmPassword == null || confirmPassword.trim().isEmpty()) {
                    request.setAttribute("errorPassword", "All password fields are required!");
                    request.getRequestDispatcher("/views/client/profile.jsp").forward(request, response);
                    return;
                }

                // Check if new password matches confirmation
                if (!newPassword.equals(confirmPassword)) {
                    request.setAttribute("errorPassword", "New password and confirmation do not match!");
                    request.getRequestDispatcher("/views/client/profile.jsp").forward(request, response);
                    return;
                }

                // Check if new password is different from current
                if (currentPassword.equals(newPassword)) {
                    request.setAttribute("errorPassword", "New password must be different from current password!");
                    request.getRequestDispatcher("/views/client/profile.jsp").forward(request, response);
                    return;
                }

                // Verify current password
                boolean isCurrentPasswordValid = userDAO.checkPassword(user.getId(), currentPassword);
                if (!isCurrentPasswordValid) {
                    request.setAttribute("errorPassword", "Current password is incorrect!");
                    request.getRequestDispatcher("/views/client/profile.jsp").forward(request, response);
                    return;
                }

                // Change password
                boolean success = userDAO.changePassword(user.getId(), newPassword);

                if (success) {
                    // Update password in session
                    user.setPassword(newPassword);
                    session.setAttribute("user", user);
                    request.setAttribute("successPassword", "Password changed successfully!");
                } else {
                    request.setAttribute("errorPassword", "Failed to change password. Please try again.");
                }

                request.getRequestDispatcher("/views/client/profile.jsp").forward(request, response);

            } else {
                // Unknown action
                request.setAttribute("error", "Invalid action!");
                request.getRequestDispatcher("/views/client/profile.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/views/client/profile.jsp").forward(request, response);
        }
    }
}
