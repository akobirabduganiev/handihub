package tech.nuqta.handihub.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * The UserPasswordUpdateRequest class represents a request to update a user's password.
 * <p>
 * It contains the following fields:
 * - id: The identifier of the user whose password will be updated.
 * - oldPassword: The user's current password.
 * - newPassword: The new password to be set for the user.
 * <p>
 * This class is typically used as a parameter in the updatePassword method of the UserService interface.
 * It is also used in the updatePassword method of the UserController class to handle HTTP requests related to password updates.
 */
@Getter
@Setter
public class UserPasswordUpdateRequest {
    @NotNull(message = "Id is required")
    private Long id;
    private String oldPassword;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;
}
