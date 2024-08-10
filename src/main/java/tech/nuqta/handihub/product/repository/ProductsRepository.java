package tech.nuqta.handihub.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.nuqta.handihub.product.entity.ProductEntity;

import java.util.Optional;

public interface ProductsRepository extends JpaRepository<ProductEntity, Long> {

    @Override
    @Query("SELECT p FROM ProductEntity p WHERE p.isEnabled = true and p.quantity > 0 and p.isDeleted = false")
    Optional<ProductEntity> findById(Long id);


}