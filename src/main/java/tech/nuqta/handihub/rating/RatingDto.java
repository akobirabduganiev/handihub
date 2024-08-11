package tech.nuqta.handihub.rating;

import lombok.Value;

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