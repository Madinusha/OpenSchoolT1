package com.example.OpenSchoolT1.service;

import com.example.OpenSchoolT1.annotation.Loggable;
import com.example.OpenSchoolT1.annotation.MeasureTime;
import com.example.OpenSchoolT1.dto.TaskRequestDTO;
import com.example.OpenSchoolT1.dto.TaskResponseDTO;
import com.example.OpenSchoolT1.exceptions.TaskNotFoundException;
import com.example.OpenSchoolT1.mapper.TaskMapper;
import com.example.OpenSchoolT1.repository.TaskRepository;
import com.example.OpenSchoolT1.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
	private final TaskRepository taskRepository;
	private final TaskMapper taskMapper;

	@Loggable
	@Transactional
	public TaskResponseDTO createTask(TaskRequestDTO dto) {
		Task task = taskMapper.toEntity(dto);
		Task saved = taskRepository.save(task);
		return taskMapper.toDTO(saved);
	}

	@Loggable
	public TaskResponseDTO getTaskById(Long id) {
		return taskRepository.findById(id)
				.map(taskMapper::toDTO)
				.orElseThrow(() -> new TaskNotFoundException("Task not found"));
	}

	@Loggable
	@MeasureTime
	@Transactional
	public TaskResponseDTO updateTask(Long id, TaskRequestDTO dto) {
		Task existing = taskRepository.findById(id)
				.orElseThrow(() -> new TaskNotFoundException("Task not found"));

		if (dto.getTitle() != null) existing.setTitle(dto.getTitle());
		if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
		if (dto.getUserId() != null) existing.setUserId(dto.getUserId());

		Task updated = taskRepository.save(existing);
		return taskMapper.toDTO(updated);
	}

	@Loggable
	@MeasureTime
	@Transactional
	public void deleteTask(Long id) {
		if (!taskRepository.existsById(id)) {
			throw new TaskNotFoundException("Task not found");
		}
		taskRepository.deleteById(id);
	}

	@Loggable
	@MeasureTime
	public List<TaskResponseDTO> getAllTasks() {
		return taskRepository.findAll().stream()
				.map(taskMapper::toDTO)
				.toList();
	}
}