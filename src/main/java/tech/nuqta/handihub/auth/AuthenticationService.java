package tech.nuqta.handihub.auth;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.email.EmailService;
import tech.nuqta.handihub.enums.EmailTemplateName;
import tech.nuqta.handihub.enums.RoleName;
import tech.nuqta.handihub.exception.AppBadRequestException;
import tech.nuqta.handihub.exception.ItemNotFoundException;
import tech.nuqta.handihub.role.RoleRepository;
import tech.nuqta.handihub.security.JwtService;
import tech.nuqta.handihub.token.Token;
import tech.nuqta.handihub.token.TokenRepository;
import tech.nuqta.handihub.user.entity.User;
import tech.nuqta.handihub.user.repository.UserRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * The AuthenticationService class provides methods for user registration, authentication, and account activation.
 * It uses various dependencies such as UserRepository, PasswordEncoder, JwtService, AuthenticationManager, TokenRepository, RoleRepository, and EmailService.
 */
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

    /**
     * Registers a new user.
     *
     * @param request The registration request containing user details.
     * @return A ResponseMessage indicating the success of the registration process.
     * @throws MessagingException if there is an error sending the validation email.
     */
    public ResponseMessage register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new AppBadRequestException("ROLE USER was not initiated"));

        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new AppBadRequestException("User with email " + request.getEmail() + " already exists");
                });
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
        return new ResponseMessage("User registered successfully. Please check your email for activation link");
    }

    /**
     * Authenticates a user using the provided authentication request.
     *
     * @param request The authentication request with email and password credentials.
     * @return An {@link AuthenticationResponse} object containing the user details and access tokens.
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppBadRequestException("Login failed. Invalid email or password"));
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

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

    /**
     * Refreshes the access token using the provided refresh token.
     *
     * @param refreshToken The refresh token to refresh the access token.
     * @return The authentication response with the refreshed access token.
     * @throws AppBadRequestException If the refresh token is invalid.
     * @throws ItemNotFoundException  If the user corresponding to the username extracted from the refresh token is not found.
     * @throws AppBadRequestException If the refresh token has expired.
     */
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
        } catch (ExpiredJwtException e) {
            throw new AppBadRequestException("Refresh token has expired");
        }
    }


    /**
     * Activates the account using the provided token.
     *
     * @param token the activation token
     * @return the response message indicating the result of the activation process
     * @throws MessagingException     if an error occurs while sending the validation email
     * @throws AppBadRequestException if the token is invalid or has expired
     * @throws ItemNotFoundException  if the user associated with the token is not found
     */
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

    /**
     * Generates an activation token for the given user and saves it in the token repository.
     *
     * @param user The user for whom the activation token is being generated and saved.
     * @return The generated activation token.
     */
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

    /**
     * Sends a validation email to the user.
     *
     * @param user The user object to whom the validation email will be sent
     * @throws MessagingException If an error occurs while sending the email
     */
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

    /**
     * Generates an activation code with the specified length.
     *
     * @param length the length of the activation code
     * @return the generated activation code as a string
     */
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
