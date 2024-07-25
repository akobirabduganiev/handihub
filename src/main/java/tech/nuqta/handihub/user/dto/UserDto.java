package tech.nuqta.handihub.user.dto;

import tech.nuqta.handihub.enums.Gender;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link tech.nuqta.handihub.user.entity.User}
 */
public record UserDto(Long id, String firstname, String lastname, LocalDate dateOfBirth, String email,
                      Gender gender, boolean isVendor, boolean accountLocked, boolean enabled, List<Object> authorities,
                      LocalDateTime createdAt, LocalDateTime updatedAt, Long modifiedBy) implements Serializable {
}