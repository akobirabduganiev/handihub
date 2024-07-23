package tech.nuqta.handihub.auth;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.email.EmailService;
import tech.nuqta.handihub.email.EmailTemplateName;
import tech.nuqta.handihub.enums.RoleName;
import tech.nuqta.handihub.exception.AppBadRequestException;
import tech.nuqta.handihub.exception.ItemNotFoundException;
import tech.nuqta.handihub.role.RoleRepository;
import tech.nuqta.handihub.security.JwtService;
import tech.nuqta.handihub.user.Token;
import tech.nuqta.handihub.user.TokenRepository;
import tech.nuqta.handihub.user.User;
import tech.nuqta.handihub.user.UserRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public ResponseMessage register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
        return new ResponseMessage("User registered successfully. Please check your email for activation link");
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        System.out.println("Thread: " + Thread.currentThread().getName());
        var user = (User) auth.getPrincipal();
        var claims = new HashMap<String, Object>();
        claims.put("fullName", user.getFullName());

        var jwtToken = jwtService.generateToken(claims, user);
        var refreshToken = jwtService.generateRefreshToken(claims, user);
        return AuthenticationResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .firstName(user.getFirstname())
                .lastName(user.getLastname())
                .refreshToken(refreshToken)
                .accessToken(jwtToken)
                .build();
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
       try {
           if (!jwtService.isRefreshToken(refreshToken)) {
               throw new AppBadRequestException("Invalid refresh token");
           }
           var username = jwtService.extractUsername(refreshToken);
           var user = userRepository.findByEmail(username)
                   .orElseThrow(() -> new ItemNotFoundException("User not found"));

           var claims = new HashMap<String, Object>();
           claims.put("fullName", user.getFullName());

           var newAccessToken = jwtService.generateToken(claims, user);
           return AuthenticationResponse.builder()
                   .id(user.getId())
                   .fullName(user.getFullName())
                   .email(user.getEmail())
                   .firstName(user.getFirstname())
                   .lastName(user.getLastname())
                   .refreshToken(refreshToken)
                   .accessToken(newAccessToken)
                   .build();
       }catch (ExpiredJwtException e) {
           throw new AppBadRequestException("Refresh token has expired");
       }
    }


    @Transactional
    public ResponseMessage activateAccount(String token) throws MessagingException {
        var savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new AppBadRequestException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new AppBadRequestException("Activation token has expired. A new token has been sent to the same email address");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new ItemNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
        return new ResponseMessage("Account activated successfully");
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateActivationCode(int length) {
        var characters = "0123456789";
        var codeBuilder = new StringBuilder();


        var secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
}
