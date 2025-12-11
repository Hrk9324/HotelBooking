package controller.client;

import model.bean.Hotel;
import model.bean.Room;
import model.bean.Service;
import model.dao.HotelDAO;
import model.dao.RoomDAO;
import model.dao.ServiceDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/hotel-detail")
public class HotelDetailServlet extends HttpServlet {
    private HotelDAO hotelDAO = new HotelDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private ServiceDAO serviceDAO = new ServiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get parameters
            String idParam = request.getParameter("id");
            String checkinParam = request.getParameter("checkin");
            String checkoutParam = request.getParameter("checkout");
            String peopleParam = request.getParameter("people");

            // Validate parameters
            if (idParam == null || checkinParam == null || checkoutParam == null) {
                response.sendRedirect("search?error=Missing parameters");
                return;
            }

            int hotelId = Integer.parseInt(idParam);
            
            // Parse dates
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilCheckIn = sdf.parse(checkinParam);
            java.util.Date utilCheckOut = sdf.parse(checkoutParam);
            Date checkIn = new Date(utilCheckIn.getTime());
            Date checkOut = new Date(utilCheckOut.getTime());

            // Fetch data
            Hotel hotel = hotelDAO.getHotelById(hotelId);
            List<Room> rooms = roomDAO.getAvailableRoomsByHotel(hotelId, checkIn, checkOut);
            List<Service> services = serviceDAO.getServicesByHotelId(hotelId);

            // Set attributes
            request.setAttribute("hotel", hotel);
            request.setAttribute("rooms", rooms);
            request.setAttribute("services", services);
            request.setAttribute("checkin", checkinParam);
            request.setAttribute("checkout", checkoutParam);
            request.setAttribute("people", peopleParam);

            // Forward to JSP
            request.getRequestDispatcher("/views/client/hotel_detail.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("search?error=Invalid request");
        }
    }
}
