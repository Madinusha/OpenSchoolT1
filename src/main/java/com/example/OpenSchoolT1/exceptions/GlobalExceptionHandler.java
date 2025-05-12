package com.example.OpenSchoolT1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			errors.put(fieldName, error.getDefaultMessage());
		});
		return errors;
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		String message = "Invalid request format";
		if (ex.getMessage().contains("TaskStatus")) {
			message = "Invalid status. Allowed values: NEW, IN_PROGRESS, DONE, CANCELLED";
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}

	@ExceptionHandler(TaskNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleTaskNotFound(TaskNotFoundException ex) {
		return ex.getMessage();
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleAllExceptions() {
		return "Internal server error";
	}
}