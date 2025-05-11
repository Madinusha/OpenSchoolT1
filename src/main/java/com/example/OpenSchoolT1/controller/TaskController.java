package com.example.OpenSchoolT1.controller;

import com.example.OpenSchoolT1.dto.TaskRequestDTO;
import com.example.OpenSchoolT1.dto.TaskResponseDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.OpenSchoolT1.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
	private final TaskService taskService;

	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TaskResponseDTO createTask(@RequestBody TaskRequestDTO dto) {
		return taskService.createTask(dto);
	}

	@GetMapping("/{id}")
	public TaskResponseDTO getTaskById(@PathVariable Long id) {
		return taskService.getTaskById(id);
	}

	@PutMapping("/{id}")
	public TaskResponseDTO updateTask(@PathVariable Long id, @RequestBody TaskRequestDTO dto) {
		return taskService.updateTask(id, dto);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTask(@PathVariable Long id) {
		taskService.deleteTask(id);
	}

	@GetMapping
	public List<TaskResponseDTO> getAllTasks() {
		return taskService.getAllTasks();
	}
}