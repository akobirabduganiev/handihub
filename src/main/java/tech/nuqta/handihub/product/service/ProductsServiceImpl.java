package tech.nuqta.handihub.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tech.nuqta.handihub.category.entity.CategoryEntity;
import tech.nuqta.handihub.category.repository.CategoryRepository;
import tech.nuqta.handihub.common.PageResponse;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.exception.ItemNotFoundException;
import tech.nuqta.handihub.product.dto.ProductDTO;
import tech.nuqta.handihub.product.dto.request.ProductCreateRequest;
import tech.nuqta.handihub.product.dto.request.ProductUpdateRequest;
import tech.nuqta.handihub.product.entity.ProductEntity;
import tech.nuqta.handihub.product.repository.ProductsRepository;
import tech.nuqta.handihub.user.entity.User;
import tech.nuqta.handihub.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductsServiceImpl implements ProductsService {

    private final ProductsRepository productsRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseMessage addProduct(ProductCreateRequest request, Authentication connectedUser) {
        var currentUser = (User) connectedUser.getPrincipal();
        var category = fetchCategoryById(request.categoryId());
        var user = fetchUserById(request.userId());

        if (!user.getId().equals(currentUser.getId())) {
            return new ResponseMessage("You are not authorized to add product for this user");
        }

        var product = createProductEntity(request, user, category);
        productsRepository.save(product);
        return new ResponseMessage("Product added successfully");
    }

    @Override
    public ResponseMessage updateProduct(ProductUpdateRequest request, Authentication connectedUser) {
        var currentUser = (User) connectedUser.getPrincipal();
        var product = productsRepository.findById(request.id())
                .orElseThrow(() -> new ItemNotFoundException("Product not found"));

        if (!product.getUser().getId().equals(currentUser.getId())) {
            return new ResponseMessage("You are not authorized to update this product");
        }

        var category = fetchCategoryById(request.categoryId());
        updateProductFields(request, product, category);
        productsRepository.save(product);
        return new ResponseMessage("Product updated successfully");
    }

    @Override
    public ResponseMessage deleteProduct(Long id, Authentication connectedUser) {
        return null;
    }

    @Override
    public ResponseMessage getProduct(Long id, Authentication connectedUser) {
        return null;
    }

    @Override
    public PageResponse<ProductDTO> getProducts(int page, int size) {
        return null;
    }

    private static ProductEntity createProductEntity(ProductCreateRequest request, User user, CategoryEntity category) {
        var product = new ProductEntity();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setSku(request.sku());
        product.setBrand(request.brand());
        product.setWeight(request.weight());
        product.setDimensions(request.dimensions());
        product.setEnabled(request.isEnabled());
        product.setFeatured(request.isFeatured());
        product.setHandmade(request.isHandmade());
        product.setMadeToOrder(request.madeToOrder());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        product.setUser(user);
        product.setCategory(category);
        return product;
    }

    private static void updateProductFields(ProductUpdateRequest request, ProductEntity product, CategoryEntity category) {
        Optional.ofNullable(request.name()).ifPresent(product::setName);
        Optional.ofNullable(request.description()).ifPresent(product::setDescription);
        Optional.ofNullable(request.sku()).ifPresent(product::setSku);
        Optional.ofNullable(request.brand()).ifPresent(product::setBrand);
        Optional.ofNullable(request.weight()).ifPresent(product::setWeight);
        Optional.ofNullable(request.dimensions()).ifPresent(product::setDimensions);
        Optional.ofNullable(request.isFeatured()).ifPresent(product::setFeatured);
        Optional.ofNullable(request.isHandmade()).ifPresent(product::setHandmade);
        Optional.ofNullable(request.madeToOrder()).ifPresent(product::setMadeToOrder);
        Optional.ofNullable(request.price()).ifPresent(product::setPrice);
        Optional.ofNullable(request.quantity()).ifPresent(product::setQuantity);
        product.setCategory(category);
    }

    private CategoryEntity fetchCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ItemNotFoundException("Category not found"));
    }

    private User fetchUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ItemNotFoundException("User not found"));
    }
}