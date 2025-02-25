package es.test.inditex.adapter.rest.exception;

import es.test.inditex.domain.exception.PriceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PriceNotFoundException.class)
    public ResponseEntity<String> handlePriceNotFound(PriceNotFoundException ex) {
        log.error("PriceNotFoundException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
        log.error("MissingServletRequestParameterException: parámetro faltante {}", ex.getParameterName(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Parámetro obligatorio faltante: " + ex.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("MethodArgumentTypeMismatchException: parámetro {} inválido, se esperaba tipo {}",
                ex.getName(), ex.getRequiredType().getSimpleName(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Parámetro inválido: " + ex.getName() + " debe ser de tipo " + ex.getRequiredType().getSimpleName());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Excepción genérica no controlada", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor.");
    }
}
