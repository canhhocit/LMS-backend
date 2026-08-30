package com.ex.learninghub.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import com.ex.learninghub.common.response.ApiResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** Malformed JSON request body → 400 */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    ResponseEntity<ApiResponse<Object>> handlingHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Malformed JSON request")
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    /** Duplicate or FK-violating data → 400 (do not leak SQL details) */
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse<Object>> handlingDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Duplicate or invalid data")
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    /** Wrong path-variable / request-param type (e.g. /users/abc) → 400 */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiResponse<Object>> handlingTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Invalid parameter type: " + ex.getName())
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    /** Bean validation on @RequestParam / @PathVariable → 400 */
    @ExceptionHandler(value = ConstraintViolationException.class)
    ResponseEntity<ApiResponse<Object>> handlingConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .findFirst()
                .map(v -> v.getMessage())
                .orElse("Invalid request parameter");
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Object>> handlingRuntimeException(Exception ex) {
        log.error("Exception caught by handler: ", ex);
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                .build();
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Object>> handlingAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    /** Token missing, expired, or invalid → 401 */
    @ExceptionHandler(value = AuthenticationException.class)
    ResponseEntity<ApiResponse<Object>> handlingAuthenticationException(AuthenticationException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(ErrorCode.UNAUTHORIZED.getCode())
                .message(ErrorCode.UNAUTHORIZED.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    /** Authenticated but not enough role → 403 */
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Object>> handlingAccessDeniedException(AccessDeniedException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(ErrorCode.FORBIDDEN.getCode())
                .message(ErrorCode.FORBIDDEN.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }



    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Object>> handlingValidationException(MethodArgumentNotValidException ex) {
        var fieldErr = ex.getBindingResult().getFieldError();
        if (fieldErr == null) {
            return ResponseEntity.badRequest().build();
        }
        String message = fieldErr.getDefaultMessage();
        // Try to resolve as an ErrorCode key first, fall back to raw message
        try {
            ErrorCode errorCode = ErrorCode.valueOf(message);
            return ResponseEntity.status(errorCode.getStatusCode())
                    .body(ApiResponse.builder()
                            .code(errorCode.getCode())
                            .message(errorCode.getMessage())
                            .build());
        } catch (IllegalArgumentException e) {
            // Plain-text validation message (e.g. @Pattern message)
            return ResponseEntity.badRequest()
                    .body(ApiResponse.builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message(message)
                            .build());
        }
    }
}
