package K23CNT3.LeVietTien.project3.TcoffeeT.service;

import K23CNT3.LeVietTien.project3.TcoffeeT.model.User;
import K23CNT3.LeVietTien.project3.TcoffeeT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User login(String username, String password) {
        Optional<User> user = userRepository.findByUsernameAndPassword(username, password);
        return user.orElse(null);
    }

    public boolean isAdmin(String username, String password) {
        User user = login(username, password);
        return user != null && user.getRole() == User.Role.ADMIN;
    }
}