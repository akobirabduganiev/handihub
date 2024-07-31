package tech.nuqta.handihub.product.service;

import org.springframework.security.core.Authentication;
import tech.nuqta.handihub.common.PageResponse;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.product.dto.ProductDTO;
import tech.nuqta.handihub.product.dto.request.ProductCreateRequest;
import tech.nuqta.handihub.product.dto.request.ProductUpdateRequest;

public class ProductsServiceImpl implements ProductsService {


    @Override
    public ResponseMessage addProduct(ProductCreateRequest request, Authentication connectedUser) {
        return null;
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
}
