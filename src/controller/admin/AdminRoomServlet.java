package controller.admin;

import model.bean.Hotel;
import model.bean.Room;
import model.bean.User;
import model.dao.HotelDAO;
import model.dao.RoomDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/rooms")
public class AdminRoomServlet extends HttpServlet {
    private RoomDAO roomDAO = new RoomDAO();
    private HotelDAO hotelDAO = new HotelDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Security Check
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login?error=AccessDenied");
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            // Default: List all rooms
            List<Room> rooms = roomDAO.getAllRooms();
            request.setAttribute("rooms", rooms);
            request.getRequestDispatcher("/views/admin/room_list.jsp").forward(request, response);
        } else if ("new".equals(action)) {
            // Show form for new room - load hotels for dropdown
            List<Hotel> hotels = hotelDAO.getAllHotels();
            request.setAttribute("hotels", hotels);
            request.getRequestDispatcher("/views/admin/room_form.jsp").forward(request, response);
        } else if ("edit".equals(action)) {
            // Show form for editing room
            int id = Integer.parseInt(request.getParameter("id"));
            Room room = roomDAO.getRoomById(id);
            List<Hotel> hotels = hotelDAO.getAllHotels();
            request.setAttribute("room", room);
            request.setAttribute("hotels", hotels);
            request.getRequestDispatcher("/views/admin/room_form.jsp").forward(request, response);
        } else if ("delete".equals(action)) {
            // Delete room
            int id = Integer.parseInt(request.getParameter("id"));
            roomDAO.deleteRoom(id);
            response.sendRedirect(request.getContextPath() + "/admin/rooms?msg=deleted");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Security Check
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login?error=AccessDenied");
            return;
        }

        // Set Character Encoding
        request.setCharacterEncoding("UTF-8");

        // Get parameters
        String idParam = request.getParameter("id");
        String hotelIdParam = request.getParameter("hotel_id");
        String name = request.getParameter("name");
        String priceParam = request.getParameter("price");
        String capacityParam = request.getParameter("capacity");
        String status = request.getParameter("status");
        String description = request.getParameter("description");

        // VALIDATION LOGIC
        String error = null;

        // Validate room name
        if (name == null || name.trim().isEmpty()) {
            error = "Room name is required.";
        }

        // Validate hotel selection
        if (error == null && (hotelIdParam == null || hotelIdParam.isEmpty())) {
            error = "Please select a hotel.";
        }

        // Validate price
        double price = 0;
        if (error == null) {
            try {
                price = Double.parseDouble(priceParam);
                if (price < 0) {
                    error = "Price must be a positive number.";
                }
            } catch (NumberFormatException e) {
                error = "Invalid price format.";
            }
        }

        // Validate capacity
        int capacity = 0;
        if (error == null) {
            try {
                capacity = Integer.parseInt(capacityParam);
                if (capacity < 1) {
                    error = "Capacity must be at least 1.";
                }
            } catch (NumberFormatException e) {
                error = "Invalid capacity format.";
            }
        }

        // IF VALIDATION FAILS
        if (error != null) {
            request.setAttribute("error", error);
            // Keep user input
            request.setAttribute("name", name);
            request.setAttribute("price", priceParam);
            request.setAttribute("capacity", capacityParam);
            request.setAttribute("status", status);
            request.setAttribute("description", description);
            request.setAttribute("selectedHotelId", hotelIdParam);
            
            // Load hotels again for dropdown
            List<Hotel> hotels = hotelDAO.getAllHotels();
            request.setAttribute("hotels", hotels);
            
            // If editing, load room for ID
            if (idParam != null && !idParam.isEmpty()) {
                Room room = new Room();
                room.setId(Integer.parseInt(idParam));
                request.setAttribute("room", room);
            }
            
            request.getRequestDispatcher("/views/admin/room_form.jsp").forward(request, response);
            return;
        }

        // IF VALIDATION PASSES - Create Room object
        Room room = new Room();
        room.setHotelId(Integer.parseInt(hotelIdParam));
        room.setRoomName(name);
        room.setPrice(price);
        room.setCapacity(capacity);
        room.setStatus(status);
        room.setDescription(description);

        // Add or Update based on id
        if (idParam == null || idParam.isEmpty()) {
            // Add new room
            roomDAO.addRoom(room);
        } else {
            // Update existing room
            room.setId(Integer.parseInt(idParam));
            roomDAO.updateRoom(room);
        }

        // Redirect with success message
        response.sendRedirect(request.getContextPath() + "/admin/rooms?msg=saved");
    }
}
