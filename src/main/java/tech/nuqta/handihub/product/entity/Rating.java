package tech.nuqta.handihub.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.nuqta.handihub.common.BaseEntity;
import tech.nuqta.handihub.user.entity.User;

@Getter
@Setter
@Entity
@Table(name = "ratings")
public class Rating extends BaseEntity {
    private int rating;
    @Column(columnDefinition = "TEXT")
    private String comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    @PreUpdate
    private void prePersistOrUpdate() {
        product.updateRating(this.rating);
    }

    @PreRemove
    private void preRemove() {
        product.updateRatingOnDelete(this.rating);
    }
}