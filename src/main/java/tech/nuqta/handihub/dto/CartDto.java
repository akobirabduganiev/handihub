package tech.nuqta.handihub.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(
        Long id,
        List<CartItemDto> products,
        BigDecimal totalPrice,
        int totalQuantity
) {
}
