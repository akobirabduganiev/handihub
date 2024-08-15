package tech.nuqta.handihub.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tech.nuqta.handihub.cart.dto.CartDto;
import tech.nuqta.handihub.cart.dto.CartItem;
import tech.nuqta.handihub.cart.entity.CartEntity;
import tech.nuqta.handihub.cart.repository.CartRepository;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.enums.CartStatus;
import tech.nuqta.handihub.exception.ItemNotFoundException;
import tech.nuqta.handihub.mapper.CartMapper;
import tech.nuqta.handihub.product.repository.ProductsRepository;
import tech.nuqta.handihub.user.entity.User;
import tech.nuqta.handihub.user.repository.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductsRepository productsRepository;
    private final CartMapper cartMapper = CartMapper.INSTANCE;

    @Override
    public ResponseMessage addOrUpdateCart(CartItem cartItem, Authentication authentication) {
        var user = (User) authentication.getPrincipal();

        var cartEntity = cartRepository.findByUserId(user).orElseGet(() -> {
            var newCart = new CartEntity();
            newCart.setUser(user);
            newCart.setStatus(CartStatus.ACTIVE);
            newCart.setProducts(new ArrayList<>());
            return newCart;
        });

        var product = productsRepository.findById(cartItem.productId())
                .orElseThrow(() -> new ItemNotFoundException("Product not found"));

        var existingProductOpt = cartEntity.getProducts().stream()
                .filter(cartProduct -> cartProduct.getId().equals(cartItem.productId()))
                .findFirst();

        if (existingProductOpt.isPresent()) {
            cartEntity.setTotalQuantity(cartEntity.getTotalQuantity() + cartItem.quantity());
            cartEntity.setTotalPrice(cartEntity.getTotalPrice()
                    .add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.quantity()))));
        } else {
            cartEntity.getProducts().add(product);
            cartEntity.setTotalQuantity(cartEntity.getTotalQuantity() + cartItem.quantity());
            cartEntity.setTotalPrice(cartEntity.getTotalPrice()
                    .add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.quantity()))));
        }

        cartRepository.save(cartEntity);
        CartDto cartDto = cartMapper.toCartDto(cartEntity);
        return new ResponseMessage(cartDto, "Cart created/updated successfully");
    }

    @Override
    public ResponseMessage getCart(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        var cartEntity = cartRepository.findByUserId(user)
                .orElseThrow(() -> new ItemNotFoundException("Cart not found"));

        CartDto cartDto = cartMapper.toCartDto(cartEntity);
        return new ResponseMessage(cartDto, "Cart retrieved successfully");
    }

    @Override
    public ResponseMessage deleteProductFromCart(Long productId, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        var cartEntity = cartRepository.findByUserId(user)
                .orElseThrow(() -> new ItemNotFoundException("Cart not found"));

        var product = productsRepository.findById(productId)
                .orElseThrow(() -> new ItemNotFoundException("Product not found"));

        var existingProductOpt = cartEntity.getProducts().stream()
                .filter(cartProduct -> cartProduct.getId().equals(productId))
                .findFirst();

        if (existingProductOpt.isPresent()) {
            var existingProduct = existingProductOpt.get();
            cartEntity.setTotalQuantity(cartEntity.getTotalQuantity() - 1);
            cartEntity.setTotalPrice(cartEntity.getTotalPrice().subtract(existingProduct.getPrice()));
            cartEntity.getProducts().remove(existingProduct);
        }

        cartRepository.save(cartEntity);
        CartDto cartDto = cartMapper.toCartDto(cartEntity);
        return new ResponseMessage(cartDto, "Product deleted from cart successfully");
    }

    @Override
    public ResponseMessage checkoutCart(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        var cartEntity = cartRepository.findByUserId(user)
                .orElseThrow(() -> new ItemNotFoundException("Cart not found"));

        cartEntity.setStatus(CartStatus.CHECKOUT);
        cartRepository.save(cartEntity);
        return new ResponseMessage("Cart checked out successfully");
    }
}
