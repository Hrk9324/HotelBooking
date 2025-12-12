package controller.admin;

import model.bean.User;
import model.bean.CustomerStats;
import model.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/customer-stats")
public class AdminCustomerStatsServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Security Check - admin only
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login?error=AccessDenied");
            return;
        }

        List<CustomerStats> stats = userDAO.getCustomerRoomUsageStats();
        request.setAttribute("stats", stats);
        request.getRequestDispatcher("/views/admin/customer_stats.jsp").forward(request, response);
    }
}

