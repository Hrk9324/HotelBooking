package controller.client;

import model.bean.Booking;
import model.bean.BookingGuest;
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

@WebServlet("/history-detail")
public class HistoryDetailServlet extends HttpServlet {
    private BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Authentication check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login?msg=Please login to view booking details");
            return;
        }

        // Get user from session
        User user = (User) session.getAttribute("user");

        try {
            // Get booking ID parameter
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect("history?error=Invalid booking ID");
                return;
            }

            int bookingId = Integer.parseInt(idParam);

            // Fetch booking details
            Booking booking = bookingDAO.getBookingById(bookingId);

            // Security check: Prevent viewing others' bookings
            if (booking == null || booking.getUserId() != user.getId()) {
                response.sendRedirect("history?error=Unauthorized access to booking");
                return;
            }

            // Fetch guests for this booking
            List<BookingGuest> guests = bookingDAO.getGuestsByBookingId(bookingId);
            booking.setGuestList(guests);

            // Set attribute and forward to detail page
            request.setAttribute("booking", booking);
            request.getRequestDispatcher("/views/client/history_detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("history?error=Invalid booking ID format");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("history?error=Failed to load booking details");
        }
    }
}
