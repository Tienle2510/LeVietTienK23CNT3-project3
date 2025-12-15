package K23CNT3.LeVietTien.project3.onlycoffee;

import K23CNT3.LeVietTien.project3.onlycoffee.model.User;
import K23CNT3.LeVietTien.project3.onlycoffee.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class OnlycoffeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlycoffeeApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("=== KIỂM TRA VÀ TẠO TÀI KHOẢN DEMO ===");

            // 1. Tạo ADMIN account
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@onlycoffee.com");
                admin.setPassword(passwordEncoder.encode("admin123"));  // SỬA LẠI DÒNG NÀY
                admin.setFullName("Administrator");
                admin.setPhone("0123456789");
                admin.setAddress("Hà Nội");
                admin.setRole("ADMIN");
                admin.setStatus(User.Status.ACTIVE);

                userRepository.save(admin);
                System.out.println("✅ Admin account created: admin / admin123");
                System.out.println("   Password hash: " + admin.getPassword());
            } else {
                System.out.println("ℹ️ Admin account already exists");
            }

            // 2. Tạo MANAGER account
            if (userRepository.findByUsername("manager").isEmpty()) {
                User manager = new User();
                manager.setUsername("manager");
                manager.setEmail("manager@onlycoffee.com");
                manager.setPassword(passwordEncoder.encode("manager123"));
                manager.setFullName("Manager");
                manager.setPhone("0987654321");
                manager.setAddress("TP.HCM");
                manager.setRole("MANAGER");
                manager.setStatus(User.Status.ACTIVE);

                userRepository.save(manager);
                System.out.println("✅ Manager account created: manager / manager123");
            } else {
                System.out.println("ℹ️ Manager account already exists");
            }

            // 3. Tạo USER account
            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User();
                user.setUsername("user");
                user.setEmail("user@onlycoffee.com");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setFullName("Customer User");
                user.setPhone("0901234567");
                user.setAddress("Đà Nẵng");
                user.setRole("USER");
                user.setStatus(User.Status.ACTIVE);

                userRepository.save(user);
                System.out.println("✅ User account created: user / user123");
            } else {
                System.out.println("ℹ️ User account already exists");
            }

            // 4. Tạo tài khoản từ application.properties
            if (userRepository.findByUsername("admin@onlycoffee.vn").isEmpty()) {
                User adminProps = new User();
                adminProps.setUsername("admin@onlycoffee.vn");
                adminProps.setEmail("admin@onlycoffee.vn");
                adminProps.setPassword(passwordEncoder.encode("123456"));
                adminProps.setFullName("System Admin");
                adminProps.setPhone("0900000000");
                adminProps.setRole("ADMIN");
                adminProps.setStatus(User.Status.ACTIVE);

                userRepository.save(adminProps);
                System.out.println("✅ System Admin account created: admin@onlycoffee.vn / 123456");
            }

            System.out.println("=== KẾT THÚC KHỞI TẠO ===");
        };
    }
}