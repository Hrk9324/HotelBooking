package model.bean;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private int bookingId;
    private int userId;
    private Date checkinDate;
    private Date checkoutDate;
    private double totalAmount;
    private String paymentStatus;
    private Timestamp createdAt;
    
    // Transient fields (not in DB, used for View only)
    private String hotelName;
    private List<BookingGuest> guestList = new ArrayList<>();
    private List<BookingService> serviceList = new ArrayList<>();

    // Empty constructor
    public Booking() {
    }

    // Full constructor
    public Booking(int bookingId, int userId, Date checkinDate, Date checkoutDate, 
                   double totalAmount, String paymentStatus, Timestamp createdAt) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(Date checkinDate) {
        this.checkinDate = checkinDate;
    }

    public Date getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(Date checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // Transient field getters and setters
    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public List<BookingGuest> getGuestList() {
        return guestList;
    }

    public void setGuestList(List<BookingGuest> guestList) {
        this.guestList = guestList;
    }

    public List<BookingService> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<BookingService> serviceList) {
        this.serviceList = serviceList;
    }
}
