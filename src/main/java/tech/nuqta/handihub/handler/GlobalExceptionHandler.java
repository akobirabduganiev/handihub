package tech.nuqta.handihub.handler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.nuqta.handihub.exception.*;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;
import static tech.nuqta.handihub.handler.BusinessErrorCodes.*;

/**
 * This class provides global exception handling for the REST API.
 * It handles various exceptions that can occur during the execution of an operation.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exp) {
        return ResponseEntity
                .status(FORBIDDEN)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(USER_NOT_AUTHORIZED.getCode())
                                .errorDescription(USER_NOT_AUTHORIZED.getDescription())
                                .error(exp.getMessage())
                                .timestamp(System.currentTimeMillis())
                                .build()
                );
    }

    @ExceptionHandler(AppConflictException.class)
    public ResponseEntity<ExceptionResponse> handleException(AppConflictException exp) {
        return ResponseEntity
                .status(CONFLICT)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(APP_CONFLICT.getCode())
                                .errorDescription(APP_CONFLICT.getDescription())
                                .error(exp.getMessage())
                                .timestamp(System.currentTimeMillis())
                                .build()
                );
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(ACCOUNT_LOCKED.getCode())
                                .errorDescription(ACCOUNT_LOCKED.getDescription())
                                .error(exp.getMessage())
                                .timestamp(System.currentTimeMillis())
                                .build()
                );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(ACCOUNT_DISABLED.getCode())
                                .errorDescription(ACCOUNT_DISABLED.getDescription())
                                .error(exp.getMessage())
                                .timestamp(System.currentTimeMillis())
                                .build()
                );
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException() {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(BAD_CREDENTIALS.getCode())
                                .errorDescription(BAD_CREDENTIALS.getDescription())
                                .error("Login and / or Password is incorrect")
                                .timestamp(System.currentTimeMillis())
                                .build()
                );
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .errorCode(INTERNAL_SERVER_ERROR.value())
                                .timestamp(System.currentTimeMillis())
                                .build()
                );
    }

    @ExceptionHandler(ActivationTokenException.class)
    public ResponseEntity<ExceptionResponse> handleException(ActivationTokenException exp) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .errorCode(BAD_REQUEST.value())
                                .timestamp(System.currentTimeMillis())
                                .build()
                );
    }

    @ExceptionHandler(AppBadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleException(AppBadRequestException exp) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .errorCode(BAD_REQUEST.value())
                                .timestamp(System.currentTimeMillis())
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    //var fieldName = ((FieldError) error).getField();
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .validationErrors(errors)
                                .build()
                );
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(ItemNotFoundException exp) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .errorCode(NOT_FOUND.value())
                                .errorDescription(NOT_FOUND.getReasonPhrase())
                                .timestamp(System.currentTimeMillis())
                                .build()
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleException(IllegalArgumentException exp) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .errorCode(BAD_REQUEST.value())
                                .errorDescription(BAD_REQUEST.getReasonPhrase())
                                .timestamp(System.currentTimeMillis())
                                .build()
                );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleException(AccessDeniedException exp) {
        return ResponseEntity
                .status(FORBIDDEN)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .errorCode(FORBIDDEN.value())
                                .errorDescription(FORBIDDEN.getReasonPhrase())
                                .timestamp(System.currentTimeMillis())
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .errorDescription("Internal error, please contact the admin")
                                .error(exp.getMessage())
                                .errorCode(INTERNAL_SERVER_ERROR.value())
                                .timestamp(System.currentTimeMillis())
                                .build()
                );
    }
}
