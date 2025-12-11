package controller.client;

import model.bean.Hotel;
import model.bean.Room;
import model.dao.HotelDAO;
import model.dao.RoomDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    private RoomDAO roomDAO = new RoomDAO();
    private HotelDAO hotelDAO = new HotelDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get parameters
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        String peopleStr = request.getParameter("people");

        // Validation
        if (checkInStr == null || checkOutStr == null || peopleStr == null) {
            response.sendRedirect(request.getContextPath() + "/?error=missing_params");
            return;
        }

        // Parse dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date checkIn = null;
        Date checkOut = null;
        int people = 0;

        try {
            checkIn = new Date(sdf.parse(checkInStr).getTime());
            checkOut = new Date(sdf.parse(checkOutStr).getTime());
            people = Integer.parseInt(peopleStr);

            // Validate date logic
            if (checkOut.before(checkIn) || checkOut.equals(checkIn)) {
                response.sendRedirect(request.getContextPath() + "/?error=invalid_dates");
                return;
            }
        } catch (ParseException | NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/?error=invalid_format");
            return;
        }

        // Search available rooms
        List<Room> availableRooms = roomDAO.searchAvailableRooms(checkIn, checkOut);

        // Grouping Logic: Group rooms by Hotel
        Map<Integer, Hotel> hotelMap = new LinkedHashMap<>();
        for (Room room : availableRooms) {
            int hotelId = room.getHotelId();
            
            // If hotel not in map, fetch it and add
            if (!hotelMap.containsKey(hotelId)) {
                Hotel hotel = hotelDAO.getHotelById(hotelId);
                if (hotel != null) {
                    hotel.setAvailableRooms(new ArrayList<>());
                    hotelMap.put(hotelId, hotel);
                }
            }
            
            // Add room to hotel's available rooms
            Hotel hotel = hotelMap.get(hotelId);
            if (hotel != null) {
                hotel.getAvailableRooms().add(room);
            }
        }

        // Filtering Logic: Remove hotels that cannot accommodate the requested number of people
        Iterator<Map.Entry<Integer, Hotel>> iterator = hotelMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Hotel> entry = iterator.next();
            Hotel hotel = entry.getValue();
            
            // Calculate total capacity
            int totalCapacity = 0;
            for (Room room : hotel.getAvailableRooms()) {
                totalCapacity += room.getCapacity();
            }
            
            // Remove hotel if it cannot accommodate the group
            if (totalCapacity < people) {
                iterator.remove();
            }
        }

        // Convert map values to list
        List<Hotel> hotels = new ArrayList<>(hotelMap.values());

        // Set attributes
        request.setAttribute("hotels", hotels);
        request.setAttribute("search_checkin", checkInStr);
        request.setAttribute("search_checkout", checkOutStr);
        request.setAttribute("search_people", people);

        // Forward to search results page
        request.getRequestDispatcher("/views/client/search_result.jsp").forward(request, response);
    }
}
