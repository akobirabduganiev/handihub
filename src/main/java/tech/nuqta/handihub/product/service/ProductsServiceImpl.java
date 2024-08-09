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

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductsServiceImpl implements ProductsService {
    private final ProductsRepository productsRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseMessage addProduct(ProductCreateRequest request, Authentication connectedUser) {
        var connectedUserPrincipal = (User) connectedUser.getPrincipal();
        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ItemNotFoundException("Category not found"));
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ItemNotFoundException("User not found"));
        if (!user.getId().equals(connectedUserPrincipal.getId())) {
            return new ResponseMessage("You are not authorized to add product for this user");
        }
        var product = createProductEntity(request, user, category);
        productsRepository.save(product);
        return new ResponseMessage("Product added successfully");
    }

    @Override
    public ResponseMessage updateProduct(ProductUpdateRequest request, Authentication connectedUser) {
        return null;
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
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setBrand(request.getBrand());
        product.setWeight(request.getWeight());
        product.setDimensions(request.getDimensions());
        product.setEnabled(request.isEnabled());
        product.setFeatured(request.isFeatured());
        product.setHandmade(request.isHandmade());
        product.setMadeToOrder(request.isMadeToOrder());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setUser(user);
        product.setCategory(category);
        return product;
    }
}
