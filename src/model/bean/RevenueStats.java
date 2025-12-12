package model.bean;

import java.io.Serializable;

public class RevenueStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private String period;          // Date, Month, or Year (format: YYYY-MM-DD, YYYY-MM, YYYY)
    private double totalRevenue;    // Total amount from paid bookings
    private int bookingCount;       // Number of bookings

    // Constructors
    public RevenueStats() {
    }

    public RevenueStats(String period, double totalRevenue, int bookingCount) {
        this.period = period;
        this.totalRevenue = totalRevenue;
        this.bookingCount = bookingCount;
    }

    // Getters and Setters
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(int bookingCount) {
        this.bookingCount = bookingCount;
    }

    // Calculate average revenue per booking
    public double getAveragePerBooking() {
        if (bookingCount == 0) return 0;
        return totalRevenue / bookingCount;
    }

    @Override
    public String toString() {
        return "RevenueStats{" +
                "period='" + period + '\'' +
                ", totalRevenue=" + totalRevenue +
                ", bookingCount=" + bookingCount +
                '}';
    }
}
