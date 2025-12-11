package model.bean;

import java.sql.Timestamp;

public class BookingGuest {
    private int guestId;
    private int bookingId;
    private String fullName;
    private String checkinCode;
    private String checkinStatus;
    private Timestamp createdAt;

    // Empty constructor
    public BookingGuest() {
    }

    // Full constructor
    public BookingGuest(int guestId, int bookingId, String fullName, String checkinCode, 
                       String checkinStatus, Timestamp createdAt) {
        this.guestId = guestId;
        this.bookingId = bookingId;
        this.fullName = fullName;
        this.checkinCode = checkinCode;
        this.checkinStatus = checkinStatus;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCheckinCode() {
        return checkinCode;
    }

    public void setCheckinCode(String checkinCode) {
        this.checkinCode = checkinCode;
    }

    public String getCheckinStatus() {
        return checkinStatus;
    }

    public void setCheckinStatus(String checkinStatus) {
        this.checkinStatus = checkinStatus;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
