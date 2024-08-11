package tech.nuqta.handihub.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.nuqta.handihub.category.entity.CategoryEntity;
import tech.nuqta.handihub.category.repository.CategoryRepository;
import tech.nuqta.handihub.common.PageResponse;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.exception.ItemNotFoundException;
import tech.nuqta.handihub.file.ImageUploadService;
import tech.nuqta.handihub.mapper.ProductMapper;
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
    private final ImageUploadService imageUploadService;


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
        log.info("Product with id {} added by user with id {}", product.getId(), currentUser.getId());
        return new ResponseMessage("Product added successfully");
    }

    @Override
    @Transactional
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
        log.info("Product with id {} updated by user with id {}", request.id(), currentUser.getId());
        return new ResponseMessage("Product updated successfully");
    }

    @Override
    public ResponseMessage deleteProduct(Long id, Authentication connectedUser) {
        var product = productsRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Product not found"));

        var currentUser = (User) connectedUser.getPrincipal();
        if (!product.getUser().getId().equals(currentUser.getId()) && !currentUser.isAdmin()) {
            return new ResponseMessage("You are not authorized to delete this product");
        }
        product.setIsDeleted(true);
        productsRepository.save(product);
        log.info("Product with id {} deleted by user with id {}", id, currentUser.getId());
        return new ResponseMessage("Product deleted successfully");

    }

    @Override
    public ResponseMessage getProduct(Long id) {
        var product = productsRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Product not found"));
        return new ResponseMessage(ProductMapper.toDto(product), "Product retrieved successfully");
    }

    @Override
    public PageResponse<ProductDTO> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        var products = productsRepository.findAll(pageable);
        return new PageResponse<>(
                ProductMapper.toDtoList(products.getContent()),
                products.getNumber() + 1,
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isFirst(),
                products.isLast()
        );
    }

    @Override
    public ResponseMessage addImages(Long productId, MultipartFile[] images, Authentication connectedUser) {
        var currentUser = (User) connectedUser.getPrincipal();
        var product = productsRepository.findById(productId)
                .orElseThrow(() -> new ItemNotFoundException("Product not found"));

        if (!product.getUser().getId().equals(currentUser.getId())) {
            return new ResponseMessage("You are not authorized to add images to this product");
        }

        for (MultipartFile image : images) {
            String filename = imageUploadService.store(image);
            product.getImages().add(filename);
        }

        productsRepository.save(product);
        log.info("Images added to product with id {} by user with id {}", productId, currentUser.getId());
        return new ResponseMessage("Images added successfully");
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