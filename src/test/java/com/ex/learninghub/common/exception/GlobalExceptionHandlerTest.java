package com.ex.learninghub.common.exception;

import com.ex.learninghub.modules.audit.service.SystemErrorLogService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final SystemErrorLogService systemErrorLogService = mock(SystemErrorLogService.class);
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(systemErrorLogService);

    @Test
    void recordsUnhandledServerErrors() {
        RuntimeException exception = new IllegalStateException("Service failed");

        var response = handler.handlingRuntimeException(exception);

        assertEquals(500, response.getStatusCode().value());
        verify(systemErrorLogService).record(exception, null);
    }

    @Test
    void doesNotRecordExpectedClientErrors() {
        handler.handlingAppException(new AppException(ErrorCode.INVALID_CREDENTIALS));

        verifyNoInteractions(systemErrorLogService);
    }
}
