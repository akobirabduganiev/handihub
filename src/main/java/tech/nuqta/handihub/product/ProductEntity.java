package tech.nuqta.handihub.product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import tech.nuqta.handihub.common.BaseEntity;
import tech.nuqta.handihub.user.User;

@Getter
@Setter
@Entity
@Table(name = "products")
public class ProductEntity extends BaseEntity {
    private String name;
    private String description;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}