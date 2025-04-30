package com.example.OpenSchoolT1.controller;

import com.example.OpenSchoolT1.dto.TaskRequestDTO;
import com.example.OpenSchoolT1.dto.TaskResponseDTO;
import com.example.OpenSchoolT1.mapper.TaskMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.OpenSchoolT1.service.TaskService;
import com.example.OpenSchoolT1.entity.Task;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
	private final TaskService taskService;
	private final TaskMapper taskMapper;

	public TaskController(TaskService taskService, TaskMapper taskMapper) {
		this.taskService = taskService;
		this.taskMapper = taskMapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TaskResponseDTO createTask(@RequestBody TaskRequestDTO taskDTO) {
		Task task = taskMapper.toEntity(taskDTO);
		Task savedTask = taskService.createTask(task);
		return taskMapper.toDTO(savedTask);
	}

	@GetMapping("/{id}")
	public TaskResponseDTO getTaskById(@PathVariable Long id) {
		Task task = taskService.getTaskById(id);
		return taskMapper.toDTO(task);
	}

	@PutMapping("/{id}")
	public TaskResponseDTO updateTask(@PathVariable Long id, @RequestBody TaskRequestDTO taskDTO) {
		Task task = taskMapper.toEntity(taskDTO);
		Task updatedTask = taskService.updateTask(id, task);
		return taskMapper.toDTO(updatedTask);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTask(@PathVariable Long id) {
		taskService.deleteTask(id);
	}

	@GetMapping
	public List<TaskResponseDTO> getAllTasks() {
		List<Task> tasks = taskService.getAllTasks();
		return tasks.stream()
				.map(taskMapper::toDTO)
				.toList();
	}
}