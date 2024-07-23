package tech.nuqta.handihub.category.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.nuqta.handihub.category.entity.CategoryEntity;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    @Transactional
    @Query("SELECT c FROM CategoryEntity c WHERE c.isDeleted = false AND c.name = :name")
    Optional<CategoryEntity> findByName(String name);

    @Transactional
    @Query("SELECT c FROM CategoryEntity c WHERE c.isDeleted = false AND c.parentCategory IS NULL")
    List<CategoryEntity> findByParentCategoryIsNull(Sort sort);

    @Transactional
    @Query("SELECT c FROM CategoryEntity c WHERE c.isDeleted = false AND c.id = :id")
    Optional<CategoryEntity> findById(Long id);
}