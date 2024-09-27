package tech.nuqta.handihub.product.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import tech.nuqta.handihub.common.PageResponse;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.dto.ProductDTO;
import tech.nuqta.handihub.product.dto.request.ProductCreateRequest;
import tech.nuqta.handihub.product.dto.request.ProductUpdateRequest;

public interface ProductsService {
    ResponseMessage addProduct(ProductCreateRequest request, Authentication connectedUser);
    ResponseMessage updateProduct(ProductUpdateRequest request, Authentication connectedUser);
    ResponseMessage deleteProduct(Long id, Authentication connectedUser);
    ResponseMessage getProduct(Long id);
    PageResponse<ProductDTO> getProducts(int page, int size);


     ResponseMessage addImages(Long id, MultipartFile[] images, Authentication authentication);

}
