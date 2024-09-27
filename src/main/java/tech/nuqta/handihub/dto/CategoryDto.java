package tech.nuqta.handihub.dto;

import java.util.List;

/**
 * DTO for {@link tech.nuqta.handihub.category.entity.CategoryEntity}
 */
public record CategoryDto(Long id, String name, String description, List<CategoryDto> subCategories) {}