package tech.nuqta.handihub.order.service;

import tech.nuqta.handihub.cart.entity.CartEntity;
import tech.nuqta.handihub.order.entity.OrderEntity;

public interface OrderService {
    OrderEntity createOrderFromCart(CartEntity cartEntity);
}
