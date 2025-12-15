package K23CNT3.LeVietTien.project3.onlycoffee.repository;

import K23CNT3.LeVietTien.project3.onlycoffee.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Tìm user theo username hoặc email
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    // Tìm user theo role (String)
    List<User> findByRole(String role);

    // Tìm user theo multiple roles
    @Query("SELECT u FROM User u WHERE u.role IN :roles")
    List<User> findByRoleIn(@Param("roles") List<String> roles);

    // Đếm active users
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'ACTIVE'")
    long countActiveUsers();
}