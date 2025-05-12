package com.example.OpenSchoolT1.mapper;

import com.example.OpenSchoolT1.dto.TaskRequestDTO;
import com.example.OpenSchoolT1.dto.TaskResponseDTO;
import com.example.OpenSchoolT1.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

	public Task toEntity(TaskRequestDTO dto) {
		Task task = new Task();
		task.setTitle(dto.getTitle());
		task.setDescription(dto.getDescription());
		task.setUserId(dto.getUserId());
		task.setStatus(dto.getStatus());
		return task;
	}

	public TaskResponseDTO toDTO(Task entity) {
		TaskResponseDTO dto = new TaskResponseDTO();
		dto.setId(entity.getId());
		dto.setTitle(entity.getTitle());
		dto.setDescription(entity.getDescription());
		dto.setUserId(entity.getUserId());
		dto.setStatus(entity.getStatus());
		return dto;
	}
}
