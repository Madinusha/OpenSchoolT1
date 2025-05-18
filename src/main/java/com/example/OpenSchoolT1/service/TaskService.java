package com.example.OpenSchoolT1.service;

import com.example.OpenSchoolT1.annotation.Loggable;
import com.example.OpenSchoolT1.annotation.MeasureTime;
import com.example.OpenSchoolT1.dto.TaskRequestDTO;
import com.example.OpenSchoolT1.dto.TaskResponseDTO;
import com.example.OpenSchoolT1.exceptions.TaskNotFoundException;
import com.example.OpenSchoolT1.kafka.TaskStatusProducer;
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
	private final TaskStatusProducer taskStatusProducer;

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

		boolean statusChanged = !dto.getStatus().equals(existing.getStatus());

		existing.setTitle(dto.getTitle());
		existing.setDescription(dto.getDescription());
		existing.setUserId(dto.getUserId());
		existing.setStatus(dto.getStatus());

		Task updated = taskRepository.save(existing);
		if (statusChanged) {
			taskStatusProducer.sendTaskUpdate(id, dto);
		}

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