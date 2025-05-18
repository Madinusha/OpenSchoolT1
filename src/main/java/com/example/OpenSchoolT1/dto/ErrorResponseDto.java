package com.example.OpenSchoolT1.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter

public class ErrorResponseDto {

	private int status;
	private String message;
	private Map<String, String> errors;

	public ErrorResponseDto(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public ErrorResponseDto(int status, String message, Map<String, String> errors) {
		this.status = status;
		this.message = message;
		this.errors = errors;
	}
}