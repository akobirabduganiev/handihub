package tech.nuqta.handihub.mapper;

import tech.nuqta.handihub.dto.CategoryDto;
import tech.nuqta.handihub.category.entity.CategoryEntity;


import java.util.stream.Collectors;


/**
 * The CategoryMapper class provides static methods to map between CategoryEntity and CategoryDto objects.
 */
public class CategoryMapper {

    public static CategoryDto toDto(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }

        return new CategoryDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getSubCategories().stream()
                        .map(CategoryMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    public static CategoryEntity toEntity(CategoryDto dto) {
        if (dto == null) {
            return null;
        }

        var entity = new CategoryEntity();
        entity.setId(dto.id());
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setSubCategories(dto.subCategories().stream()
                .map(CategoryMapper::toEntity)
                .collect(Collectors.toList()));

        return entity;
    }
}
