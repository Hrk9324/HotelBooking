package controller.client;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/booking-success")
public class BookingSuccessServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Forward to booking success JSP
        // The 'id' parameter will automatically be available via ${param.id}
        request.getRequestDispatcher("/views/client/booking_success.jsp").forward(request, response);
    }
}
