package tech.nuqta.handihub.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordUpdateRequest {
    private Long id;
    private String oldPassword;
    private String newPassword;
}
