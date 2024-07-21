package tech.nuqta.handihub.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponse {
    private Long id;
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String refreshToken;
    private String accessToken;
}
