package tech.nuqta.handihub.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;
import java.util.Set;

/**
 * Represents the response structure for handling exceptions.
 * This class provides getters and setters for its properties.
 * It also uses lombok annotations to generate the boilerplate code.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {

    private Integer errorCode;
    private String errorDescription;
    private String error;
    private Long timestamp;
    private Set<String> validationErrors;
    private Map<String, String> errors;
}
