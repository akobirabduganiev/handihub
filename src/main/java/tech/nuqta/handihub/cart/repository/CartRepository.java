package tech.nuqta.handihub.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.nuqta.handihub.cart.entity.CartEntity;
import tech.nuqta.handihub.user.entity.User;

import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    @Query("SELECT c FROM CartEntity c WHERE c.user = :user and c.isDeleted = false")
    Optional<CartEntity> findByUserId(User user);
}