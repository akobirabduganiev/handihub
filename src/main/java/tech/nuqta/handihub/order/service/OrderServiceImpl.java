package tech.nuqta.handihub.order.service;

import org.springframework.stereotype.Service;
import tech.nuqta.handihub.cart.entity.CartEntity;
import tech.nuqta.handihub.order.entity.OrderEntity;

@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public OrderEntity createOrderFromCart(CartEntity cartEntity) {
        return null;
    }
}
