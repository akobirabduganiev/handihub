package tech.nuqta.handihub.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.nuqta.handihub.user.entity.User;

import java.util.Optional;

/**
 * The UserRepository interface extends the JpaRepository interface for managing User entities in the database.
 * It provides methods for performing CRUD operations on User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isDeleted = false")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.isDeleted = false")
    Optional<User> findById(Long id);
}
