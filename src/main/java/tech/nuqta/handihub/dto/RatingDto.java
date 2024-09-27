package tech.nuqta.handihub.dto;

import lombok.Value;
import tech.nuqta.handihub.rating.Rating;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Rating}
 */
@Value
public class RatingDto implements Serializable {
    Long id;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    int rating;
    String comment;
    Long productId;
    Long userId;
}