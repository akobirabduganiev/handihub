package tech.nuqta.handihub.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tech.nuqta.handihub.cart.dto.CartItem;
import tech.nuqta.handihub.cart.entity.CartEntity;
import tech.nuqta.handihub.cart.repository.CartRepository;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.enums.CartStatus;
import tech.nuqta.handihub.exception.ItemNotFoundException;
import tech.nuqta.handihub.mapper.CartMapper;
import tech.nuqta.handihub.product.repository.ProductsRepository;
import tech.nuqta.handihub.user.entity.User;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductsRepository productsRepository;

    @Override
    public ResponseMessage addOrUpdateCart(CartItem cartItem, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        var cartEntity = retrieveOrCreateCart(user);
        updateCartDetails(cartEntity, cartItem);
        cartRepository.save(cartEntity);
        var cartDto = CartMapper.toCartDto(cartEntity);
        return new ResponseMessage(cartDto, "Cart created/updated successfully");
    }

    @Override
    public ResponseMessage getCart(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        var cartEntity = cartRepository.findByUserId(user)
                .orElseThrow(() -> new ItemNotFoundException("Cart not found"));
        var cartDto = CartMapper.toCartDto(cartEntity);
        return new ResponseMessage(cartDto, "Cart retrieved successfully");
    }

    @Override
    public ResponseMessage deleteProductFromCart(Long productId, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        var cartEntity = cartRepository.findByUserId(user)
                .orElseThrow(() -> new ItemNotFoundException("Cart not found"));
        var product = productsRepository.findById(productId)
                .orElseThrow(() -> new ItemNotFoundException("Product not found"));
        cartEntity.getProducts().removeIf(cartProduct -> cartProduct.getId().equals(productId));
        cartEntity.setTotalQuantity(cartEntity.getTotalQuantity() - 1);
        cartEntity.setTotalPrice(cartEntity.getTotalPrice().subtract(product.getPrice()));
        cartRepository.save(cartEntity);
        var cartDto = CartMapper.toCartDto(cartEntity);
        return new ResponseMessage(cartDto, "Product deleted from cart successfully");
    }

    @Override
    public ResponseMessage checkoutCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        CartEntity cartEntity = cartRepository.findByUserId(user)
                .orElseThrow(() -> new ItemNotFoundException("Cart not found"));
        cartEntity.setStatus(CartStatus.CHECKOUT);
        cartRepository.save(cartEntity);
        return new ResponseMessage("Cart checked out successfully");
    }

    private CartEntity retrieveOrCreateCart(User user) {
        return cartRepository.findByUserId(user).orElseGet(() -> {
            CartEntity newCart = new CartEntity();
            newCart.setUser(user);
            newCart.setStatus(CartStatus.ACTIVE);
            newCart.setProducts(new ArrayList<>());
            newCart.setTotalPrice(BigDecimal.ZERO);
            newCart.setTotalQuantity(0);
            return newCart;
        });
    }

    private void updateCartDetails(CartEntity cartEntity, CartItem cartItem) {
        var product = productsRepository.findById(cartItem.productId())
                .orElseThrow(() -> new ItemNotFoundException("Product not found"));
        var existingProduct = cartEntity.getProducts().stream()
                .filter(cartProduct -> cartProduct.getId().equals(cartItem.productId()))
                .findFirst();
        if (existingProduct.isPresent()) {
            cartEntity.setTotalQuantity(cartEntity.getTotalQuantity() + cartItem.quantity());
            cartEntity.setTotalPrice(cartEntity.getTotalPrice()
                    .add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.quantity()))));
        } else {
            cartEntity.getProducts().add(product);
            cartEntity.setTotalQuantity(cartEntity.getTotalQuantity() + cartItem.quantity());
            cartEntity.setTotalPrice(cartEntity.getTotalPrice()
                    .add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.quantity()))));
        }
    }
}