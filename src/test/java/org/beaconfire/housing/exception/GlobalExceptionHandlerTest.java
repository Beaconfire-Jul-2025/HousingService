package org.beaconfire.housing.exception;

import org.beaconfire.housing.dto.ApiResponse;
import org.beaconfire.housing.service.FacilityReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

//    @Test
//    void handleReportNotFoundException_ShouldReturn404WithMessage() {
//        // Given
//        String errorMessage = "Report not found";
//        ReportNotFoundException exception = new ReportNotFoundException(errorMessage);
//
//        // When
//        ApiResponse response = globalExceptionHandler.handleReportNotFound(exception);
//
//        // Then
//        assertEquals(404000, response.getErrorCode());
//        assertEquals(errorMessage, response.getErrorMessage());
//    }
}