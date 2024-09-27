package tech.nuqta.handihub.rating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tech.nuqta.handihub.common.PageResponse;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.dto.RatingDto;
import tech.nuqta.handihub.exception.ItemNotFoundException;
import tech.nuqta.handihub.mapper.RatingMapper;
import tech.nuqta.handihub.product.repository.ProductsRepository;
import tech.nuqta.handihub.user.entity.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final ProductsRepository productsRepository;

    @Override
    public ResponseMessage rateProduct(RateRequest request, Authentication connectedUser) {
        var product = productsRepository.findById(request.productId())
                .orElseThrow(() -> new ItemNotFoundException("Product not found"));
        var currentUser = (User) connectedUser.getPrincipal();
        Rating rating = new Rating();
        rating.setRating(request.rating());
        rating.setProduct(product);
        rating.setUser(currentUser);
        ratingRepository.save(rating);
        log.info("Product with id {} rated by user with id {}", request.productId(), currentUser.getId());
        return new ResponseMessage("Product rated successfully");
    }

    @Override
    public Double getAverageRating(Long productId) {
        var product = productsRepository.findById(productId)
                .orElseThrow(() -> new ItemNotFoundException("Product not found"));
        return product.getAverageRating();
    }

    @Override
    public PageResponse<RatingDto> getAllProductRatings(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createdAt").descending());
        var ratings = ratingRepository.findAllByProductId(pageable, productId);
        return new PageResponse<>(
                RatingMapper.toDtoList(ratings.getContent()),
                ratings.getNumber() + 1,
                ratings.getSize(),
                ratings.getTotalElements(),
                ratings.getTotalPages(),
                ratings.isFirst(),
                ratings.isLast()
        );
    }

}
