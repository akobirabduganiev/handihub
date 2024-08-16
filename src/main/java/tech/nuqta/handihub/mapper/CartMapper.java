package tech.nuqta.handihub.mapper;

import tech.nuqta.handihub.cart.dto.CartDto;
import tech.nuqta.handihub.cart.dto.CartItemDto;
import tech.nuqta.handihub.cart.entity.CartEntity;

public class CartMapper {
    public static CartDto toCartDto(CartEntity cartEntity) {
        return new CartDto(
                cartEntity.getId(),
                cartEntity.getProducts()
                        .stream().map(product -> new CartItemDto(product.getId(), product.getQuantity())).toList(),
                cartEntity.getTotalPrice(),
                cartEntity.getTotalQuantity()
        );
    }
}
