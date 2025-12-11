package model.dao;

import model.bean.Service;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO extends BaseDAO {

    public List<Service> getServicesByHotelId(int hotelId) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT service_id, hotel_id, name, description, price FROM Services WHERE hotel_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, hotelId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("service_id"));
                    service.setHotelId(rs.getInt("hotel_id"));
                    service.setName(rs.getString("name"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getDouble("price"));
                    services.add(service);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    public Service getServiceById(int id) {
        Service service = null;
        String sql = "SELECT service_id, hotel_id, name, description, price FROM Services WHERE service_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    service = new Service();
                    service.setId(rs.getInt("service_id"));
                    service.setHotelId(rs.getInt("hotel_id"));
                    service.setName(rs.getString("name"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getDouble("price"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return service;
    }
}
