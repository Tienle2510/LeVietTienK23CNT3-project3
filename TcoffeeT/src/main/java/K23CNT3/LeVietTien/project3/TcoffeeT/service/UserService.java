package K23CNT3.LeVietTien.project3.TcoffeeT.service;

import K23CNT3.LeVietTien.project3.TcoffeeT.model.User;
import K23CNT3.LeVietTien.project3.TcoffeeT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        List<User> users = userRepository.findAll();
        return users.stream().anyMatch(user -> email.equals(user.getEmail()));
    }

    public List<User> getUsersByRole(User.Role role) {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .filter(user -> user.getRole() == role)
                .toList();
    }

    public List<User> getActiveUsers() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .filter(user -> user.getIsActive() != null && user.getIsActive())
                .toList();
    }

    public long countUsers() {
        return userRepository.count();
    }

    public long countActiveUsers() {
        return getActiveUsers().size();
    }

    public User createUser(User user) {
        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        if (user == null) {
            return null;
        }

        if (userDetails.getFullName() != null) {
            user.setFullName(userDetails.getFullName());
        }
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPhone() != null) {
            user.setPhone(userDetails.getPhone());
        }
        if (userDetails.getAddress() != null) {
            user.setAddress(userDetails.getAddress());
        }
        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }
        if (userDetails.getIsActive() != null) {
            user.setIsActive(userDetails.getIsActive());
        }

        return userRepository.save(user);
    }

    // THÊM PHƯƠNG THỨC authenticate() ĐỂ FIX LỖI
    public User authenticate(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
            // Kiểm tra tài khoản có active không
            if (user.getIsActive() != null && !user.getIsActive()) {
                return null; // Tài khoản bị khóa
            }
            return user;
        }
        return null;
    }

    // THÊM PHƯƠNG THỨC usernameExists() - ĐÃ CÓ existsByUsername()
    // THÊM PHƯƠNG THỨC emailExists() - ĐÃ CÓ existsByEmail()
}