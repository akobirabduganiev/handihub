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
    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Rating> ratings;
    private double averageRating;
    private int ratingCount;

    public void updateRating(int newRating) {
        double totalRating = this.averageRating * this.ratingCount + newRating;
        this.ratingCount++;
        this.averageRating = totalRating / this.ratingCount;
    }

    public void updateRatingOnDelete(int ratingToRemove) {
        if (this.ratingCount > 1) {
            double totalRating = this.averageRating * this.ratingCount - ratingToRemove;
            this.ratingCount--;
            this.averageRating = totalRating / this.ratingCount;
        } else {
            this.ratingCount = 0;
            this.averageRating = 0.0;
        }
    }
}