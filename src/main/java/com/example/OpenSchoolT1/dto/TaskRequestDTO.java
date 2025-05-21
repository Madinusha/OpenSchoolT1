package com.example.OpenSchoolT1.dto;

import com.example.OpenSchoolT1.entity.TaskStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRequestDTO {
	@NotBlank(message = "Title cannot be blank")
	private String title;

	@NotBlank(message = "Description cannot be blank")
	private String description;

	@NotNull(message = "User ID cannot be null")
	private Long userId;

	@NotNull(message = "Status cannot be null")
	@Enumerated(EnumType.STRING)
	private TaskStatus status;
}