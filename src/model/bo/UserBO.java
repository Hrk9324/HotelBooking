package model.bo;

import model.bean.User;
import model.dao.UserDAO;
import java.util.ArrayList;

public class UserBO {
    private UserDAO dao = new UserDAO();

    public User checkLogin(String username, String password) {
        return dao.checkLogin(username, password);
    }

    public ArrayList<User> getAllUsers() {
        return dao.getAllUsers();
    }

    public User getUserById(int id) {
        return dao.getUserById(id);
    }

    public void createUser(User u) {
        dao.createUser(u);
    }

    public void updateUser(User u) {
        dao.updateUser(u);
    }

    public void deleteUser(int id) {
        dao.deleteUser(id);
    }
}
