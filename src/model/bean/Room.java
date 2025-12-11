package model.bean;

public class Room {
    private int id;
    private int hotelId;
    private String roomName;
    private double price;
    private int capacity;
    private String status;
    private String description;
    private String hotelName; // Helper field for display

    // Empty constructor
    public Room() {
    }

    // Full constructor
    public Room(int id, int hotelId, String roomName, double price, int capacity, String status, String description) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomName = roomName;
        this.price = price;
        this.capacity = capacity;
        this.status = status;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }
}
