package controller.admin;

import model.bean.User;
import model.bean.BookingGuest;
import model.dao.BookingDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/checkin")
public class AdminCheckinServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Auth check - Admin only
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        // Check for success message
        String msg = request.getParameter("msg");
        String code = request.getParameter("code");
        
        if ("StatusUpdated".equals(msg) && code != null) {
            // Re-fetch the guest to show updated status
            BookingDAO bookingDAO = new BookingDAO();
            BookingGuest guest = bookingDAO.getGuestByCode(code);
            
            if (guest != null) {
                request.setAttribute("guest", guest);
                request.setAttribute("successMsg", "Status updated successfully!");
            }
        }

        request.getRequestDispatcher("/views/admin/checkin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Auth check - Admin only
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        String action = request.getParameter("action");
        BookingDAO bookingDAO = new BookingDAO();

        // ==========================================
        // ACTION: SEARCH
        // ==========================================
        if ("search".equals(action)) {
            String code = request.getParameter("code");
            
            if (code == null || code.trim().isEmpty()) {
                request.setAttribute("error", "Please enter a check-in code!");
                request.getRequestDispatcher("/views/admin/checkin.jsp").forward(request, response);
                return;
            }

            code = code.trim().toUpperCase(); // Normalize code
            BookingGuest guest = bookingDAO.getGuestByCode(code);

            if (guest != null) {
                request.setAttribute("guest", guest);
            } else {
                request.setAttribute("error", "Code not found! Please verify the check-in code.");
            }

            request.getRequestDispatcher("/views/admin/checkin.jsp").forward(request, response);
            return;
        }

        // ==========================================
        // ACTION: UPDATE STATUS
        // ==========================================
        if ("update".equals(action)) {
            String code = request.getParameter("code");
            String status = request.getParameter("status");

            if (code == null || status == null) {
                request.setAttribute("error", "Invalid request parameters!");
                request.getRequestDispatcher("/views/admin/checkin.jsp").forward(request, response);
                return;
            }

            boolean success = bookingDAO.updateGuestStatus(code, status);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/checkin?msg=StatusUpdated&code=" + code);
            } else {
                request.setAttribute("error", "Failed to update status. Please try again.");
                request.getRequestDispatcher("/views/admin/checkin.jsp").forward(request, response);
            }
            return;
        }

        // Default: Unknown action
        request.setAttribute("error", "Unknown action!");
        request.getRequestDispatcher("/views/admin/checkin.jsp").forward(request, response);
    }
}
