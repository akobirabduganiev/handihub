package tech.nuqta.handihub.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tech.nuqta.handihub.cart.dto.CartItem;
import tech.nuqta.handihub.cart.repository.CartRepository;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.exception.ItemNotFoundException;
import tech.nuqta.handihub.product.repository.ProductsRepository;
import tech.nuqta.handihub.user.entity.User;
import tech.nuqta.handihub.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductsRepository productsRepository;

    @Override
    public ResponseMessage createCart(CartItem cartItem, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        var product = productsRepository.findById(cartItem.productId())
                .orElseThrow(() -> new ItemNotFoundException("Product not found"));
        return null;
    }

    @Override
    public ResponseMessage getCart(Authentication authentication) {
        return null;
    }

    @Override
    public ResponseMessage updateCart(CartItem cartItem, Authentication authentication) {
        return null;
    }

    @Override
    public ResponseMessage deleteProductFromCart(Long productId, Authentication authentication) {
        return null;
    }

    @Override
    public ResponseMessage checkoutCart(Authentication authentication) {
        return null;
    }
}
