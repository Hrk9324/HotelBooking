package model.bean;

public class BookingService {
    private int bookingId;
    private int serviceId;
    private double price;
    private int quantity;
    
    // Transient field for display only (not in DB)
    private transient String serviceName;

    // Empty constructor
    public BookingService() {
    }

    // Full constructor
    public BookingService(int bookingId, int serviceId, double price, int quantity) {
        this.bookingId = bookingId;
        this.serviceId = serviceId;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Transient field getter and setter
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
