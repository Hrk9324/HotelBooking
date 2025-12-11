package controller.client;

import model.bean.Booking;
import model.bean.User;
import model.dao.BookingDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {
    private BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Authentication check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login?msg=Please login to view booking history");
            return;
        }

        // Get user from session
        User user = (User) session.getAttribute("user");

        // Fetch user's booking history (summary only)
        List<Booking> bookings = bookingDAO.getBookingsByUserId(user.getId());

        // Set attribute and forward to JSP
        request.setAttribute("bookings", bookings);
        request.getRequestDispatcher("/views/client/history.jsp").forward(request, response);
    }
}
