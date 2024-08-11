package tech.nuqta.handihub.rating;

import org.springframework.security.core.Authentication;
import tech.nuqta.handihub.common.PageResponse;
import tech.nuqta.handihub.common.ResponseMessage;

public interface RatingService {
    ResponseMessage rateProduct(RateRequest request, Authentication connectedUser);


    Double getAverageRating(Long productId);

    PageResponse<RatingDto> getAllProductRatings(Long productId, int page, int size);
}
