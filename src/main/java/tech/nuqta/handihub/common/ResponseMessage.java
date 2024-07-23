package tech.nuqta.handihub.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Getter
@Setter
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
