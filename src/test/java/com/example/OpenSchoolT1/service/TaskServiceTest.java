package com.example.OpenSchoolT1.service;

import com.example.OpenSchoolT1.dto.TaskRequestDTO;
import com.example.OpenSchoolT1.dto.TaskResponseDTO;
import com.example.OpenSchoolT1.entity.Task;
import com.example.OpenSchoolT1.entity.TaskStatus;
import com.example.OpenSchoolT1.exceptions.TaskNotFoundException;
import com.example.OpenSchoolT1.kafka.TaskStatusProducer;
import com.example.OpenSchoolT1.mapper.TaskMapper;
import com.example.OpenSchoolT1.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
	@Mock
	private TaskRepository taskRepository;

	@Mock
	private TaskMapper taskMapper;

	@Mock
	private TaskStatusProducer taskStatusProducer;

	@InjectMocks
	private TaskService taskService;

	@Test
	@DisplayName("Создание Task успешно")
	void createTask() {
		Task task = getExampleTask();
		TaskResponseDTO taskResponseDTO = getExampleTaskResponseDTO();
		TaskRequestDTO taskRequestDTO = getExampleTaskRequestDTO();

		when(taskMapper.toEntity(taskRequestDTO)).thenReturn(task);
		when(taskRepository.save(task)).thenReturn(task);
		when(taskMapper.toDTO(task)).thenReturn(taskResponseDTO);

		TaskResponseDTO result = taskService.createTask(taskRequestDTO);

		assertNotNull(result);
		assertEquals(taskResponseDTO.getId(), result.getId());
		assertEquals(taskResponseDTO.getTitle(), result.getTitle());
		assertEquals(taskResponseDTO.getDescription(), result.getDescription());
		assertEquals(taskResponseDTO.getUserId(), result.getUserId());
		assertEquals(taskResponseDTO.getStatus(), result.getStatus());

		verify(taskMapper, times(1)).toEntity(taskRequestDTO);
		verify(taskRepository, times(1)).save(task);
		verify(taskMapper, times(1)).toDTO(task);
	}

	@Test
	@DisplayName("Получение задачи по id успешно")
	void getTaskById_success() {
		Task task = getExampleTask();
		TaskResponseDTO dto = getExampleTaskResponseDTO();

		when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
		when(taskMapper.toDTO(task)).thenReturn(dto);

		TaskResponseDTO result = taskService.getTaskById(1L);

		assertNotNull(result);
		assertEquals(dto.getId(), result.getId());
		assertEquals(dto.getTitle(), result.getTitle());
		assertEquals(dto.getDescription(), result.getDescription());
		assertEquals(dto.getUserId(), result.getUserId());
		assertEquals(dto.getStatus(), result.getStatus());
		verify(taskRepository, times(1)).findById(1L);
		verify(taskMapper, times(1)).toDTO(task);
	}

	@Test
	@DisplayName("Получение несуществующей задачи должно выбросить исключение")
	void getTaskById_notFound() {
		when(taskRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
	}

	@Test
	@DisplayName("Обновление задачи с новым статусом успешно")
	void updateTask_statusChanged_success() {
		Task existing = getExampleTask();
		existing.setStatus(TaskStatus.valueOf("NEW"));

		TaskRequestDTO dto = getExampleTaskRequestDTO();
		dto.setStatus(TaskStatus.valueOf("DONE"));

		Task updated = new Task();
		updated.setId(1L);
		updated.setTitle(dto.getTitle());
		updated.setDescription(dto.getDescription());
		updated.setUserId(dto.getUserId());
		updated.setStatus(dto.getStatus());

		TaskResponseDTO responseDTO = getExampleTaskResponseDTO();
		responseDTO.setStatus(dto.getStatus());

		when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(taskRepository.save(existing)).thenReturn(updated);
		when(taskMapper.toDTO(updated)).thenReturn(responseDTO);

		TaskResponseDTO result = taskService.updateTask(1L, dto);

		assertNotNull(result);
		assertEquals(responseDTO.getId(), result.getId());
		assertEquals(responseDTO.getTitle(), result.getTitle());
		assertEquals(responseDTO.getDescription(), result.getDescription());
		assertEquals(responseDTO.getUserId(), result.getUserId());
		assertEquals(responseDTO.getStatus(), result.getStatus());
		verify(taskRepository, times(1)).save(existing);
		verify(taskStatusProducer, times(1)).sendTaskUpdate(1L, dto);
	}

	@Test
	@DisplayName("Обновление задачи без изменения статуса успешно")
	void updateTask_notStatusChanged_success() {
		Task existing = getExampleTask();
		TaskRequestDTO dto = getExampleTaskRequestDTO();

		Task updated = new Task();
		updated.setId(1L);
		updated.setTitle(dto.getTitle());
		updated.setDescription(dto.getDescription());
		updated.setUserId(dto.getUserId());
		updated.setStatus(dto.getStatus());

		TaskResponseDTO responseDTO = getExampleTaskResponseDTO();
		responseDTO.setStatus(dto.getStatus());

		when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(taskRepository.save(any())).thenReturn(updated);
		when(taskMapper.toDTO(any())).thenReturn(responseDTO);

		TaskResponseDTO result = taskService.updateTask(1L, dto);

		assertNotNull(result);
		assertEquals(responseDTO.getId(), result.getId());
		assertEquals(responseDTO.getTitle(), result.getTitle());
		assertEquals(responseDTO.getDescription(), result.getDescription());
		assertEquals(responseDTO.getUserId(), result.getUserId());
		assertEquals(responseDTO.getStatus(), result.getStatus());
		verify(taskRepository, times(1)).save(existing);
		verify(taskStatusProducer, never()).sendTaskUpdate(1L, dto);
	}

	@Test
	@DisplayName("Обновление несуществующей задачи должно выбросить исключение")
	void updateTask_notFound() {
		when(taskRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(1L, new TaskRequestDTO()));
	}

	@Test
	@DisplayName("Удаление задачи успешно")
	void deleteTask_success() {
		when(taskRepository.existsById(1L)).thenReturn(true);

		assertDoesNotThrow(() -> taskService.deleteTask(1L));
		verify(taskRepository, times(1)).deleteById(1L);
	}

	@Test
	@DisplayName("Удаление несуществующей задачи должно выбросить исключение")
	void deleteTask_notFound() {
		when(taskRepository.existsById(1L)).thenReturn(false);

		assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(1L));
	}

	@Test
	@DisplayName("Получение всех задач успешно")
	void getAllTasks_success() {
		List<Task> tasks = List.of(getExampleTask());
		List<TaskResponseDTO> dtos = List.of(getExampleTaskResponseDTO());

		when(taskRepository.findAll()).thenReturn(tasks);
		when(taskMapper.toDTO(any(Task.class))).thenReturn(dtos.get(0));

		List<TaskResponseDTO> result = taskService.getAllTasks();

		assertNotNull(result);
		assertEquals(dtos.size(), result.size());
		assertEquals(dtos.get(0).getId(), result.get(0).getId());
		assertEquals(dtos.get(0).getTitle(), result.get(0).getTitle());
		assertEquals(dtos.get(0).getDescription(), result.get(0).getDescription());
		assertEquals(dtos.get(0).getUserId(), result.get(0).getUserId());
		assertEquals(dtos.get(0).getStatus(), result.get(0).getStatus());
		verify(taskRepository, times(1)).findAll();
	}

	private Task getExampleTask() {
		Task example = new Task();
		example.setId(1L);
		example.setTitle("Title");
		example.setDescription("Description");
		example.setUserId(1L);
		example.setStatus(TaskStatus.valueOf("NEW"));
		return example;
	}

	private TaskRequestDTO getExampleTaskRequestDTO() {
		TaskRequestDTO example = new TaskRequestDTO();
		example.setTitle("Title");
		example.setDescription("Description");
		example.setUserId(1L);
		example.setStatus(TaskStatus.valueOf("NEW"));
		return example;
	}

	private TaskResponseDTO getExampleTaskResponseDTO() {
		TaskResponseDTO example = new TaskResponseDTO();
		example.setId(1L);
		example.setTitle("Title");
		example.setDescription("Description");
		example.setUserId(1L);
		example.setStatus(TaskStatus.valueOf("NEW"));
		return example;
	}
}