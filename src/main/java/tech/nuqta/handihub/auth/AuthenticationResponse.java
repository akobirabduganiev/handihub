package tech.nuqta.handihub.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tech.nuqta.handihub.role.Role;

import java.util.List;

@Getter
@Setter
@Builder
public class AuthenticationResponse {
    private Long id;
    private String fullName;
    private List<Role> roles;
    private String firstName;
    private String lastName;
    private String email;
    private String refreshToken;
    private String accessToken;
}
