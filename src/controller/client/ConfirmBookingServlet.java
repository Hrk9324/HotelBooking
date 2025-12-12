package controller.client;

import model.bean.User;
import model.dao.BookingDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/confirm-booking")
public class ConfirmBookingServlet extends HttpServlet {
    private BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // =============================================
        // 1. Authentication Check
        // =============================================
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login?msg=Please login to complete booking");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            // =============================================
            // 2. Parameter Retrieval & Parsing
            // =============================================
            String hotelId = request.getParameter("hotel_id");
            String checkinParam = request.getParameter("checkin");
            String checkoutParam = request.getParameter("checkout");
            String[] roomIdStrings = request.getParameterValues("room_ids");
            String[] serviceIdStrings = request.getParameterValues("service_ids");
            String[] guestNames = request.getParameterValues("guest_names");

            // =============================================
            // 3. Strict Validation
            // =============================================
            
            // Validate room selection
            if (roomIdStrings == null || roomIdStrings.length == 0) {
                response.sendRedirect("hotel-detail?id=" + hotelId + 
                                    "&checkin=" + checkinParam + 
                                    "&checkout=" + checkoutParam + 
                                    "&error=Please select at least one room");
                return;
            }

            // Validate guest names
            if (guestNames == null || guestNames.length == 0) {
                response.sendRedirect("hotel-detail?id=" + hotelId + 
                                    "&checkin=" + checkinParam + 
                                    "&checkout=" + checkoutParam + 
                                    "&error=Guest information is required");
                return;
            }

            // Validate all guest names are filled
            for (String guestName : guestNames) {
                if (guestName == null || guestName.trim().isEmpty()) {
                    response.sendRedirect("hotel-detail?id=" + hotelId + 
                                        "&checkin=" + checkinParam + 
                                        "&checkout=" + checkoutParam + 
                                        "&error=All guest names must be filled");
                    return;
                }
            }

            // Parse and validate dates
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilCheckIn = sdf.parse(checkinParam);
            java.util.Date utilCheckOut = sdf.parse(checkoutParam);
            Date checkIn = new Date(utilCheckIn.getTime());
            Date checkOut = new Date(utilCheckOut.getTime());

            // Validate date order - allow same-day check-in and check-out
            if (checkOut.before(checkIn)) {
                response.sendRedirect("hotel-detail?id=" + hotelId + 
                                    "&checkin=" + checkinParam + 
                                    "&checkout=" + checkoutParam + 
                                    "&error=Check-out date must be after check-in date");
                return;
            }

            // =============================================
            // 4. Data Conversion
            // =============================================
            List<Integer> roomIds = new ArrayList<>();
            for (String roomIdStr : roomIdStrings) {
                try {
                    roomIds.add(Integer.parseInt(roomIdStr));
                } catch (NumberFormatException e) {
                    response.sendRedirect("hotel-detail?id=" + hotelId + 
                                        "&checkin=" + checkinParam + 
                                        "&checkout=" + checkoutParam + 
                                        "&error=Invalid room selection");
                    return;
                }
            }

            List<Integer> serviceIds = new ArrayList<>();
            if (serviceIdStrings != null) {
                for (String serviceIdStr : serviceIdStrings) {
                    try {
                        serviceIds.add(Integer.parseInt(serviceIdStr));
                    } catch (NumberFormatException e) {
                        // Skip invalid service IDs
                        continue;
                    }
                }
            }

            List<String> guestNameList = new ArrayList<>();
            for (String guestName : guestNames) {
                guestNameList.add(guestName.trim());
            }

            // =============================================
            // 5. Call DAO with Transaction
            // =============================================
            int bookingId = bookingDAO.createBooking(
                user.getId(), 
                checkIn, 
                checkOut, 
                roomIds, 
                serviceIds, 
                guestNameList
            );

            // =============================================
            // 6. Success - Redirect to Confirmation Page
            // =============================================
            response.sendRedirect("booking-success?id=" + bookingId);

        } catch (SQLException e) {
            // Handle specific booking errors (e.g., room already booked)
            e.printStackTrace();
            String errorMessage = e.getMessage();
            
            if (errorMessage != null && errorMessage.contains("already booked")) {
                response.sendRedirect("search?error=Sorry, one or more selected rooms were just booked by another customer. Please try again.");
            } else {
                response.sendRedirect("search?error=Booking failed. Please try again.");
            }

        } catch (Exception e) {
            // Handle parsing or other errors
            e.printStackTrace();
            response.sendRedirect("search?error=Invalid booking data. Please try again.");
        }
    }
}
