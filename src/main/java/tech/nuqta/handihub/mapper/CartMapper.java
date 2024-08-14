package tech.nuqta.handihub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tech.nuqta.handihub.cart.dto.CartDto;
import tech.nuqta.handihub.cart.entity.CartEntity;

@Mapper
public interface CartMapper {
    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

    @Mapping(source = "products", target = "products")
    CartDto toCartDto(CartEntity cartEntity);
}
