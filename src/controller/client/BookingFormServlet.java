package controller.client;

import model.bean.Room;
import model.bean.Service;
import model.bean.User;
import model.dao.RoomDAO;
import model.dao.ServiceDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

            // Calculation logic
            List<Room> selectedRooms = new ArrayList<>();
            double totalPrice = 0.0;
            int totalCapacity = 0;

            // Process rooms
            for (String roomIdStr : roomIds) {
                int roomId = Integer.parseInt(roomIdStr);
                Room room = roomDAO.getRoomById(roomId);
                if (room != null) {
                    selectedRooms.add(room);
                    totalPrice += room.getPrice();
                    totalCapacity += room.getCapacity();
                }
            }

            // Process services
            List<Service> selectedServices = new ArrayList<>();
            if (serviceIds != null) {
                for (String serviceIdStr : serviceIds) {
                    int serviceId = Integer.parseInt(serviceIdStr);
                    Service service = serviceDAO.getServiceById(serviceId);
                    if (service != null) {
                        selectedServices.add(service);
                        totalPrice += service.getPrice();
                    }
                }
            }

            // Set attributes
            request.setAttribute("selectedRooms", selectedRooms);
            request.setAttribute("selectedServices", selectedServices);
            request.setAttribute("totalPrice", totalPrice);
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
