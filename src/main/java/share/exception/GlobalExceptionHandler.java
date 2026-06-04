package share.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import share.dto.ApiResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // App Ex
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Object>> handlingAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        ApiResponse.builder().code(errorCode.getCode())
                .message(errorCode.getMessage()).build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // Method Arg
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Object>> handlingValidationException(MethodArgumentNotValidException ex) {
        var fieldErr = ex.getBindingResult().getFieldError();
        if (fieldErr == null) {
            return ResponseEntity.badRequest().build();
        }
        String errorKey = fieldErr.getDefaultMessage();
        ErrorCode errorCode = ErrorCode.KEY_INVALID;
        try {
            errorCode = ErrorCode.valueOf(errorKey);
        } catch (IllegalArgumentException e) {
            log.error("Unknow validatuon err key: {}", errorKey);
        }
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder().code(errorCode.getCode()).message(errorCode.getMessage())
                        .build());
    }

}
