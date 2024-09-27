package tech.nuqta.handihub.controller

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.mail.MessagingException
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tech.nuqta.handihub.auth.AuthenticationRequest
import tech.nuqta.handihub.auth.AuthenticationResponse
import tech.nuqta.handihub.auth.AuthenticationService
import tech.nuqta.handihub.auth.RegistrationRequest
import tech.nuqta.handihub.common.ResponseMessage

/**
 * The AuthenticationController class handles the authentication-related APIs.
 * It provides methods for user registration, user authentication, account activation, and OTP refreshing.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
class AuthenticationController (private val service: AuthenticationService) {

    @PostMapping("/register")
    @Throws(MessagingException::class)
    fun register(
        @RequestBody request: @Valid RegistrationRequest
    ): ResponseEntity<ResponseMessage?> {
        return ResponseEntity.ok<ResponseMessage?>(service.register(request))
    }

    @PostMapping("/authenticate")
    fun authenticate(
        @RequestBody request: AuthenticationRequest
    ): ResponseEntity<AuthenticationResponse?> {
        return ResponseEntity.ok<AuthenticationResponse?>(service.authenticate(request))
    }

    @GetMapping("/activate-account")
    @Throws(MessagingException::class)
    fun confirm(
        @RequestParam token: String?
    ): ResponseEntity<ResponseMessage?> {
        return ResponseEntity.ok<ResponseMessage?>(service.activateAccount(token))
    }

    @PostMapping("/refresh-token")
    fun refreshToken(
        request: HttpServletRequest

    ): ResponseEntity<AuthenticationResponse?> {
        var authorization = request.getHeader("Authorization")
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build<AuthenticationResponse?>()
        }
        authorization = authorization.substring(7)
        return ResponseEntity.ok<AuthenticationResponse?>(service.refreshToken(authorization))
    }
}
