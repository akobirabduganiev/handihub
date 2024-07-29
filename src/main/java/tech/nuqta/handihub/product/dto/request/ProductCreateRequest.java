package tech.nuqta.handihub.product.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProductCreateRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    @NotNull(message = "Name is required")
    private String name;
    @NotNull(message = "Description is required")
    private String description;
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;
    private String sku;
    private String brand;
    @NotNull(message = "Weight is required")
    private Double weight;
    private String dimensions;
    private boolean isEnabled;
    private boolean isFeatured;
    private boolean isHandmade;
    private boolean madeToOrder;
}
