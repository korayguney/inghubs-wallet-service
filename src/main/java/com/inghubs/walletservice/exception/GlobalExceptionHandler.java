package com.inghubs.walletservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Handles exceptions and provides consistent error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles WalletTransactionException and returns a custom error response.
     *
     * @param ex The WalletTransactionException instance.
     * @param request The WebRequest instance.
     * @return A ResponseEntity containing the error details.
     */
    @ExceptionHandler(WalletTransactionException.class)
    public ResponseEntity<Map<String, Object>> handleWalletTransactionException(
            WalletTransactionException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles specific exceptions related to customer and wallet not found scenarios.
     * Provides custom error responses for these exceptions.
     *
     * @param ex The exception instance.
     * @param request The WebRequest instance.
     * @return A ResponseEntity containing the error details.
     */
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCustomerNotFoundException(
            CustomerNotFoundException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("status", HttpStatus.NOT_FOUND.value());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions related to wallet not found scenarios.
     * Provides custom error responses for these exceptions.
     *
     * @param ex The exception instance.
     * @param request The WebRequest instance.
     * @return A ResponseEntity containing the error details.
     */
    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleWalletNotFoundException(
            WalletNotFoundException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("status", HttpStatus.NOT_FOUND.value());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions related to withdraw not allowed scenarios.
     * Provides custom error responses for these exceptions.
     *
     * @param ex The exception instance.
     * @param request The WebRequest instance.
     * @return A ResponseEntity containing the error details.
     */
    @ExceptionHandler(WithdrawNotAllowedException.class)
    public ResponseEntity<Map<String, Object>> handleWithdrawNotAllowedException(
            WithdrawNotAllowedException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("status", HttpStatus.FORBIDDEN.value());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles exceptions related to invalid payment amount scenarios.
     * Provides custom error responses for these exceptions.
     *
     * @param ex The exception instance.
     * @param request The WebRequest instance.
     * @return A ResponseEntity containing the error details.
     */
    @ExceptionHandler(InvalidPaymentAmountException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPaymentAmountException(
            InvalidPaymentAmountException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions related to insufficient balance scenarios.
     * Provides custom error responses for these exceptions.
     *
     * @param ex The exception instance.
     * @param request The WebRequest instance.
     * @return A ResponseEntity containing the error details.
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientBalanceException(
            InsufficientBalanceException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("status", HttpStatus.FORBIDDEN.value());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles AccessDeniedException and returns a custom error response.
     *
     * @param ex The AccessDeniedException instance.
     * @param request The WebRequest instance.
     * @return A ResponseEntity containing the error details.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", "Access Denied");
        errorDetails.put("details", ex.getMessage());
        errorDetails.put("status", HttpStatus.FORBIDDEN.value());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles generic exceptions and returns a custom error response.
     *
     * @param ex The Exception instance.
     * @param request The WebRequest instance.
     * @return A ResponseEntity containing the error details.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", "An unexpected error occurred.");
        errorDetails.put("details", ex.getMessage());
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}