package tech.nuqta.handihub.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Getter
public class ResponseMessage {
    private final HttpStatus status = HttpStatus.OK;
    private final int statusCode = HttpStatus.OK.value();
    private final Long timestamp = System.currentTimeMillis();
    private String message;

    public ResponseMessage(String message) {
        this.message = message;
    }


}
