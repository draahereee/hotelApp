package hotel.service;

import hotel.database.DatabaseHelper;
import hotel.model.User;

public class AuthService {
    public User login(String username, String password) {
        return DatabaseHelper.loginUser(username, password);
    }

    public boolean register(User user) {
        return DatabaseHelper.registerUser(user);
    }

    public boolean updateProfile(User user) {
        return DatabaseHelper.updateProfile(user);
    }
}
