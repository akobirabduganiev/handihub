package tech.nuqta.handihub.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.nuqta.handihub.common.PageResponse;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.rating.RateRequest;
import tech.nuqta.handihub.dto.RatingDto;
import tech.nuqta.handihub.rating.RatingService;

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

    @GetMapping("/{productId}/rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long productId) {
        return ResponseEntity.ok(ratingService.getAverageRating(productId));
    }

    @GetMapping("/get-all")
    public ResponseEntity<PageResponse<RatingDto>> getRatings(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ratingService.getAllProductRatings(productId, page, size));
    }

}
