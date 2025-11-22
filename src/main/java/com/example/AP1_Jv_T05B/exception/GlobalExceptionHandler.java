package com.example.AP1_Jv_T05B.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(InvalidJwtException.class)
  public ResponseEntity<ErrorResponseDTO> handleInvalidJwtException(InvalidJwtException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
  }

  // Обрабатываем ошибки валидации @Valid
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponseDTO> handleValidationException(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();

    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.put(error.getField(), error.getDefaultMessage());
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ValidationErrorResponseDTO(LocalDateTime.now(), errors));
  }

  @ExceptionHandler(DataQueryException.class)
  public ResponseEntity<ErrorResponseDTO> handleDataQueryException(DataQueryException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(CannotJoinException.class)
  public ResponseEntity<ErrorResponseDTO> handleCannotJoinException(CannotJoinException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(NotYourTurnException.class)
  public ResponseEntity<ErrorResponseDTO> handleNotYourTurnException(NotYourTurnException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleUserNotFoundException(UserNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<ErrorResponseDTO> handleMissingHeader(MissingRequestHeaderException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(GameAlreadyStartedException.class)
  public ResponseEntity<ErrorResponseDTO> handleAlreadyStarted(GameAlreadyStartedException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(GameNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleNotFound(GameNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(GameAlreadyFinishedException.class)
  public ResponseEntity<ErrorResponseDTO> handleFinished(GameAlreadyFinishedException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(InvalidGameFieldException.class)
  public ResponseEntity<ErrorResponseDTO> handleInvalidField(InvalidGameFieldException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
  }

  // На случай непредвиденных ошибок
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleGeneral(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            new ErrorResponseDTO("Internal server error: " + ex.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponseDTO> handleInvalidCredentials(InvalidCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(
      DataIntegrityViolationException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponseDTO("User with this login already exists", LocalDateTime.now()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponseDTO("Request body is missing or invalid JSON", LocalDateTime.now()));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponseDTO> handleMissingRequestParam(
      MissingServletRequestParameterException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            new ErrorResponseDTO(
                "Missing required parameter: " + ex.getParameterName(), LocalDateTime.now()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            new ErrorResponseDTO(
                "Invalid type for parameter: " + ex.getName(), LocalDateTime.now()));
  }
}
