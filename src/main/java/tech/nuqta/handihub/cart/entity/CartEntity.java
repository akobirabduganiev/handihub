package tech.nuqta.handihub.cart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.nuqta.handihub.common.BaseEntity;
import tech.nuqta.handihub.enums.CartStatus;
import tech.nuqta.handihub.product.entity.ProductEntity;
import tech.nuqta.handihub.user.entity.User;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "cart")
public class CartEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "carts_products",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<ProductEntity> products;
    private BigDecimal totalPrice;
    private Integer totalQuantity;
    @Enumerated(EnumType.STRING)
    private CartStatus status;
}