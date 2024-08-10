package tech.nuqta.handihub.product.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductCreateRequest(
        @NotNull(message = "User ID is required") Long userId,
        @NotNull(message = "Category ID is required") Long categoryId,
        @NotNull(message = "Name is required") String name,
        @NotNull(message = "Description is required") String description,
        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than 0") Double price,
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0") Integer quantity,
        String sku,
        String brand,
        @NotNull(message = "Weight is required") Double weight,
        String dimensions,
        boolean isEnabled,
        boolean isFeatured,
        boolean isHandmade,
        boolean madeToOrder
) {}