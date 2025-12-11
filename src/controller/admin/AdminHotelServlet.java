package controller.admin;

import model.bean.Hotel;
import model.bean.User;
import model.dao.HotelDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/hotels")
public class AdminHotelServlet extends HttpServlet {
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
            // Default: List all hotels
            List<Hotel> hotels = hotelDAO.getAllHotels();
            request.setAttribute("hotels", hotels);
            request.getRequestDispatcher("/views/admin/hotel_list.jsp").forward(request, response);
        } else if ("new".equals(action)) {
            // Show form for new hotel
            request.getRequestDispatcher("/views/admin/hotel_form.jsp").forward(request, response);
        } else if ("edit".equals(action)) {
            // Show form for editing hotel
            int id = Integer.parseInt(request.getParameter("id"));
            Hotel hotel = hotelDAO.getHotelById(id);
            request.setAttribute("hotel", hotel);
            request.getRequestDispatcher("/views/admin/hotel_form.jsp").forward(request, response);
        } else if ("delete".equals(action)) {
            // Delete hotel
            int id = Integer.parseInt(request.getParameter("id"));
            hotelDAO.deleteHotel(id);
            response.sendRedirect(request.getContextPath() + "/admin/hotels?msg=deleted");
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
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String description = request.getParameter("description");

        // Create Hotel object
        Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setAddress(address);
        hotel.setDescription(description);

        // Add or Update based on id
        if (idParam == null || idParam.isEmpty()) {
            // Add new hotel
            hotelDAO.addHotel(hotel);
        } else {
            // Update existing hotel
            hotel.setId(Integer.parseInt(idParam));
            hotelDAO.updateHotel(hotel);
        }

        // Redirect with success message
        response.sendRedirect(request.getContextPath() + "/admin/hotels?msg=saved");
    }
}
