package tech.nuqta.handihub.rating;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.nuqta.handihub.common.ResponseMessage;

@RestController
@RequestMapping("/api/v1/rating")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping("/rate")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ResponseMessage> rateProduct(@RequestBody @Valid RateRequest request, Authentication connectedUser) {
        return ResponseEntity.ok(ratingService.rateProduct(request, connectedUser));
    }

    @GetMapping("/{id}/rating")
    public ResponseEntity<Double> getProductRating(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.getProductRating(id));
    }

}
