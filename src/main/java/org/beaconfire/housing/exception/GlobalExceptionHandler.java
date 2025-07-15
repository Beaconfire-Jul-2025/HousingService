package org.beaconfire.housing.exception;

import org.beaconfire.housing.dto.ApiResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.application.name:beaconfire}")
    private String appName;

    private String traceId() {
        return MDC.get("traceId");
    }

    private String host() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private ApiResponse<?> fail(String errorCode, String errorMessage, int showType) {
        return ApiResponse.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .showType(showType)
                .traceId(traceId())
                .host(host())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return fail("400001", msg, 2);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ApiResponse<?> notFound(NoSuchElementException ex) {
        return fail("404001", ex.getMessage(), 2);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<?> badRequest(IllegalArgumentException ex) {
        return fail("400002", ex.getMessage(), 2);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> other(Exception ex) {
        return fail("500000", "The system is busy, please try again later", 2);
    }

    // TODO: Add specific exception handlers for your application, wrap them in ApiResponse
    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleReportNotFound(ReportNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(FacilityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFacilityReport(FacilityNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(RoleCheckException.class)
    public ResponseEntity<Map<String, Object>> handleRoleCheck(RoleCheckException ex) {
        // User Role does not meet requirement
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }


}
