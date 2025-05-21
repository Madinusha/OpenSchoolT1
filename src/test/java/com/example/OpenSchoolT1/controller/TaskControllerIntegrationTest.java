package com.example.OpenSchoolT1.controller;

import com.example.OpenSchoolT1.config.TestcontainerConfig;
import com.example.OpenSchoolT1.dto.TaskRequestDTO;
import com.example.OpenSchoolT1.dto.TaskResponseDTO;
import com.example.OpenSchoolT1.entity.Task;
import com.example.OpenSchoolT1.entity.TaskStatus;
import com.example.OpenSchoolT1.kafka.TaskStatusProducer;
import com.example.OpenSchoolT1.mapper.TaskMapper;
import com.example.OpenSchoolT1.repository.TaskRepository;
import com.example.OpenSchoolT1.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerIntegrationTest extends TestcontainerConfig {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TaskService taskService;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private TaskMapper taskMapper;

//	@MockBean
//	private TaskStatusProducer taskStatusProducer;
//	что-то я не придумала как переопределить поведение бина. Ошибка выскакивает,
//	но на прохождение тестов не влияет. Что использовать после версии spring-boot-test 3.4.0 ?

	@BeforeEach
	void setup() throws Exception {
		taskRepository.deleteAll();
	}

	@Test
	@DisplayName("Создание задачи - успешный сценарий")
	void createTask_shouldReturnCreatedTask() throws Exception {
		TaskRequestDTO request = getExampleTaskRequestDTO();

		TaskResponseDTO expectedResponse = getExampleTaskResponseDTO();

		mockMvc.perform(post("/tasks")
						.contentType("application/json")
						.content(getTaskRequestDTOJson(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.title").value(expectedResponse.getTitle()))
				.andExpect(jsonPath("$.description").value(expectedResponse.getDescription()))
				.andExpect(jsonPath("$.userId").value(expectedResponse.getUserId()))
				.andExpect(jsonPath("$.status").value(expectedResponse.getStatus().name()));
	}

	@Test
	@DisplayName("Создание задачи с невалидными данными - должен вернуть 400")
	void createTask_withInvalidData_returnsBadRequest() throws Exception {
		TaskRequestDTO invalidRequest = new TaskRequestDTO();
		invalidRequest.setTitle("");
		invalidRequest.setDescription("");
		invalidRequest.setUserId(null);
		invalidRequest.setStatus(TaskStatus.NEW);

		mockMvc.perform(post("/tasks")
						.contentType(MediaType.APPLICATION_JSON)
						.content(getTaskRequestDTOJson(invalidRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.title").value("Title cannot be blank"))
				.andExpect(jsonPath("$.errors.description").value("Description cannot be blank"))
				.andExpect(jsonPath("$.errors.userId").value("User ID cannot be null"));

		String invalidStatusJson = """
				{
				    "title": "Valid Title",
				    "description": "Valid Description",
				    "userId": 1,
				    "status": "INVALID_STATUS"
				}
				""";

		mockMvc.perform(post("/tasks")
						.contentType(MediaType.APPLICATION_JSON)
						.content(invalidStatusJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid status. Allowed values: NEW, IN_PROGRESS, DONE, CANCELLED"));
	}

	@Test
	@DisplayName("Получение задачи по ID - успешный сценарий")
	void getTaskById_returnsTask() throws Exception {
		TaskRequestDTO request = getExampleTaskRequestDTO();
		TaskResponseDTO createdTask = taskService.createTask(request);

		mockMvc.perform(get("/tasks/" + createdTask.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(createdTask.getId()))
				.andExpect(jsonPath("$.title").value(createdTask.getTitle()));
	}

	@Test
	@DisplayName("Получение несуществующей задачи - должен вернуть 404")
	void getTaskById_withNonExistingId_returnsNotFound() throws Exception {
		mockMvc.perform(get("/tasks/2003"))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Обновление статуса задачи")
	void updateTaskStatus() throws Exception {
		TaskRequestDTO createRequest = getExampleTaskRequestDTO();
		TaskResponseDTO createdTask = taskService.createTask(createRequest);

		TaskRequestDTO updateRequest = getExampleTaskRequestDTO();
		updateRequest.setStatus(TaskStatus.DONE);

//		doNothing().when(taskStatusProducer).sendTaskUpdate(anyLong(), any(TaskRequestDTO.class));

		mockMvc.perform(put("/tasks/" + createdTask.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(getTaskRequestDTOJson(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value(createdTask.getTitle()))
				.andExpect(jsonPath("$.description").value(createdTask.getDescription()))
				.andExpect(jsonPath("$.userId").value(createdTask.getUserId()))
				.andExpect(jsonPath("$.status").value(updateRequest.getStatus().name()));

//		verify(taskStatusProducer, times(1)).sendTaskUpdate(eq(createdTask.getId()), any(TaskRequestDTO.class));
	}


	@Test
	@DisplayName("Обновление несуществующей задачи - должен вернуть 404")
	void updateTask_withNonExistingId_returnsNotFound() throws Exception {
		TaskRequestDTO request = getExampleTaskRequestDTO();

		mockMvc.perform(put("/tasks/2003")
						.contentType(MediaType.APPLICATION_JSON)
						.content(getTaskRequestDTOJson(request)))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Удаление задачи - успешный сценарий")
	void deleteTask_returnsNoContent() throws Exception {
		TaskRequestDTO request = getExampleTaskRequestDTO();
		TaskResponseDTO createdTask = taskService.createTask(request);

		mockMvc.perform(delete("/tasks/" + createdTask.getId()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/tasks/" + createdTask.getId()))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Удаление несуществующей задачи - должен вернуть 404")
	void deleteTask_withNonExistingId_returnsNotFound() throws Exception {
		mockMvc.perform(delete("/tasks/9999"))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Получение всех задач - успешный сценарий")
	void getAllTasks_returnsTaskList() throws Exception {
		taskService.createTask(getExampleTaskRequestDTO());
		taskService.createTask(getExampleTaskRequestDTO());

		mockMvc.perform(get("/tasks"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2));
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

	private String getTaskRequestDTOJson(TaskRequestDTO taskRequestDTO) {
		return String.format("""
						{
							"title": "%s",
							"description": "%s",
							"userId": %s,
							"status": "%s"
						}
						""",
				taskRequestDTO.getTitle() != null ? taskRequestDTO.getTitle() : "",
				taskRequestDTO.getDescription() != null ? taskRequestDTO.getDescription() : "",
				taskRequestDTO.getUserId() != null ? taskRequestDTO.getUserId() : "null",
				taskRequestDTO.getStatus() != null ? taskRequestDTO.getStatus().name() : "null");
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
