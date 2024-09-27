package tech.nuqta.handihub.mapper;

import tech.nuqta.handihub.dto.ProductDTO;
import tech.nuqta.handihub.product.entity.ProductEntity;

import java.util.List;

public class ProductMapper {
    public static ProductDTO toDto(ProductEntity entity) {
        return new ProductDTO(
                entity.getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedBy(),
                entity.getName(),
                entity.getDescription(),
                entity.getSku(),
                entity.getBrand(),
                entity.getWeight(),
                entity.getDimensions(),
                entity.isEnabled(),
                entity.isFeatured(),
                entity.isHandmade(),
                entity.isMadeToOrder(),
                entity.getImages(),
                entity.getPrice(),
                entity.getQuantity(),
                entity.getUser().getId(),
                entity.getCategory().getId()
        );
    }

    public static List<ProductDTO> toDtoList(List<ProductEntity> content) {
        return content.stream()
                .map(ProductMapper::toDto)
                .toList();
    }
}
