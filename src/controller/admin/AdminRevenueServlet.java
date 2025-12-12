package controller.admin;

import model.bean.User;
import model.bean.RevenueStats;
import model.bean.Booking;
import model.dao.BookingDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/admin/revenue")
public class AdminRevenueServlet extends HttpServlet {

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

        String view = request.getParameter("view");
        if (view == null || view.isEmpty()) {
            view = "month"; // default view
        }

        BookingDAO bookingDAO = new BookingDAO();
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        try {
            if ("day".equals(view)) {
                // Get revenue by day for specific month/year
                String yearParam = request.getParameter("year");
                String monthParam = request.getParameter("month");
                String dateParam = request.getParameter("date");
                
                int year = (yearParam != null && !yearParam.isEmpty()) ? Integer.parseInt(yearParam) : currentYear;
                int month = (monthParam != null && !monthParam.isEmpty()) ? Integer.parseInt(monthParam) : currentMonth;

                List<RevenueStats> dailyStats = bookingDAO.getRevenueByDay(year, month);
                double totalDailyRevenue = dailyStats.stream()
                        .mapToDouble(RevenueStats::getTotalRevenue)
                        .sum();
                int totalDailyBookings = dailyStats.stream()
                        .mapToInt(RevenueStats::getBookingCount)
                        .sum();

                request.setAttribute("stats", dailyStats);
                request.setAttribute("totalRevenue", totalDailyRevenue);
                request.setAttribute("totalBookings", totalDailyBookings);
                request.setAttribute("selectedYear", year);
                request.setAttribute("selectedMonth", month);
                request.setAttribute("currentYear", currentYear);

                // If a specific date is selected, fetch bookings from that date (inclusive)
                if (dateParam != null && !dateParam.isEmpty()) {
                    String normalizedDate = dateParam;
                    try {
                        // Handle dd/MM/yyyy input (browser locale) and normalize to yyyy-MM-dd for DB/query + input value
                        if (dateParam.contains("/")) {
                            SimpleDateFormat src = new SimpleDateFormat("dd/MM/yyyy");
                            Date parsed = src.parse(dateParam);
                            normalizedDate = new java.sql.Date(parsed.getTime()).toString(); // yyyy-MM-dd
                        }
                    } catch (Exception ignore) {
                        // fallback: keep original
                    }

                    List<Booking> dateBookings = bookingDAO.getBookingsByDate(normalizedDate);
                    request.setAttribute("selectedDate", normalizedDate);
                    request.setAttribute("dateBookings", dateBookings);
                }

            } else if ("year".equals(view)) {
                // Get revenue by year (all time)
                List<RevenueStats> yearlyStats = bookingDAO.getRevenueByYear();
                double totalYearlyRevenue = yearlyStats.stream()
                        .mapToDouble(RevenueStats::getTotalRevenue)
                        .sum();
                int totalYearlyBookings = yearlyStats.stream()
                        .mapToInt(RevenueStats::getBookingCount)
                        .sum();

                request.setAttribute("stats", yearlyStats);
                request.setAttribute("totalRevenue", totalYearlyRevenue);
                request.setAttribute("totalBookings", totalYearlyBookings);

            } else {
                // Month view: allow selecting both year and month
                String yearParam = request.getParameter("year");
                String monthParam = request.getParameter("month");
                
                int year = (yearParam != null && !yearParam.isEmpty()) ? Integer.parseInt(yearParam) : currentYear;
                int month = (monthParam != null && !monthParam.isEmpty()) ? Integer.parseInt(monthParam) : currentMonth;

                List<RevenueStats> monthlyStats = new ArrayList<>();
                
                // If specific month selected, show only that month
                if (monthParam != null && !monthParam.isEmpty()) {
                    RevenueStats monthStat = bookingDAO.getRevenueByMonthAndYear(year, month);
                    if (monthStat.getPeriod() != null) {
                        monthlyStats.add(monthStat);
                    }
                } else {
                    // Otherwise show all months of the year
                    monthlyStats = bookingDAO.getRevenueByMonth(year);
                }
                
                double totalMonthlyRevenue = monthlyStats.stream()
                        .mapToDouble(RevenueStats::getTotalRevenue)
                        .sum();
                int totalMonthlyBookings = monthlyStats.stream()
                        .mapToInt(RevenueStats::getBookingCount)
                        .sum();

                request.setAttribute("stats", monthlyStats);
                request.setAttribute("totalRevenue", totalMonthlyRevenue);
                request.setAttribute("totalBookings", totalMonthlyBookings);
                request.setAttribute("selectedYear", year);
                request.setAttribute("selectedMonth", month);
                request.setAttribute("currentYear", currentYear);
            }

            request.setAttribute("view", view);
            request.getRequestDispatcher("/views/admin/revenue.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to load revenue statistics: " + e.getMessage());
            try {
                request.getRequestDispatcher("/views/admin/revenue.jsp").forward(request, response);
            } catch (ServletException | IOException ex) {
                ex.printStackTrace();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("error", "Invalid year or month format");
            try {
                request.getRequestDispatcher("/views/admin/revenue.jsp").forward(request, response);
            } catch (ServletException | IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
