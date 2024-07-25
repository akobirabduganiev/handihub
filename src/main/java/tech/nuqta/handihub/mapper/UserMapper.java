package tech.nuqta.handihub.mapper;

import tech.nuqta.handihub.user.dto.UserDto;
import tech.nuqta.handihub.user.entity.User;

import java.util.Arrays;
import java.util.List;

/**
 * The UserMapper class is responsible for mapping User objects to UserDto objects and vice versa.
 */
public class UserMapper {
    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getFirstname(), user.getLastname(), user.getDateOfBirth(),
                user.getEmail(),user.getGender(), user.isVendor(), user.isAccountLocked(), user.isEnabled(),
                Arrays.asList(user.getAuthorities().toArray()), user.getCreatedAt(), user.getUpdatedAt(), user.getModifiedBy());
    }

    public static List<UserDto> toDtoList(List<User> users) {
        return users.stream().map(UserMapper::toDto).toList();
    }
}
