package tech.nuqta.handihub.dto;

import jakarta.validation.constraints.NotNull;

public record CartItem(
        @NotNull(message = "Product ID is required")
        Long productId,
        @NotNull(message = "Quantity is required")
        Integer quantity
) {
}
