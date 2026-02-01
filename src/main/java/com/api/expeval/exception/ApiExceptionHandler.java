package com.api.expeval.exception;

import java.time.Instant;

import jakarta.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.api.expeval.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ApiExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

  @ExceptionHandler(ExpressionValidationException.class)
  public ResponseEntity<ErrorResponse> handleValidation(ExpressionValidationException ex,
                                                        HttpServletRequest request) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex, request);
  }

  @ExceptionHandler(ExpressionEvaluationException.class)
  public ResponseEntity<ErrorResponse> handleEvaluation(ExpressionEvaluationException ex,
                                                        HttpServletRequest request) {
    return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex, request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgument(MethodArgumentNotValidException ex,
                                                            HttpServletRequest request) {
    String message = ex.getBindingResult().getAllErrors().stream()
        .findFirst()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .orElse("Validation failed.");
    return buildResponse(HttpStatus.BAD_REQUEST, new RuntimeException(message), request);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                 HttpServletRequest request) {
    String message = ex.getConstraintViolations().stream()
        .findFirst()
        .map(ConstraintViolation::getMessage)
        .orElse("Validation failed.");
    return buildResponse(HttpStatus.BAD_REQUEST, new RuntimeException(message), request);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex,
                                                              HttpServletRequest request) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex, request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnhandled(Exception ex, HttpServletRequest request) {
    log.error("Unhandled error", ex);
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
  }

  private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, Exception ex,
                                                      HttpServletRequest request) {
    ErrorResponse response = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(status.value())
        .error(status.getReasonPhrase())
        .message(ex.getMessage())
        .path(request.getRequestURI())
        .build();
    return ResponseEntity.status(status).body(response);
  }
}
