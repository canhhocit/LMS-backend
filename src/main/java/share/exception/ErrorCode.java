package share.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;
@Getter
public enum ErrorCode {
    // Email
    EMAIL_EXISTS(1002, "Email already exists", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(1001,"Email is required", HttpStatus.BAD_REQUEST),
    KEY_INVALID(999,"Key invalid", HttpStatus.BAD_REQUEST)
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
    
}
