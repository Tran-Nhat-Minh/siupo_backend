package supbo.restaurant.userservice.repository;

import supbo.restaurant.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:keyword% OR u.email LIKE %:keyword% OR u.username LIKE %:keyword%")
    java.util.List<User> searchByKeyword(@Param("keyword") String keyword);
}