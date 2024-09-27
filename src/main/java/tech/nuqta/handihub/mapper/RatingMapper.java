package tech.nuqta.handihub.mapper;

import tech.nuqta.handihub.rating.Rating;
import tech.nuqta.handihub.dto.RatingDto;

import java.util.List;

public class RatingMapper {
    public static RatingDto toDto(Rating rating) {
        return new RatingDto(
                rating.getId(),
                rating.getCreatedAt(),
                rating.getUpdatedAt(),
                rating.getRating(),
                rating.getComment(),
                rating.getProduct().getId(),
                rating.getUser().getId()
        );
    }

    public static List<RatingDto> toDtoList(List<Rating> content) {
        return content.stream()
                .map(RatingMapper::toDto)
                .toList();
    }
}
