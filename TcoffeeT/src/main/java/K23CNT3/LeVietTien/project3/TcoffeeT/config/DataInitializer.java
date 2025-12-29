package K23CNT3.LeVietTien.project3.TcoffeeT.config;

import K23CNT3.LeVietTien.project3.TcoffeeT.model.User;
import K23CNT3.LeVietTien.project3.TcoffeeT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Tạo admin user nếu chưa có
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setEmail("admin@coffee.com");
            admin.setFullName("Administrator");
            admin.setRole(User.Role.ADMIN);
            admin.setIsActive(true);
            userRepository.save(admin);
            System.out.println("✅ Admin user created: username='admin', password='admin123'");
        } else {
            System.out.println("✅ Admin user already exists");
        }

        // Tạo user test nếu chưa có
        if (!userRepository.existsByUsername("user1")) {
            User user = new User();
            user.setUsername("user1");
            user.setPassword("user123");
            user.setEmail("user1@example.com");
            user.setFullName("Nguyễn Văn A");
            user.setRole(User.Role.USER);
            user.setIsActive(true);
            userRepository.save(user);
            System.out.println("✅ Test user created: username='user1', password='user123'");
        }
    }
}