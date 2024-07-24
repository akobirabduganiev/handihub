package tech.nuqta.handihub.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * The ResponseMessage class represents a response message sent back to the client.
 * It contains the following fields:
 * - status: The HTTP status of the response (always HttpStatus.OK).
 * - statusCode: The status code of the response (always 200).
 * - timestamp: The timestamp when the response was created (current system time in milliseconds).
 * - message: The message to be included in the response.
 * - data: The data to be included in the response.
 * <p>
 * This class is typically used to wrap the response data and provide additional information such as status and timestamp.
 */
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseMessage {
    private final HttpStatus status = HttpStatus.OK;
    private final int statusCode = HttpStatus.OK.value();
    private final Long timestamp = System.currentTimeMillis();
    private String message;
    private Object data;
    public ResponseMessage(String message) {
        this.message = message;
    }

    public ResponseMessage(Object data, String message) {
        this.message = message;
        this.data = data;
    }


}
