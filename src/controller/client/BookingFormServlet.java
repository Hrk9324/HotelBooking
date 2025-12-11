package controller.client;

import model.bean.Room;
import model.bean.Service;
import model.dao.RoomDAO;
import model.dao.ServiceDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@WebServlet("/booking-form")
public class BookingFormServlet extends HttpServlet {
    private RoomDAO roomDAO = new RoomDAO();
    private ServiceDAO serviceDAO = new ServiceDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Auth check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login?msg=Please login to book");
            return;
        }

        try {
            // Get parameters
            String hotelId = request.getParameter("hotel_id");
            String checkin = request.getParameter("checkin");
            String checkout = request.getParameter("checkout");
            String[] roomIds = request.getParameterValues("room_ids");
            String[] serviceIds = request.getParameterValues("service_ids");

            // Validate required parameters
            if (hotelId == null || checkin == null || checkout == null || roomIds == null || roomIds.length == 0) {
                response.sendRedirect("hotel-detail?id=" + hotelId + 
                                    "&checkin=" + checkin + 
                                    "&checkout=" + checkout + 
                                    "&error=Please select at least one room");
                return;
            }

            // Parse dates and calculate number of nights
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date checkInDate = sdf.parse(checkin);
            java.util.Date checkOutDate = sdf.parse(checkout);
            long diff = checkOutDate.getTime() - checkInDate.getTime();
            long nights = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            if (nights < 1) nights = 1; // Minimum 1 night

            // Process rooms and calculate room total per night
            List<Room> selectedRooms = new ArrayList<>();
            double roomTotalPerNight = 0.0;
            int totalCapacity = 0;

            for (String roomIdStr : roomIds) {
                int roomId = Integer.parseInt(roomIdStr);
                Room room = roomDAO.getRoomById(roomId);
                if (room != null) {
                    selectedRooms.add(room);
                    roomTotalPerNight += room.getPrice();
                    totalCapacity += room.getCapacity();
                }
            }

            // Process services and calculate service total
            List<Service> selectedServices = new ArrayList<>();
            double serviceTotal = 0.0;
            if (serviceIds != null) {
                for (String serviceIdStr : serviceIds) {
                    int serviceId = Integer.parseInt(serviceIdStr);
                    Service service = serviceDAO.getServiceById(serviceId);
                    if (service != null) {
                        selectedServices.add(service);
                        serviceTotal += service.getPrice();
                    }
                }
            }

            // Calculate grand total: (Room Price/night * Nights) + Services
            double grandTotal = (roomTotalPerNight * nights) + serviceTotal;

            // Set attributes
            request.setAttribute("selectedRooms", selectedRooms);
            request.setAttribute("selectedServices", selectedServices);
            request.setAttribute("nights", nights);
            request.setAttribute("roomTotalPerNight", roomTotalPerNight);
            request.setAttribute("serviceTotal", serviceTotal);
            request.setAttribute("totalPrice", grandTotal);
            request.setAttribute("totalCapacity", totalCapacity);
            request.setAttribute("checkin", checkin);
            request.setAttribute("checkout", checkout);
            request.setAttribute("hotel_id", hotelId);

            // Forward to booking form JSP
            request.getRequestDispatcher("/views/client/booking_form.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("search?error=Booking process failed");
        }
    }
}
