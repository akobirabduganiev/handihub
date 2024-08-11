package tech.nuqta.handihub.rating;

import org.springframework.security.core.Authentication;
import tech.nuqta.handihub.common.ResponseMessage;

public interface RatingService {
    ResponseMessage rateProduct(RateRequest request, Authentication connectedUser);

    Double getProductRating(Long id);
}
