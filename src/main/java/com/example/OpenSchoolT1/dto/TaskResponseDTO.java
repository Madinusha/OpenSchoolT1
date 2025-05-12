package com.example.OpenSchoolT1.dto;

import com.example.OpenSchoolT1.entity.TaskStatus;
import lombok.Data;

@Data
public class TaskResponseDTO {
	private Long id;
	private String title;
	private String description;
	private Long userId;
	private TaskStatus status;
}
