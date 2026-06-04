package share.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
@Field
public enum ErrorCode {
    
    // user
    EMIAL_REQUIRED(1001,"Email is required", HttpStatus.BAD_REQUEST),;

    int code;

    // ErrorCode(int code, String message, HttpStatus statusCode) {
    //     this.code = code;
    //     this.me
    // }
    


}
