package K23CNT3.LeVietTien.project3.onlycoffee.security;

import K23CNT3.LeVietTien.project3.onlycoffee.model.User;
import K23CNT3.LeVietTien.project3.onlycoffee.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔍 Đang tìm user với: " + username);

        // Tìm theo username hoặc email
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> {
                    System.out.println("❌ Không tìm thấy user: " + username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
        System.out.println("✅ Tìm thấy user: " + user.getUsername());
        System.out.println("🔑 Password hash: " + user.getPassword());
        System.out.println("👑 Role: " + user.getRole());
        System.out.println("📊 Status: " + user.getStatus());
        System.out.println("🔐 Authorities: " + user.getAuthorities());

        return user;
    }
}