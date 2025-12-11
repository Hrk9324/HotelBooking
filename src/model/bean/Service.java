package model.bean;

public class Service {
    private int id;
    private int hotelId;
    private String name;
    private String description;
    private double price;

    // Empty constructor
    public Service() {
    }

    // Full constructor
    public Service(int id, int hotelId, String name, String description, double price) {
        this.id = id;
        this.hotelId = hotelId;
        this.name = name;
        this.description = description;
        this.price = price;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
