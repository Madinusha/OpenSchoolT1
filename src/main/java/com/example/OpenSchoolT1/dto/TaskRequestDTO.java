package com.example.OpenSchoolT1.dto;

import com.example.OpenSchoolT1.entity.TaskStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class TaskRequestDTO {
	private String title;
	private String description;
	private Long userId;

	@Enumerated(EnumType.STRING)
	private TaskStatus status;
}