package com.example.OpenSchoolT1.exceptions;

import com.example.OpenSchoolT1.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			errors.put(fieldName, error.getDefaultMessage());
		});

		ErrorResponseDto errorResponse = new ErrorResponseDto(
				HttpStatus.BAD_REQUEST.value(),
				"Validation failed",
				errors
		);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		String message = "Invalid request format";
		if (ex.getMessage().contains("TaskStatus")) {
			message = "Invalid status. Allowed values: NEW, IN_PROGRESS, DONE, CANCELLED";
		}

		ErrorResponseDto errorResponse = new ErrorResponseDto(
				HttpStatus.BAD_REQUEST.value(),
				message
		);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(TaskNotFoundException.class)
	public ResponseEntity<ErrorResponseDto> handleTaskNotFound(TaskNotFoundException ex) {
		ErrorResponseDto errorResponse = new ErrorResponseDto(
				HttpStatus.NOT_FOUND.value(),
				ex.getMessage()
		);

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDto> handleAllExceptions() {
		ErrorResponseDto errorResponse = new ErrorResponseDto(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal server error"
		);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
}