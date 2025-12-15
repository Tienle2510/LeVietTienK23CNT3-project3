package K23CNT3.LeVietTien.project3.onlycoffee.config;

import K23CNT3.LeVietTien.project3.onlycoffee.model.User;
import K23CNT3.LeVietTien.project3.onlycoffee.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DatabaseInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initUsers() {
        return args -> {
            // Kiểm tra và tạo tài khoản admin nếu chưa tồn tại
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@onlycoffee.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFullName("Administrator");
                admin.setRole("ADMIN");
                admin.setStatus(User.Status.ACTIVE);

                userRepository.save(admin);
                System.out.println("✅ Đã tạo tài khoản Admin: admin / admin123");
            }

            // Kiểm tra và tạo tài khoản manager nếu chưa tồn tại
            if (!userRepository.existsByUsername("manager")) {
                User manager = new User();
                manager.setUsername("manager");
                manager.setEmail("manager@onlycoffee.com");
                manager.setPassword(passwordEncoder.encode("manager123"));
                manager.setFullName("Manager");
                manager.setRole("MANAGER");
                manager.setStatus(User.Status.ACTIVE);

                userRepository.save(manager);
                System.out.println("✅ Đã tạo tài khoản Manager: manager / manager123");
            }

            // Kiểm tra và tạo tài khoản user demo
            if (!userRepository.existsByUsername("user")) {
                User user = new User();
                user.setUsername("user");
                user.setEmail("user@onlycoffee.com");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setFullName("Demo User");
                user.setRole("USER");
                user.setStatus(User.Status.ACTIVE);

                userRepository.save(user);
                System.out.println("✅ Đã tạo tài khoản User demo: user / user123");
            }
        };
    }
}