package tech.nuqta.handihub.cart.service;

import org.springframework.security.core.Authentication;
import tech.nuqta.handihub.cart.dto.CartItem;
import tech.nuqta.handihub.common.ResponseMessage;

public interface CartService {
    ResponseMessage createCart(CartItem cartItem, Authentication authentication);
    ResponseMessage getCart(Authentication authentication);
    ResponseMessage updateCart(CartItem cartItem, Authentication authentication);
    ResponseMessage deleteProductFromCart(Long productId, Authentication authentication);
    ResponseMessage checkoutCart(Authentication authentication);

}
