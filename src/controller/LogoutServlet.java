package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get the current session (do not create a new one)
        HttpSession session = request.getSession(false);
        
        // If session exists, invalidate it
        if (session != null) {
            session.invalidate();
        }
        
        // Redirect to login page with logout message
        response.sendRedirect(request.getContextPath() + "/login?msg=logged_out");
    }
}
