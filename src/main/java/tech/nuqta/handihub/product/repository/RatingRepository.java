package tech.nuqta.handihub.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.nuqta.handihub.product.entity.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
  }