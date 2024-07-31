package tech.nuqta.handihub.product.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductUpdateRequest(
        @NotNull(message = "Product ID is required")
        Long id,
        @NotNull(message = "Product name is required")
        @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
        String name,
        @NotNull(message = "Product description is required")
        String description,
        @NotNull(message = "Product SKU is required")
        @Size(min = 3, max = 255, message = "Product SKU must be between 3 and 255 characters")
        String sku,
        @NotNull(message = "Product brand is required")
        @Size(min = 3, max = 255, message = "Product brand must be between 3 and 255 characters")
        String brand,
        @NotNull(message = "Product weight is required")
        Double weight,
        @NotNull(message = "Product dimensions is required")
        @Size(min = 3, max = 255, message = "Product dimensions must be between 3 and 255 characters")
        String dimensions,
        boolean isFeatured,
        boolean isHandmade,
        boolean madeToOrder,
        boolean isEnabled,
        @NotNull(message = "Product price is required")
        @Positive(message = "Product price must be greater than 0")
        Double price,
        @NotNull(message = "Product quantity is required")
        @Positive(message = "Product quantity must be greater than 0")

        Integer quantity,
        @NotNull(message = "User ID is required")
        Long userId,
        @NotNull(message = "Category ID is required")
        Long categoryId
) {
}
