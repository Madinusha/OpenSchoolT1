package com.example.OpenSchoolT1.controller;

import com.example.OpenSchoolT1.dto.TaskRequestDTO;
import com.example.OpenSchoolT1.dto.TaskResponseDTO;
import com.example.OpenSchoolT1.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.OpenSchoolT1Starter.annotation.HttpLog;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
	private final TaskService taskService;

	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	@HttpLog
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TaskResponseDTO createTask(@RequestBody @Valid TaskRequestDTO dto) {
		return taskService.createTask(dto);
	}

	@HttpLog
	@GetMapping("/{id}")
	public TaskResponseDTO getTaskById(@PathVariable Long id) {
		return taskService.getTaskById(id);
	}

	@HttpLog
	@PutMapping("/{id}")
	public TaskResponseDTO updateTask(@PathVariable Long id, @RequestBody @Valid TaskRequestDTO dto) {
		return taskService.updateTask(id, dto);
	}

	@HttpLog
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTask(@PathVariable Long id) {
		taskService.deleteTask(id);
	}

	@HttpLog
	@GetMapping
	public List<TaskResponseDTO> getAllTasks() {
		return taskService.getAllTasks();
	}
}