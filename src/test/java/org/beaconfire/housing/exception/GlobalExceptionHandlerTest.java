package org.beaconfire.housing.exception;

import org.beaconfire.housing.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleReportNotFoundException_ShouldReturn404WithMessage() {
        // Given
        String errorMessage = "Report not found";
        ReportNotFoundException exception = new ReportNotFoundException(errorMessage);

        // When
        ApiResponse response = globalExceptionHandler.handleReportNotFound(exception);

        // Then
        assertEquals("404000", response.getErrorCode());
        assertEquals(errorMessage, response.getErrorMessage());
    }

    @Test
    void handleMethodArgumentNotValid_ShouldReturn400WithFieldErrorMessages() {
        FieldError fieldError = new FieldError("object", "field", "must not be null");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ApiResponse<?> response = globalExceptionHandler.handleValidation(ex);

        assertEquals("400001", response.getErrorCode());
        assertTrue(response.getErrorMessage().contains("field: must not be null"));
    }

    @Test
    void handleNoSuchElementException_ShouldReturn404() {
        String msg = "Item not found";
        NoSuchElementException ex = new NoSuchElementException(msg);

        ApiResponse<?> response = globalExceptionHandler.notFound(ex);

        assertEquals("404001", response.getErrorCode());
        assertEquals(msg, response.getErrorMessage());
    }

    @Test
    void handleIllegalArgumentException_ShouldReturn400() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");

        ApiResponse<?> response = globalExceptionHandler.badRequest(ex);

        assertEquals("400002", response.getErrorCode());
        assertEquals("Invalid input", response.getErrorMessage());
    }

    @Test
    void handleFacilityNotFoundException_ShouldReturn400() {
        FacilityNotFoundException ex = new FacilityNotFoundException("Facility missing");

        ApiResponse<?> response = globalExceptionHandler.handleFacilityReport(ex);

        assertEquals("400000", response.getErrorCode());
        assertEquals("Facility missing", response.getErrorMessage());
    }

    @Test
    void handleRoleCheckException() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        ApiResponse<?> response = globalExceptionHandler.handleRoleCheck(ex);

        assertEquals("401000", response.getErrorCode());
        assertEquals("Access denied", response.getErrorMessage());
    }

    @Test
    void handleDataIntegrityViolationException_ShouldReturn400() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicate key");

        ApiResponse<?> response = globalExceptionHandler.handleDataIntegrity(ex);

        assertEquals("400000", response.getErrorCode());
        assertEquals("Duplicate key", response.getErrorMessage());
    }

    @Test
    void handleGenericException_ShouldReturn500() {
        Exception ex = new Exception("Unexpected");

        ApiResponse<?> response = globalExceptionHandler.other(ex);

        assertEquals("500000", response.getErrorCode());
        assertEquals("The system is busy, please try again later", response.getErrorMessage());
    }
}