package tech.nuqta.handihub.product.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link tech.nuqta.handihub.product.entity.ProductEntity}
 */
public record ProductDTO(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdBy,
        Long modifiedBy,
        String name,
        String description,
        String sku,
        String brand,
        Double weight,
        String dimensions,
        boolean isEnabled,
        boolean isFeatured,
        boolean isHandmade,
        boolean madeToOrder,
        List<String> images,
        Double price,
        Integer quantity,
        Long userId,
        Long categoryId
) implements Serializable {
}