package K23CNT3.LeVietTien.project3.onlycoffee.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 15)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String avatar;

    // QUAN TRỌNG: Dùng String để mapping với ENUM('USER', 'ADMIN', 'MANAGER') trong database
    @Column(nullable = false, columnDefinition = "ENUM('USER', 'ADMIN', 'MANAGER') DEFAULT 'USER'")
    private String role = "USER";

    public enum Status {
        ACTIVE, INACTIVE, BANNED
    }

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ACTIVE', 'INACTIVE', 'BANNED') DEFAULT 'ACTIVE'")
    private Status status = Status.ACTIVE;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // Helper enum cho code logic
    public enum RoleName {
        USER, ADMIN, MANAGER
    }

    // Spring Security Methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // QUAN TRỌNG: Thêm ROLE_ prefix cho Spring Security
        String authority = "ROLE_" + this.role;
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !status.equals(Status.BANNED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status.equals(Status.ACTIVE);
    }

    // Helper methods
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    public boolean isManager() {
        return "MANAGER".equals(this.role);
    }

    public boolean isAdminOrManager() {
        return isAdmin() || isManager();
    }

    // Helper method để lấy role với ROLE_ prefix
    public String getRoleWithPrefix() {
        return "ROLE_" + this.role;
    }
}