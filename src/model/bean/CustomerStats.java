package model.bean;

/**
 * Customer usage statistics view model for admin reporting.
 * Holds basic customer info along with booking and room usage counts.
 */
public class CustomerStats {
    private int userId;
    private String username;
    private String fullName;
    private String email;
    private int bookingCount;
    private int roomCount;

    public CustomerStats() {
    }

    public CustomerStats(int userId, String username, String fullName, String email, int bookingCount, int roomCount) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.bookingCount = bookingCount;
        this.roomCount = roomCount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(int bookingCount) {
        this.bookingCount = bookingCount;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(int roomCount) {
        this.roomCount = roomCount;
    }
}
