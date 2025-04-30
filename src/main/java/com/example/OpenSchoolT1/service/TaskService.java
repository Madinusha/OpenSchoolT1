package com.example.OpenSchoolT1.service;

import com.example.OpenSchoolT1.annotation.Loggable;
import com.example.OpenSchoolT1.annotation.MeasureTime;
import com.example.OpenSchoolT1.exceptions.TaskNotFoundException;
import com.example.OpenSchoolT1.repository.TaskRepository;
import com.example.OpenSchoolT1.entity.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TaskService {
	private final TaskRepository taskRepository;

	public TaskService(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	@Loggable
	@Transactional
	public Task createTask(Task task) {
		return taskRepository.save(task);
	}

	@Loggable
	public Task getTaskById(Long id) {
		return taskRepository.findById(id)
				.orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
	}

	@Loggable
	@MeasureTime
	@Transactional
	public Task updateTask(Long id, Task taskDetails) {
		Task existingTask = getTaskById(id);

		if (taskDetails.getTitle() != null) {
			existingTask.setTitle(taskDetails.getTitle());
		}
		if (taskDetails.getDescription() != null) {
			existingTask.setDescription(taskDetails.getDescription());
		}
		if (taskDetails.getUserId() != null) {
			existingTask.setUserId(taskDetails.getUserId());
		}

		return taskRepository.save(existingTask);
	}

	@Loggable
	@MeasureTime
	@Transactional
	public void deleteTask(Long id) {
		if (!taskRepository.existsById(id)) {
			throw new TaskNotFoundException("Task not found with id: " + id);
		}
		taskRepository.deleteById(id);
	}

	@Loggable
	@MeasureTime
	public List<Task> getAllTasks() {
		return taskRepository.findAll();
	}
}