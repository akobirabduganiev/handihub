package tech.nuqta.handihub.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordUpdateRequest {
    @NotNull(message = "Id is required")
    private Long id;
    private String oldPassword;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;
}
