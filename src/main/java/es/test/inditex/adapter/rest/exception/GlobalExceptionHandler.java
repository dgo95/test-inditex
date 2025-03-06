package es.test.inditex.adapter.rest.exception;

import es.test.inditex.domain.exception.PriceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PriceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePriceNotFound(PriceNotFoundException ex) {
        log.error("PriceNotFoundException: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "PRICE_NOT_FOUND",
                ex.getMessage(),
                "Please check the values for date, productId, and brandId."
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        String message = "Missing required parameter: " + ex.getParameterName();
        log.error("MissingServletRequestParameterException: {}", message);
        ErrorResponse error = new ErrorResponse(
                "MISSING_PARAMETER",
                message,
                "Ensure all required parameters are provided."
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid parameter: " + ex.getName() + " must be of type " +
                Objects.requireNonNull(ex.getRequiredType()).getSimpleName() +
                ". Provided value: " + ex.getValue();
        log.error("MethodArgumentTypeMismatchException: {}", message);
        ErrorResponse error = new ErrorResponse(
                "INVALID_PARAMETER_TYPE",
                message,
                "Please verify the parameter types."
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        String errorMessage = "Validation error: " +
                Arrays.stream(ex.getDetailMessageArguments())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
        log.error("HandlerMethodValidationException: {}", errorMessage);
        ErrorResponse error = new ErrorResponse(
                "VALIDATION_ERROR",
                errorMessage,
                "Ensure that all validation constraints are met."
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Internal server error.",
                "An unexpected error occurred. Please contact support."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
