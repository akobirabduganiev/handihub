package tech.nuqta.handihub.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * The CategoryCreateRequest class represents a request to create a new category.
 *
 * It contains the following fields:
 * - name: The name of the category (not null, maximum length of 255 characters).
 * - description: The description of the category.
 * - parentCategoryId: The ID of the parent category (nullable).
 *
 * This class is used as a parameter in the createCategory and createSubCategory methods of the CategoryController class.
 */
@Getter
@Setter
public class CategoryCreateRequest {
    @NotNull(message = "Name cannot be null")
    @Size(max = 255, message = "Name cannot be longer than 255 characters")
    @NotBlank(message = "Name cannot be blank")
    private String name;
    private String description;
    private Long parentCategoryId;
}
