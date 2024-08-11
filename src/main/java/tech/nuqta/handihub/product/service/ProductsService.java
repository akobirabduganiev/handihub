package tech.nuqta.handihub.product.service;

import org.springframework.security.core.Authentication;
import tech.nuqta.handihub.common.PageResponse;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.product.dto.ProductDTO;
import tech.nuqta.handihub.product.dto.request.ProductCreateRequest;
import tech.nuqta.handihub.product.dto.request.ProductUpdateRequest;
import tech.nuqta.handihub.rating.RateRequest;

public interface ProductsService {
    ResponseMessage addProduct(ProductCreateRequest request, Authentication connectedUser);
    ResponseMessage updateProduct(ProductUpdateRequest request, Authentication connectedUser);
    ResponseMessage deleteProduct(Long id, Authentication connectedUser);
    ResponseMessage getProduct(Long id);
    PageResponse<ProductDTO> getProducts(int page, int size);


}
