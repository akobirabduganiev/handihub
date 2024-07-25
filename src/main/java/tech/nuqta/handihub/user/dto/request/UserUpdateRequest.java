package tech.nuqta.handihub.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import tech.nuqta.handihub.enums.Gender;

import java.time.LocalDate;

/**
 * The UserUpdateRequest class represents a request to update a user's information.
 * It contains the following fields:
 * - id: The identifier of the user.
 * - firstname: The first name of the user.
 * - lastname: The last name of the user.
 * - dateOfBirth: The date of birth of the user.
 * - gender: The gender of the user.
 * This class is typically used as a parameter in the updateUser method of the UserService interface.
 * It is also used in the updateUser method of the UserController class to handle HTTP requests related to user updates.
 */
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
