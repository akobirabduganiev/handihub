package tech.nuqta.handihub.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.nuqta.handihub.cart.entity.CartEntity;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
}