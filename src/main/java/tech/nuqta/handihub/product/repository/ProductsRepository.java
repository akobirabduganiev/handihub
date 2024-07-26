package tech.nuqta.handihub.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.nuqta.handihub.product.entity.ProductEntity;

public interface ProductsRepository extends JpaRepository<ProductEntity, Long> {

}