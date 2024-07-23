package tech.nuqta.handihub.category.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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
