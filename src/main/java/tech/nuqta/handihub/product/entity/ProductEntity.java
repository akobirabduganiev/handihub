package tech.nuqta.handihub.product.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.nuqta.handihub.category.entity.CategoryEntity;
import tech.nuqta.handihub.common.BaseEntity;
import tech.nuqta.handihub.user.entity.User;

import java.util.List;

/**
 * This class represents a ProductEntity.
 * It extends the BaseEntity class and stores information about a product in the database.
 */
@Getter
@Setter
@Entity
@Table(name = "products")
public class ProductEntity extends BaseEntity {
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String sku;
    private String brand;
    private Double weight;
    private String dimensions;
    private boolean isEnabled;
    private boolean isFeatured;
    private boolean isHandmade;
    private boolean madeToOrder;
    @ElementCollection
    private List<String> images;
    private Double price;
    private Integer quantity;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private CategoryEntity category;
}