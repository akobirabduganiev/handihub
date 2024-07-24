package tech.nuqta.handihub.mapper;

import tech.nuqta.handihub.user.dto.UserDto;
import tech.nuqta.handihub.user.entity.User;

import java.util.List;

public class UserMapper {
    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getFirstname(), user.getLastname(), user.getDateOfBirth(),
                user.getEmail(),user.getGender(), user.isVendor(), user.isAccountLocked(), user.isEnabled(),
                user.getRoles(), user.getCreatedDate(), user.getLastModifiedDate());
    }

    public static List<UserDto> toDtoList(List<User> users) {
        return users.stream().map(UserMapper::toDto).toList();
    }
}
