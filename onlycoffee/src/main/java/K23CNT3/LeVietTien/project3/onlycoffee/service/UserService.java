package K23CNT3.LeVietTien.project3.onlycoffee.service;

import K23CNT3.LeVietTien.project3.onlycoffee.dto.RegisterDTO;
import K23CNT3.LeVietTien.project3.onlycoffee.model.User;
import K23CNT3.LeVietTien.project3.onlycoffee.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(User.Status.ACTIVE);
        user.setEmailVerified(false);
        // Không set role ở đây vì đã có default trong entity
        return userRepository.save(user);
    }

    // Thêm method registerUser cho AuthController - SỬA LẠI
    @Transactional
    public User registerUser(RegisterDTO registerDTO) {
        System.out.println("📝 Bắt đầu đăng ký: " + registerDTO.getUsername());

        // Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            System.out.println("❌ Username đã tồn tại: " + registerDTO.getUsername());
            throw new RuntimeException("Username đã được sử dụng: " + registerDTO.getUsername());
        }

        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            System.out.println("❌ Email đã tồn tại: " + registerDTO.getEmail());
            throw new RuntimeException("Email đã được sử dụng: " + registerDTO.getEmail());
        }

        // Kiểm tra mật khẩu khớp
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            System.out.println("❌ Mật khẩu không khớp");
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        // Tạo user mới
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setFullName(registerDTO.getFullName());
        user.setPhone(registerDTO.getPhone());

        // QUAN TRỌNG: Role mặc định là "USER" (không phải ROLE_USER)
        // Entity sẽ tự động set "USER" vì có DEFAULT 'USER' trong database

        user.setStatus(User.Status.ACTIVE);
        user.setEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        System.out.println("✅ Tạo user thành công: " + user.getUsername());
        System.out.println("🔑 Password đã encode: " + user.getPassword().substring(0, 20) + "...");
        System.out.println("👑 Role: " + user.getRole());

        User savedUser = userRepository.save(user);
        System.out.println("💾 Đã lưu user vào database, ID: " + savedUser.getId());

        return savedUser;
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setFullName(userDetails.getFullName());
        user.setPhone(userDetails.getPhone());
        user.setAddress(userDetails.getAddress());
        user.setAvatar(userDetails.getAvatar());

        // Cập nhật role nếu có (vẫn dùng String)
        if (userDetails.getRole() != null) {
            // Đảm bảo role đúng với database enum
            String newRole = userDetails.getRole();
            if (newRole.equals("ADMIN") || newRole.equals("MANAGER") || newRole.equals("USER")) {
                user.setRole(newRole);
            }
        }

        if (userDetails.getStatus() != null) {
            user.setStatus(userDetails.getStatus());
        }

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setStatus(User.Status.INACTIVE);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void updateLastLogin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    // Sửa method này - query theo String role
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    // Sửa method này
    public List<User> getAdminsAndManagers() {
        return userRepository.findByRoleIn(List.of("ADMIN", "MANAGER"));
    }

    public long countActiveUsers() {
        return userRepository.countActiveUsers();
    }
}