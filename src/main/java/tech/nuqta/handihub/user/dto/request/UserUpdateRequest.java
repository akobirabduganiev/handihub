package tech.nuqta.handihub.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import tech.nuqta.handihub.enums.Gender;

import java.time.LocalDate;

@Getter
@Setter
public class UserUpdateRequest {
    @NotNull(message = "Id is required")
    private Long id;
    @NotNull(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstname;
    @NotNull(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastname;
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    private Gender gender;

}
