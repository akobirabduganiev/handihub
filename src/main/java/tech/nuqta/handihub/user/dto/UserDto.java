package tech.nuqta.handihub.user.dto;

import tech.nuqta.handihub.enums.Gender;
import tech.nuqta.handihub.role.Role;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link tech.nuqta.handihub.user.entity.User}
 */
public record UserDto(Long id, String firstname, String lastname, LocalDate dateOfBirth, String email,
                      Gender gender, boolean isVendor, boolean accountLocked, boolean enabled, List<Role> roles,
                      LocalDateTime createdDate, LocalDateTime lastModifiedDate) implements Serializable {
}