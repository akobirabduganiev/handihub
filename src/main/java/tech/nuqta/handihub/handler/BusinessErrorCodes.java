package tech.nuqta.handihub.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

/**
 * Enumeration of business error codes.
 * Each code consists of a numeric value, an associated HTTP status and a description.
 * The code represents a specific business error that can occur during the execution of an operation.
 */
@Getter
public enum BusinessErrorCodes {
    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST, "The new password does not match"),
    ACCOUNT_LOCKED(302, FORBIDDEN, "User account is locked"),
    ACCOUNT_DISABLED(303, FORBIDDEN, "User account is disabled"),
    BAD_CREDENTIALS(304, FORBIDDEN, "Login and / or Password is incorrect"),
    USER_NOT_FOUND(305, NOT_FOUND, "User not found"),
    APP_CONFLICT(306, CONFLICT, "Conflict occurred while processing the request"),
    USER_NOT_AUTHORIZED(307, FORBIDDEN, "User is not authorized to perform this operation");


    private final int code;
    private final String description;
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, HttpStatus status, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = status;
    }
}
